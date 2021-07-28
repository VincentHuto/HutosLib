package com.hutoslib.client.particle.type;

import com.hutoslib.client.particle.data.ColorParticleData;
import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;

public class GlowParticleType extends ParticleType<ColorParticleData> {
	public GlowParticleType() {
		super(false, ColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<ColorParticleData> codec() {
		return ColorParticleData.CODEC;
	}
}