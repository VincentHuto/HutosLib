package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketKarmaClient implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketKarmaClient> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_karma_client"));

public static final StreamCodec<FriendlyByteBuf, PacketKarmaClient> CODEC = StreamCodec.of(
PacketKarmaClient::encode, PacketKarmaClient::decode);

public static PacketKarmaClient decode(final FriendlyByteBuf buf) {
return new PacketKarmaClient();
}

public static void encode(final PacketKarmaClient msg, final FriendlyByteBuf buf) {
}

public static void handle(final PacketKarmaClient msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
ServerPlayer sender = ctx.sender();
if (sender != null) {
IKarma volume = sender.getData(HLAttachmentTypes.KARMA.get());
PacketDistributor.sendToPlayer(sender, new PacketKarmaServer(volume));
}
});
}

public PacketKarmaClient() {
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
