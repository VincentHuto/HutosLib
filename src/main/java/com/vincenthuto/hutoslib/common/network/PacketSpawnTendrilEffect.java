package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.TendrilRenderer;
import com.vincenthuto.hutoslib.client.particle.data.TendrilEffectData;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSpawnTendrilEffect(TendrilAnchor start, TendrilAnchor end, TendrilEffectConfig config)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketSpawnTendrilEffect> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_spawn_tendril_effect"));

	public static final StreamCodec<FriendlyByteBuf, PacketSpawnTendrilEffect> CODEC =
			StreamCodec.of(PacketSpawnTendrilEffect::encode, PacketSpawnTendrilEffect::new);

	public PacketSpawnTendrilEffect(FriendlyByteBuf buf) {
		this(TendrilAnchor.fromBuffer(buf), TendrilAnchor.fromBuffer(buf), TendrilEffectConfig.fromBuffer(buf));
	}

	public static void handle(PacketSpawnTendrilEffect msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			TendrilEffectConfig config = msg.config().clamped();
			long seed = config.fixedSeed() ? config.seed() : System.nanoTime();
			TendrilRenderer.INSTANCE.add(new TendrilEffectData(msg.start(), msg.end(), config, seed), 0.0F);
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketSpawnTendrilEffect msg) {
		TendrilAnchor.toBuffer(buf, msg.start());
		TendrilAnchor.toBuffer(buf, msg.end());
		msg.config().toBuffer(buf);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
