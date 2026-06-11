package com.vincenthuto.hutoslib.client.screen.tendril;

import com.vincenthuto.hutoslib.common.network.PacketTendrilTesterItem;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

public class TendrilTesterItemScreen extends TendrilTesterScreen {
	private final InteractionHand hand;

	private TendrilTesterItemScreen(InteractionHand hand, TendrilEffectConfig config) {
		super(Component.literal("Tendril Tester"), config, false);
		this.hand = hand;
	}

	public static void open(InteractionHand hand, TendrilEffectConfig config) {
		Minecraft.getInstance().setScreen(new TendrilTesterItemScreen(hand, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketTendrilTesterItem(hand, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketTendrilTesterItem(hand, false, true, config));
	}
}
