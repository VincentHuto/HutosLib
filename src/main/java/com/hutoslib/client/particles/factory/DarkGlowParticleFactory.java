package com.hutoslib.client.particles.factory;

import com.hutoslib.client.particle.ParticleColor;
import com.hutoslib.client.particles.ParticleDarkGlow;
import com.hutoslib.client.particles.ParticleInit;
import com.hutoslib.client.particles.data.DarkColorParticleData;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class DarkGlowParticleFactory implements IParticleFactory<DarkColorParticleData> {
	private final IAnimatedSprite spriteSet;
	public static final String NAME = "dark_glow";

	public DarkGlowParticleFactory(IAnimatedSprite sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle makeParticle(DarkColorParticleData data, ClientWorld worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return new ParticleDarkGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(),
				data.color.getBlue(), 1.0f, .035f, 136, this.spriteSet);

	}

	public static IParticleData createData(ParticleColor color) {
		return new DarkColorParticleData(ParticleInit.dark_glow.get(), color);
	}

}