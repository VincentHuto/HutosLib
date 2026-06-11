package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.entity.GenericParticleTesterBlockEntity;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTesterSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketGenericParticleTesterBlock(BlockPos pos, boolean save, boolean test,
		GenericParticleTestConfig config) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketGenericParticleTesterBlock> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_generic_particle_tester_block"));

	public static final StreamCodec<FriendlyByteBuf, PacketGenericParticleTesterBlock> CODEC =
			StreamCodec.of(PacketGenericParticleTesterBlock::encode, PacketGenericParticleTesterBlock::new);

	public PacketGenericParticleTesterBlock(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean(), GenericParticleTestConfig.fromBuffer(buf));
	}

	public static void handle(PacketGenericParticleTesterBlock msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			if (!(level.getBlockEntity(msg.pos()) instanceof GenericParticleTesterBlockEntity blockEntity)) {
				return;
			}
			GenericParticleTestConfig config = msg.config().clamped();
			if (msg.save()) {
				blockEntity.setConfig(config);
			}
			if (msg.test()) {
				GenericParticleTesterSpawner.spawn(level, GenericParticleTesterBlockEntity.spawnPos(msg.pos()),
						config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketGenericParticleTesterBlock msg) {
		buf.writeBlockPos(msg.pos());
		buf.writeBoolean(msg.save());
		buf.writeBoolean(msg.test());
		msg.config().toBuffer(buf);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
