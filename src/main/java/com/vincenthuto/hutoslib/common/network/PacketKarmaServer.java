package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketKarmaServer implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketKarmaServer> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_karma_server"));

public static final StreamCodec<FriendlyByteBuf, PacketKarmaServer> CODEC = StreamCodec.of(
PacketKarmaServer::encode, PacketKarmaServer::decode);

public static PacketKarmaServer decode(final FriendlyByteBuf buf) {
return new PacketKarmaServer(buf.readBoolean(), buf.readFloat());
}

public static void encode(final PacketKarmaServer msg, final FriendlyByteBuf buf) {
buf.writeBoolean(msg.active);
buf.writeFloat(msg.volume);
}

public static void handle(final PacketKarmaServer msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
if (Minecraft.getInstance().player != null) {
IKarma capa = Minecraft.getInstance().player.getData(HLAttachmentTypes.KARMA.get());
capa.setActive(msg.active);
capa.setKarma(msg.volume);
}
});
}

private final boolean active;
private final float volume;

public PacketKarmaServer(boolean active, float volumeIn) {
this.active = active;
this.volume = volumeIn;
}

public PacketKarmaServer(IKarma volume) {
this.active = volume.isActive();
this.volume = volume.getKarma();
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
