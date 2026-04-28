package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketKarmaServer implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketKarmaServer> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_karma_server"));

public static final StreamCodec<FriendlyByteBuf, PacketKarmaServer> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketKarmaServer::new);

public static void handle(final PacketKarmaServer msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
Player player = ctx.player();
if (player != null) {
IKarma karmaState = KarmaProvider.getKarma(player);
karmaState.setActive(msg.active);
karmaState.setKarma(msg.karma);
}
});
}

private final boolean active;
private final float karma;

public PacketKarmaServer(FriendlyByteBuf buf) {
this.active = buf.readBoolean();
this.karma = buf.readFloat();
}

public PacketKarmaServer(boolean active, float karma) {
this.active = active;
this.karma = karma;
}

public PacketKarmaServer(IKarma karmaState) {
this.active = karmaState.isActive();
this.karma = karmaState.getKarma();
}

public void encode(FriendlyByteBuf buf) {
buf.writeBoolean(active);
buf.writeFloat(karma);
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
