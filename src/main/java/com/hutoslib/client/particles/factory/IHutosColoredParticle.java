package com.hutoslib.client.particles.factory;

import com.hutoslib.client.particle.ParticleColor;

import net.minecraft.particles.IParticleData;

public interface IHutosColoredParticle {
	public IParticleData createData(ParticleColor color);
}
