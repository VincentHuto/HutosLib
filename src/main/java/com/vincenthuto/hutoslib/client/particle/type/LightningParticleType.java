package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.Codec;
import com.vincenthuto.hutoslib.client.particle.data.ColorLightningData;

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