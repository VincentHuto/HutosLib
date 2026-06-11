package com.vincenthuto.hutoslib.client.screen.particle;

import com.vincenthuto.hutoslib.common.network.PacketGenericParticleTesterBlock;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class GenericParticleTesterBlockScreen extends GenericParticleTesterScreen {
	private final BlockPos pos;

	private GenericParticleTesterBlockScreen(BlockPos pos, GenericParticleTestConfig config) {
		super(Component.literal("Particle Tester Block"), config, true);
		this.pos = pos;
	}

	public static void open(BlockPos pos, GenericParticleTestConfig config) {
		Minecraft.getInstance().setScreen(new GenericParticleTesterBlockScreen(pos, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketGenericParticleTesterBlock(pos, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketGenericParticleTesterBlock(pos, false, true, config));
	}
}
