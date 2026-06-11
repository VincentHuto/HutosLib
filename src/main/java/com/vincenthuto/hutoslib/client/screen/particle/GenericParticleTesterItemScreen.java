package com.vincenthuto.hutoslib.client.screen.particle;

import com.vincenthuto.hutoslib.common.network.PacketGenericParticleTesterItem;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

public class GenericParticleTesterItemScreen extends GenericParticleTesterScreen {
	private final InteractionHand hand;

	private GenericParticleTesterItemScreen(InteractionHand hand, GenericParticleTestConfig config) {
		super(Component.literal("Particle Tester"), config, false);
		this.hand = hand;
	}

	public static void open(InteractionHand hand, GenericParticleTestConfig config) {
		Minecraft.getInstance().setScreen(new GenericParticleTesterItemScreen(hand, config));
	}

	@Override
	protected void onSave() {
		PacketDistributor.sendToServer(new PacketGenericParticleTesterItem(hand, true, false, config));
	}

	@Override
	protected void onTest() {
		PacketDistributor.sendToServer(new PacketGenericParticleTesterItem(hand, false, true, config));
	}
}
