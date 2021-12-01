package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.Codec;
import com.vincenthuto.hutoslib.client.particle.data.ColorParticleData;

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