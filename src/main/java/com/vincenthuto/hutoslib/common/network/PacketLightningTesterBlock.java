package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.entity.LightningTesterBlockEntity;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.lightning.LightningTesterSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketLightningTesterBlock(BlockPos pos, boolean save, boolean test, LightningTestConfig config)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketLightningTesterBlock> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_lightning_tester_block"));

	public static final StreamCodec<FriendlyByteBuf, PacketLightningTesterBlock> CODEC =
			StreamCodec.of(PacketLightningTesterBlock::encode, PacketLightningTesterBlock::new);

	public PacketLightningTesterBlock(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean(), LightningTestConfig.fromBuffer(buf));
	}

	public static void handle(PacketLightningTesterBlock msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			if (!(level.getBlockEntity(msg.pos()) instanceof LightningTesterBlockEntity blockEntity)) {
				return;
			}
			LightningTestConfig config = msg.config().clamped();
			if (msg.save()) {
				blockEntity.setConfig(config);
			}
			if (msg.test()) {
				Vec3 start = Vec3.atCenterOf(msg.pos());
				LightningTesterSpawner.spawn(level, (ServerPlayer) player, start, start.add(config.targetOffset()),
						config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketLightningTesterBlock msg) {
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
