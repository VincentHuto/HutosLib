package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.Codec;
import com.vincenthuto.hutoslib.client.particle.data.EmberParticleData;

import net.minecraft.core.particles.ParticleType;

public class EmberParticleType extends ParticleType<EmberParticleData> {
	public EmberParticleType() {
		super(false, EmberParticleData.DESERIALIZER);
	}

	@Override
	public Codec<EmberParticleData> codec() {
		return EmberParticleData.CODEC;
	}
}