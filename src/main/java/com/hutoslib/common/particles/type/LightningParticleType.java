package com.hutoslib.common.particles.type;

import com.hutoslib.common.particles.data.ColorLightningData;
import com.mojang.serialization.Codec;

import net.minecraft.particles.ParticleType;

public class LightningParticleType extends ParticleType<ColorLightningData> {
	public LightningParticleType() {
		super(false, ColorLightningData.DESERIALIZER);
	}

	@Override
	public Codec<ColorLightningData> func_230522_e_() {
		return ColorLightningData.CODEC;
	}
}