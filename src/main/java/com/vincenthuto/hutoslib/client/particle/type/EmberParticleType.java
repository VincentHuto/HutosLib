package com.vincenthuto.hutoslib.client.particle.type;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.particle.data.EmberParticleData;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EmberParticleType extends ParticleType<EmberParticleData> {
	public EmberParticleType() {
		super(false);
	}

	@Override
	public MapCodec<EmberParticleData> codec() {
		return EmberParticleData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, EmberParticleData> streamCodec() {
		return EmberParticleData.STREAM_CODEC;
	}
}