package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.particle.data.DarkColorParticleData;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class DarkGlowParticleType extends ParticleType<DarkColorParticleData> {
	public DarkGlowParticleType() {
		super(false);
	}

	@Override
	public MapCodec<DarkColorParticleData> codec() {
		return DarkColorParticleData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, DarkColorParticleData> streamCodec() {
		return DarkColorParticleData.STREAM_CODEC;
	}
}