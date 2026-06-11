package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.template.EffectTemplateType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketEffectTemplateItem(InteractionHand hand, EffectTemplateType templateType, String json)
		implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketEffectTemplateItem> TYPE =
			new CustomPacketPayload.Type<>(HutosLib.rloc("packet_effect_template_item"));

	public static final StreamCodec<FriendlyByteBuf, PacketEffectTemplateItem> CODEC =
			StreamCodec.of(PacketEffectTemplateItem::encode, PacketEffectTemplateItem::new);

	public PacketEffectTemplateItem(FriendlyByteBuf buf) {
		this(buf.readEnum(InteractionHand.class), buf.readEnum(EffectTemplateType.class), buf.readUtf(32767));
	}

	public static void handle(PacketEffectTemplateItem msg, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var player = ctx.player();
			if (player == null) {
				return;
			}
			ItemStack stack = player.getItemInHand(msg.hand());
			if (!msg.templateType().matches(stack)) {
				return;
			}
			msg.templateType().writeJsonToItem(stack, msg.json());
		});
	}

	private static void encode(FriendlyByteBuf buf, PacketEffectTemplateItem msg) {
		buf.writeEnum(msg.hand());
		buf.writeEnum(msg.templateType());
		buf.writeUtf(msg.json(), 32767);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
