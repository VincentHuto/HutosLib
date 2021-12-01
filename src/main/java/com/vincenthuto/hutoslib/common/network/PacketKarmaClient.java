package com.vincenthuto.hutoslib.common.network;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class PacketKarmaClient {
	public PacketKarmaClient() {

	}

	public static void handle(final PacketKarmaClient msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender(); // the client that sent this packet
			if (sender != null) {
				IKarma volume = sender.getCapability(KarmaProvider.KARMA_CAPA)
						.orElseThrow(IllegalStateException::new);
				HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> sender),
						new PacketKarmaServer(volume));
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void encode(final PacketKarmaClient msg, final FriendlyByteBuf packetBuffer) {

	}

	public static PacketKarmaClient decode(final FriendlyByteBuf packetBuffer) {
		return new PacketKarmaClient();
	}
}