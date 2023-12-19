package com.vincenthuto.hutoslib.common.network;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketKarmaServer {
	public static PacketKarmaServer decode(final FriendlyByteBuf packetBuffer) {
		return new PacketKarmaServer(packetBuffer.readBoolean(), packetBuffer.readFloat());
	}
	public static void encode(final PacketKarmaServer msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBoolean(msg.active);
		packetBuffer.writeFloat(msg.volume);
	}

	public static void handle(final PacketKarmaServer msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {

			if (Minecraft.getInstance().player != null) {
				IKarma capa = Minecraft.getInstance().player.getCapability(KarmaProvider.KARMA_CAPA)
						.orElseThrow(NullPointerException::new);
				capa.setActive(msg.active);
				capa.setKarma(msg.volume);
			}

		});
		ctx.get().setPacketHandled(true);
	}

	private boolean active;

	private float volume;

	public PacketKarmaServer(boolean active, float volumeIn) {
		this.active = active;
		this.volume = volumeIn;
	}

	public PacketKarmaServer(IKarma volume) {
		this.active = volume.isActive();
		this.volume = volume.getKarma();
	}
}