package com.hutoslib.client.particle.type;

import com.hutoslib.client.particle.data.ColorLightningData;
import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;

public class LightningParticleType extends ParticleType<ColorLightningData> {
	public LightningParticleType() {
		super(false, ColorLightningData.DESERIALIZER);
	}

	@Override
	public Codec<ColorLightningData> codec() {
		return ColorLightningData.CODEC;
	}
}