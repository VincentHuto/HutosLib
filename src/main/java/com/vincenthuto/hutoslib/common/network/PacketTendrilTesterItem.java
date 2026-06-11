package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemTendrilTester;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectSpawner;
import com.vincenthuto.hutoslib.common.tendril.TendrilTesterOrigin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketTendrilTesterItem(InteractionHand hand, boolean save, boolean test, TendrilEffectConfig config)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketTendrilTesterItem> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_tendril_tester_item"));

	public static final StreamCodec<FriendlyByteBuf, PacketTendrilTesterItem> CODEC =
			StreamCodec.of(PacketTendrilTesterItem::encode, PacketTendrilTesterItem::new);

	public PacketTendrilTesterItem(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readBoolean(), buf.readBoolean(),
				TendrilEffectConfig.fromBuffer(buf));
	}

	public static void handle(PacketTendrilTesterItem msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			ItemStack stack = player.getItemInHand(msg.hand());
			if (!(stack.getItem() instanceof ItemTendrilTester)) {
				return;
			}
			TendrilEffectConfig config = msg.config().clamped();
			if (msg.save()) {
				config.writeToItem(stack);
			}
			if (msg.test()) {
				Vec3 start = player.getEyePosition();
				TendrilEffectSpawner.spawn(level, (ServerPlayer) player,
						TendrilTesterOrigin.playerHandPoint(player, msg.hand()),
						new TendrilAnchor.Point(start.add(player.getLookAngle().scale(config.range()))), config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketTendrilTesterItem msg) {
		buf.writeEnum(msg.hand());
		buf.writeBoolean(msg.save());
		buf.writeBoolean(msg.test());
		msg.config().toBuffer(buf);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
