package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemGenericParticleTester;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTesterSpawner;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketGenericParticleTesterItem(InteractionHand hand, boolean save, boolean test,
		GenericParticleTestConfig config) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketGenericParticleTesterItem> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_generic_particle_tester_item"));

	public static final StreamCodec<FriendlyByteBuf, PacketGenericParticleTesterItem> CODEC =
			StreamCodec.of(PacketGenericParticleTesterItem::encode, PacketGenericParticleTesterItem::new);

	public PacketGenericParticleTesterItem(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readBoolean(), buf.readBoolean(),
				GenericParticleTestConfig.fromBuffer(buf));
	}

	public static void handle(PacketGenericParticleTesterItem msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null || !(player.level() instanceof ServerLevel level)) {
				return;
			}
			ItemStack stack = player.getItemInHand(msg.hand());
			if (!(stack.getItem() instanceof ItemGenericParticleTester)) {
				return;
			}
			GenericParticleTestConfig config = msg.config().clamped();
			if (msg.save()) {
				config.writeToItem(stack);
			}
			if (msg.test()) {
				Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(config.range()));
				GenericParticleTesterSpawner.spawn(level, pos, config);
			}
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketGenericParticleTesterItem msg) {
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
