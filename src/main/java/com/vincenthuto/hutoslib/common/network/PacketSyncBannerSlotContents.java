package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSyncBannerSlotContents implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketSyncBannerSlotContents> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_sync_banner_slot"));

public static final StreamCodec<FriendlyByteBuf, PacketSyncBannerSlotContents> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketSyncBannerSlotContents::new);

public final NonNullList<ItemStack> stacks = NonNullList.create();
public int entityId;

public PacketSyncBannerSlotContents(FriendlyByteBuf buf) {
entityId = buf.readVarInt();
int numStacks = buf.readVarInt();
for (int i = 0; i < numStacks; i++) {
stacks.add(buf.readItem());
}
}

public PacketSyncBannerSlotContents(Player player, BannerExtensionSlot extension) {
this.entityId = player.getId();
extension.getSlots().stream().map(BannerSlotItemHandler::getContents).forEach(stacks::add);
}

public void encode(FriendlyByteBuf buf) {
buf.writeVarInt(entityId);
buf.writeVarInt(stacks.size());
for (ItemStack stack : stacks) {
buf.writeItem(stack);
}
}

public static void handle(PacketSyncBannerSlotContents msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
Minecraft minecraft = Minecraft.getInstance();
Entity entity = minecraft.level.getEntity(msg.entityId);
if (entity instanceof LivingEntity living && living instanceof Player) {
BannerExtensionSlot.get(living).setAll(msg.stacks);
}
});
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
