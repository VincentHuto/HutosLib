package com.vincenthuto.hutoslib.client.screen.lightning;

import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.network.PacketLightningTesterItem;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

public class LightningTesterItemScreen extends LightningTesterScreen {
	private final InteractionHand hand;

	private LightningTesterItemScreen(InteractionHand hand, LightningTestConfig config) {
		super(Component.literal("Lightning Tester Item"), config, false);
		this.hand = hand;
	}

	public static void open(InteractionHand hand, LightningTestConfig config) {
		Minecraft.getInstance().setScreen(new LightningTesterItemScreen(hand, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketLightningTesterItem(hand, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketLightningTesterItem(hand, false, true, config));
	}
}
