package com.vincenthuto.hutoslib.common.container;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketSyncBannerSlotContents;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.NEOFORGE)
public class BannerExtensionSlot implements IBannerContainer, INBTSerializable<CompoundTag> {

@SubscribeEvent
public static void entityTick(PlayerTickEvent.Post event) {
get(event.getEntity()).tickAllSlots();
}

@SubscribeEvent
public static void joinWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
Player target = event.getEntity();
if (target.level().isClientSide) return;
get(target).syncToSelf();
}

@SubscribeEvent
public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
Player target = event.getEntity();
if (target.level().isClientSide) return;
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
if (!entity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
&& !player.isSpectator()) {
Collection<ItemEntity> old = entity.captureDrops(event.getDrops());
player.drop(stack, true, false);
entity.captureDrops(old);
banner.setContents(ItemStack.EMPTY);
}
} else {
entity.spawnAtLocation(stack);
banner.setContents(ItemStack.EMPTY);
}
}
}

@SubscribeEvent
public static void track(PlayerEvent.StartTracking event) {
Entity target = event.getTarget();
if (target.level().isClientSide) return;
if (target instanceof LivingEntity living) {
get(living).syncToSelf();
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
new ResourceLocation("hutoslib", "banner"), inventory, 0);

private final ImmutableList<BannerSlotItemHandler> slots = ImmutableList.of(banner);

public BannerExtensionSlot(LivingEntity owner) {
this.owner = owner;
}

@Override
public void deserializeNBT(CompoundTag nbt) {
inventory.deserializeNBT(nbt);
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
if (owner != null && !owner.level().isClientSide) {
syncToTracking();
}
}

@Override
public CompoundTag serializeNBT() {
return inventory.serializeNBT();
}

public void setAll(NonNullList<ItemStack> stacks) {
List<BannerSlotItemHandler> slotList = getSlots();
for (int i = 0; i < slotList.size(); i++) {
slotList.get(i).setContents(stacks.get(i));
}
}

protected void syncToTracking() {
if (owner == null) return;
PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents((Player) owner, this);
PacketDistributor.sendToPlayersTrackingEntityAndSelf(owner, message);
}

protected void syncTo(Player target) {
PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents((Player) owner, this);
PacketDistributor.sendToPlayer((ServerPlayer) target, message);
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
