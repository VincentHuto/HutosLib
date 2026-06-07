package com.vincenthuto.hutoslib.client.screen.lightning;

import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.network.PacketLightningTesterBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class LightningTesterBlockScreen extends LightningTesterScreen {
	private final BlockPos pos;

	private LightningTesterBlockScreen(BlockPos pos, LightningTestConfig config) {
		super(Component.literal("Lightning Tester Block"), config, true);
		this.pos = pos;
	}

	public static void open(BlockPos pos, LightningTestConfig config) {
		Minecraft.getInstance().setScreen(new LightningTesterBlockScreen(pos, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketLightningTesterBlock(pos, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketLightningTesterBlock(pos, false, true, config));
	}
}
