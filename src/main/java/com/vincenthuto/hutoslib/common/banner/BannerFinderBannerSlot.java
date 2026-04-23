package com.vincenthuto.hutoslib.common.banner;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.network.PacketBannerChange;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class BannerFinderBannerSlot extends BannerFinder {

private static class EquipmentSlotBannerGetter implements BannerGetter {
private final LivingEntity player;
private final EquipmentSlot slot;

private EquipmentSlotBannerGetter(LivingEntity player, EquipmentSlot slot) {
this.player = player;
this.slot = slot;
}

@Override
public ItemStack getBanner() { return player.getItemBySlot(slot); }

@Override
public void setBanner(ItemStack stack) { player.setItemSlot(slot, stack); }

@Override
public void syncToClients() {
// Vanilla equipment sync handles this path.
}
}

private static class ExtensionSlotBannerGetter implements BannerGetter {
@SuppressWarnings("unused")
private final LivingEntity player;
private final BannerSlotItemHandler slot;

private ExtensionSlotBannerGetter(LivingEntity player, BannerSlotItemHandler slot) {
this.player = player;
this.slot = slot;
}

@Override
public ItemStack getBanner() { return slot.getContents(); }

@Override
public boolean isHidden() { return false; }

@Override
public void setBanner(ItemStack stack) { slot.setContents(stack); }

@Override
public void syncToClients() {
LivingEntity thePlayer = slot.getContainer().getOwner();
if (thePlayer.level().isClientSide) return;
PacketBannerChange message = new PacketBannerChange(thePlayer, "banner_slot", new JsonPrimitive(0),
slot.getContents());
PacketDistributor.sendToPlayersTrackingEntityAndSelf(thePlayer, message);
}
}

public static void initFinder() {
BannerFinder.addFinder(new BannerFinderBannerSlot());
}

@Override
public Optional<? extends BannerGetter> findStack(LivingEntity player, boolean allowCosmetic) {
BannerExtensionSlot ext = BannerExtensionSlot.get(player);
Optional<? extends BannerGetter> fromBannerSlot = ext.getSlots().stream()
.filter(slot -> slot.getContents().getItem() instanceof ItemArmBanner)
.map(slot -> new ExtensionSlotBannerGetter(player, slot))
.findFirst();

if (fromBannerSlot.isPresent() || !allowCosmetic) {
return fromBannerSlot;
}

ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
if (chest.getItem() instanceof ItemArmBanner) {
return Optional.of(new EquipmentSlotBannerGetter(player, EquipmentSlot.CHEST));
}

return Optional.empty();
}

@Override
public String getName() { return "banner_slot"; }

@Override
protected Optional<BannerGetter> getSlotFromId(Player player, JsonElement packetData) {
BannerExtensionSlot ext = BannerExtensionSlot.get(player);
return Optional.ofNullable(ext.getSlots().get(packetData.getAsInt()))
.map(slot -> new ExtensionSlotBannerGetter(player, slot));
}
}
