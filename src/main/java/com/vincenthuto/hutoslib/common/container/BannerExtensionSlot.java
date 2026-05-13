package com.vincenthuto.hutoslib.common.container;

import com.google.common.collect.ImmutableList;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketSyncBannerSlotContents;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = HutosLib.MOD_ID)
public class BannerExtensionSlot implements IBannerContainer {

@SubscribeEvent
public static void entityTick(PlayerTickEvent.Post event) {
get(event.getEntity()).tickAllSlots();
}

@SubscribeEvent
public static void joinWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
Player target = event.getEntity();
if (target.level().isClientSide()) return;
get(target).syncToSelf();
}

@SubscribeEvent
public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
Player target = event.getEntity();
if (target.level().isClientSide()) return;
get(target).syncToSelf();
}

@SubscribeEvent
public static void playerClone(PlayerEvent.Clone event) {
Player oldPlayer = event.getOriginal();
Player newPlayer = event.getEntity();
BannerExtensionSlot oldSlot = get(oldPlayer);
BannerExtensionSlot newSlot = get(newPlayer);
ItemStack stack = oldSlot.getBanner().getContents();
newSlot.getBanner().setContents(stack.copy());
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
}

public static final String BANNER_KEY = "hutoslib:banner";

private LivingEntity owner;

private final ItemStackHandler inventory = new ItemStackHandler(1) {
@Override
protected void onContentsChanged(int slot) {
super.onContentsChanged(slot);
banner.onContentsChanged();
}
};

private final BannerSlotItemHandler banner = new BannerSlotItemHandler(this,
Identifier.fromNamespaceAndPath("hutoslib", "banner"), inventory, 0);

private final ImmutableList<BannerSlotItemHandler> slots = ImmutableList.of(banner);

public BannerExtensionSlot(LivingEntity owner) {
this.owner = owner;
}

	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
	}

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

	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		return new CompoundTag();
	}

public void setAll(NonNullList<ItemStack> stacks) {
List<BannerSlotItemHandler> slotList = getSlots();
for (int i = 0; i < slotList.size(); i++) {
slotList.get(i).setContents(stacks.get(i));
}
}

protected void syncToTracking() {
if (!(owner instanceof Player playerOwner)) return;
PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents(playerOwner, this);
PacketDistributor.sendToPlayersTrackingEntityAndSelf(owner, message);
}

protected void syncTo(Player target) {
if (!(owner instanceof Player playerOwner)) return;
if (!(target instanceof ServerPlayer serverTarget)) return;
PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents(playerOwner, this);
PacketDistributor.sendToPlayer(serverTarget, message);
}

protected void syncToSelf() {
if (owner instanceof Player player) {
syncTo(player);
}
}

private void tickAllSlots() {
for (BannerSlotItemHandler slot : slots) {
slot.onWornTick();
}
}

private static boolean hasVanishingCurse(ItemStack stack) {
var enchantments = stack.get(DataComponents.ENCHANTMENTS);
if (enchantments == null) return false;
return enchantments.keySet().stream()
    .anyMatch(h -> h.is(Enchantments.VANISHING_CURSE));
}
}
