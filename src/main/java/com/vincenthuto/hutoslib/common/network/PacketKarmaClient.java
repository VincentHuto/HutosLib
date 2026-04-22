package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaProvider;

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
(buf, msg) -> msg.encode(buf), PacketKarmaClient::new);

public static void handle(final PacketKarmaClient msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
if (ctx.player() instanceof ServerPlayer sender) {
IKarma karmaState = KarmaProvider.getKarma(sender);
PacketDistributor.sendToPlayer(sender, new PacketKarmaServer(karmaState));
}
});
}

public PacketKarmaClient() {
}

public PacketKarmaClient(FriendlyByteBuf buf) {
}

public void encode(FriendlyByteBuf buf) {
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
