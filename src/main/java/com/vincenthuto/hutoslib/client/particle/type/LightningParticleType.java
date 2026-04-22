package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.particle.data.ColorLightningData;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class LightningParticleType extends ParticleType<ColorLightningData> {
	public LightningParticleType() {
		super(false);
	}

	@Override
	public MapCodec<ColorLightningData> codec() {
		return ColorLightningData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ColorLightningData> streamCodec() {
		return ColorLightningData.STREAM_CODEC;
	}
}