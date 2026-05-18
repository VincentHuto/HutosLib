package com.vincenthuto.hutoslib.common.container;

import com.google.common.collect.ImmutableList;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketSyncBannerSlotContents;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = HutosLib.MOD_ID)
public class BannerExtensionSlot implements IBannerContainer, ValueIOSerializable {

    public static final String BANNER_KEY = "hutoslib:banner";
    private LivingEntity owner;
    private boolean restoring;

    public BannerExtensionSlot(LivingEntity owner) {
        this.owner = owner;
    }

    @SubscribeEvent
    public static void entityTick(PlayerTickEvent.Post event) {
        get(event.getEntity()).tickAllSlots();
    }

    @SubscribeEvent
    public static void joinWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player target = event.getEntity();
        if (target.level().isClientSide()) return;
        get(target).syncToTracking();
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            get(player).syncToTracking();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player target = event.getEntity();
        if (target.level().isClientSide()) return;
        get(target).syncToTracking();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            get(player).syncToTracking();
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        BannerExtensionSlot oldSlot = get(oldPlayer);
        BannerExtensionSlot newSlot = get(newPlayer);
        ItemStack stack = oldSlot.getBanner().getContents();
        newSlot.getBanner().setContents(stack.copy());
        newSlot.syncToTracking();
    }

    @SubscribeEvent
    public static void playerDeath(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player) && !(entity instanceof ArmorStand)) return;

        BannerExtensionSlot instance = get(entity);
        BannerSlotItemHandler banner = instance.getBanner();
        ItemStack stack = banner.getContents();
        if (hasVanishingCurse(stack)) {
            stack = ItemStack.EMPTY;
            banner.setContents(stack);
        }
        if (stack.getCount() > 0) {
            if (entity instanceof Player player) {
                if (entity.level() instanceof ServerLevel serverLevel
                        && !serverLevel.getGameRules().get(GameRules.KEEP_INVENTORY)
                        && !player.isSpectator()) {
                    Collection<ItemEntity> old = entity.captureDrops(event.getDrops());
                    player.drop(stack, true, false);
                    entity.captureDrops(old);
                    banner.setContents(ItemStack.EMPTY);
                }
            } else {
                if (entity.level() instanceof ServerLevel serverLevel) {
                    entity.spawnAtLocation(serverLevel, stack);
                }
                banner.setContents(ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void track(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target.level().isClientSide()) return;
        if (target instanceof Player playerTarget) {
            get(playerTarget).syncTo(event.getEntity());
        }
    }

    public static BannerExtensionSlot get(LivingEntity entity) {
        BannerExtensionSlot slot = entity.getData(HLAttachmentTypes.BANNER_SLOT.get());
        if (slot.owner == null) {
            slot.owner = entity;
        }
        return slot;
    }    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (restoring) return;
            banner.onContentsChanged();
        }
    };

    private static boolean hasVanishingCurse(ItemStack stack) {
        var enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null) return false;
        return enchantments.keySet().stream()
                .anyMatch(h -> h.is(Enchantments.VANISHING_CURSE));
    }    private final BannerSlotItemHandler banner = new BannerSlotItemHandler(this,
            Identifier.fromNamespaceAndPath("hutoslib", "banner"), inventory, 0);

    @Override
    public void deserialize(ValueInput input) {
        ItemStack stack = input.read(BANNER_KEY, ItemStack.OPTIONAL_CODEC)
                .orElseGet(() -> input.listOrEmpty("Items", ItemStackWithSlot.CODEC).stream()
                        .filter(item -> item.slot() == 0)
                        .map(ItemStackWithSlot::stack)
                        .findFirst()
                        .orElse(ItemStack.EMPTY));
        setContentsFromStorage(stack);
    }    private final ImmutableList<BannerSlotItemHandler> slots = ImmutableList.of(banner);

    @Nonnull
    public BannerSlotItemHandler getBanner() {
        return banner;
    }

    @Nonnull
    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Nonnull
    @Override
    public ImmutableList<BannerSlotItemHandler> getSlots() {
        return slots;
    }

    @Override
    public void onContentsChanged(BannerSlotItemHandler slot) {
        if (owner != null && !owner.level().isClientSide()) {
            syncToTracking();
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        ItemStack stack = banner.getContents();
        if (!stack.isEmpty()) {
            output.store(BANNER_KEY, ItemStack.OPTIONAL_CODEC, stack);
        }
    }

    public void setAll(NonNullList<ItemStack> stacks) {
        List<BannerSlotItemHandler> slotList = getSlots();
        for (int i = 0; i < slotList.size(); i++) {
            ItemStack stack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            slotList.get(i).setContents(stack.copy());
        }
    }

    public void syncToTracking() {
        if (owner == null || owner.level().isClientSide()) return;
        if (!(owner instanceof Player playerOwner)) return;
        PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents(playerOwner, this);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(owner, message);
    }

    public void syncTo(Player target) {
        if (owner == null || owner.level().isClientSide()) return;
        if (!(owner instanceof Player playerOwner)) return;
        if (!(target instanceof ServerPlayer serverTarget)) return;
        PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents(playerOwner, this);
        PacketDistributor.sendToPlayer(serverTarget, message);
    }

    public void syncToSelf() {
        if (owner instanceof Player player) {
            syncTo(player);
        }
    }

    private void setContentsFromStorage(ItemStack stack) {
        restoring = true;
        try {
            inventory.setStackInSlot(0, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        } finally {
            restoring = false;
        }
    }

    private void tickAllSlots() {
        for (BannerSlotItemHandler slot : slots) {
            slot.onWornTick();
        }
    }






}
