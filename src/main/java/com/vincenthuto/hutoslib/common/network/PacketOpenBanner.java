package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketOpenBanner implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketOpenBanner> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_open_banner"));

public static final StreamCodec<FriendlyByteBuf, PacketOpenBanner> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketOpenBanner::new);

public PacketOpenBanner() {
}

public PacketOpenBanner(FriendlyByteBuf buf) {
}

public void encode(FriendlyByteBuf buf) {
}

public static void handle(PacketOpenBanner msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
ServerPlayer sender = ctx.sender();
if (sender != null) {
sender.openMenu(new SimpleMenuProvider(
(i, playerInventory, playerEntity) -> new BannerSlotContainer(i, playerInventory),
Component.translatable("container.crafting")));
}
});
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
