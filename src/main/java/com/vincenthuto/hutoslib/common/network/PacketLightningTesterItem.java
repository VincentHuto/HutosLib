package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemLightningTester;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.lightning.LightningTesterSpawner;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketLightningTesterItem(InteractionHand hand, boolean save, boolean test, LightningTestConfig config)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketLightningTesterItem> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_lightning_tester_item"));

	public static final StreamCodec<FriendlyByteBuf, PacketLightningTesterItem> CODEC =
			StreamCodec.of(PacketLightningTesterItem::encode, PacketLightningTesterItem::new);

	public PacketLightningTesterItem(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readBoolean(), buf.readBoolean(),
				LightningTestConfig.fromBuffer(buf));
	}

	public static void handle(PacketLightningTesterItem msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			ItemStack stack = player.getItemInHand(msg.hand());
			if (!(stack.getItem() instanceof ItemLightningTester)) {
				return;
			}
			LightningTestConfig config = msg.config().clamped();
			if (msg.save()) {
				config.writeToItem(stack);
			}
			if (msg.test()) {
				Vec3 start = player.getEyePosition();
				Vec3 end = start.add(player.getLookAngle().scale(config.range()));
				LightningTesterSpawner.spawn(level, (ServerPlayer) player, start, end, config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketLightningTesterItem msg) {
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
