package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketContainerSlot implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketContainerSlot> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_container_slot"));

public static final StreamCodec<FriendlyByteBuf, PacketContainerSlot> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketContainerSlot::new);

public PacketContainerSlot() {
}

public PacketContainerSlot(FriendlyByteBuf buf) {
}

public void encode(FriendlyByteBuf buf) {
}

public static void handle(PacketContainerSlot msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
Player sender = ctx.player();
if (sender != null) {
sender.containerMenu.sendAllDataToRemote();
}
});
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
