package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.particle.data.ColorParticleData;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class GlowParticleType extends ParticleType<ColorParticleData> {
	public GlowParticleType() {
		super(false);
	}

	@Override
	public MapCodec<ColorParticleData> codec() {
		return ColorParticleData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleData> streamCodec() {
		return ColorParticleData.STREAM_CODEC;
	}
}