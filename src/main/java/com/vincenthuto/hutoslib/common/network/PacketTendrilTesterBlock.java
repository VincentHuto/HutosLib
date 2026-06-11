package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.entity.TendrilTesterBlockEntity;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketTendrilTesterBlock(BlockPos pos, boolean save, boolean test, TendrilEffectConfig config)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketTendrilTesterBlock> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_tendril_tester_block"));

	public static final StreamCodec<FriendlyByteBuf, PacketTendrilTesterBlock> CODEC =
			StreamCodec.of(PacketTendrilTesterBlock::encode, PacketTendrilTesterBlock::new);

	public PacketTendrilTesterBlock(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean(), TendrilEffectConfig.fromBuffer(buf));
	}

	public static void handle(PacketTendrilTesterBlock msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			if (!(level.getBlockEntity(msg.pos()) instanceof TendrilTesterBlockEntity blockEntity)) {
				return;
			}
			TendrilEffectConfig config = msg.config().clamped();
			if (msg.save()) {
				blockEntity.setConfig(config);
			}
			if (msg.test()) {
				Vec3 start = Vec3.atCenterOf(msg.pos());
				TendrilEffectSpawner.spawn(level, (ServerPlayer) player, new TendrilAnchor.Point(start),
						new TendrilAnchor.Point(start.add(config.targetOffset())), config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketTendrilTesterBlock msg) {
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
