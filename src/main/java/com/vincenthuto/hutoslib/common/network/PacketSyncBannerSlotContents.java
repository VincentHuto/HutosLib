package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSyncBannerSlotContents implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketSyncBannerSlotContents> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_sync_banner_slot"));

public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncBannerSlotContents> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketSyncBannerSlotContents::new);

public final NonNullList<ItemStack> stacks = NonNullList.create();
public int entityId;

public PacketSyncBannerSlotContents(RegistryFriendlyByteBuf buf) {
entityId = buf.readVarInt();
int numStacks = buf.readVarInt();
for (int i = 0; i < numStacks; i++) {
stacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
}
}

public PacketSyncBannerSlotContents(Player player, BannerExtensionSlot extension) {
this.entityId = player.getId();
extension.getSlots().stream().map(BannerSlotItemHandler::getContents).map(ItemStack::copy).forEach(stacks::add);
}

public void encode(RegistryFriendlyByteBuf buf) {
buf.writeVarInt(entityId);
buf.writeVarInt(stacks.size());
for (ItemStack stack : stacks) {
ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
}
}

public static void handle(PacketSyncBannerSlotContents msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
Player receiver = ctx.player();
if (receiver == null) return;
var level = receiver.level();
if (level == null) return;
Entity entity = level.getEntity(msg.entityId);
if (!(entity instanceof Player) && receiver.getId() == msg.entityId) {
entity = receiver;
}
if (entity instanceof Player player) {
BannerExtensionSlot.get(player).setAll(msg.stacks);
}
});
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
