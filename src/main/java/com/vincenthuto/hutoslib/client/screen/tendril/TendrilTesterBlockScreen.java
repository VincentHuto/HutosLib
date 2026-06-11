package com.vincenthuto.hutoslib.client.screen.tendril;

import com.vincenthuto.hutoslib.common.network.PacketTendrilTesterBlock;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class TendrilTesterBlockScreen extends TendrilTesterScreen {
	private final BlockPos pos;

	private TendrilTesterBlockScreen(BlockPos pos, TendrilEffectConfig config) {
		super(Component.literal("Tendril Tester Block"), config, true);
		this.pos = pos;
	}

	public static void open(BlockPos pos, TendrilEffectConfig config) {
		Minecraft.getInstance().setScreen(new TendrilTesterBlockScreen(pos, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketTendrilTesterBlock(pos, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketTendrilTesterBlock(pos, false, true, config));
	}
}
