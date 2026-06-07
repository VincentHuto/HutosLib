package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.lightning.LightningTestBoltFactory;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSpawnLightningTest(Vec3 start, Vec3 end, LightningTestConfig config) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketSpawnLightningTest> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_spawn_lightning_test"));

	public static final StreamCodec<FriendlyByteBuf, PacketSpawnLightningTest> CODEC =
			StreamCodec.of(PacketSpawnLightningTest::encode, PacketSpawnLightningTest::new);

	public PacketSpawnLightningTest(FriendlyByteBuf buf) {
		this(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
				new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), LightningTestConfig.fromBuffer(buf));
	}

	public static void handle(PacketSpawnLightningTest msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			LightningTestConfig config = msg.config().clamped();
			if (config.backend() == LightningTestConfig.Backend.PARTICLE) {
				var level = Minecraft.getInstance().level;
				if (level == null) {
					return;
				}
				level.addParticle(LightningParticleFactory.createData(toParticleColor(config.colorPreset()),
						config.speed(), config.maxAge(), config.fract(), config.maxOffset()), msg.start().x,
						msg.start().y, msg.start().z, msg.end().x, msg.end().y, msg.end().z);
				return;
			}
			long seed = config.fixedSeed() ? config.seed() : System.nanoTime();
			BoltRenderer.INSTANCE.add(LightningTestBoltFactory.create(msg.start(), msg.end(), seed, config.outerColor(),
					config.size(), config), 0.0F);
			BoltRenderer.INSTANCE.add(LightningTestBoltFactory.create(msg.start(), msg.end(), seed, config.innerColor(),
					Math.max(0.01F, config.size() * 0.45F), config), 0.0F);
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketSpawnLightningTest msg) {
		buf.writeDouble(msg.start().x);
		buf.writeDouble(msg.start().y);
		buf.writeDouble(msg.start().z);
		buf.writeDouble(msg.end().x);
		buf.writeDouble(msg.end().y);
		buf.writeDouble(msg.end().z);
		msg.config().toBuffer(buf);
	}

	private static ParticleColor toParticleColor(int color) {
		return new ParticleColor((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
