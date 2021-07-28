package com.hutoslib.client.particle.type;

import com.hutoslib.client.particle.data.DarkColorParticleData;
import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;

public class DarkGlowParticleType extends ParticleType<DarkColorParticleData> {
	public DarkGlowParticleType() {
		super(false, DarkColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<DarkColorParticleData> codec() {
		return DarkColorParticleData.CODEC;
	}

}