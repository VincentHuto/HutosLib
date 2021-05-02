package com.hutoslib.common.particles.type;

import com.hutoslib.common.particles.data.ColorParticleData;
import com.mojang.serialization.Codec;

import net.minecraft.particles.ParticleType;

public class GlowParticleType extends ParticleType<ColorParticleData> {
	public GlowParticleType() {
		super(false, ColorParticleData.DESERIALIZER);
	}

	@Override
	public Codec<ColorParticleData> func_230522_e_() {
		return ColorParticleData.CODEC;
	}
}