package com.hutoslib.client.particle.type;

import com.hutoslib.client.particle.data.EmberParticleData;
import com.mojang.serialization.Codec;

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