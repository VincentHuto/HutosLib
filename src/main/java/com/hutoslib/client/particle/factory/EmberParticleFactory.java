package com.hutoslib.client.particle.factory;

import com.hutoslib.client.particle.ParticleGlow;
import com.hutoslib.client.particle.HutosLibParticleInit;
import com.hutoslib.client.particle.data.EmberParticleData;
import com.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;

public class EmberParticleFactory implements ParticleProvider<EmberParticleData> {
	private final SpriteSet spriteSet;
	public static final String NAME = "ember";

	public EmberParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	/**
	 * Made to be much more generic form of glow, allowing form more variable
	 * control default values for alpha scale and life are 1.0, 0.35, 136
	 */
	@Override
	public Particle createParticle(EmberParticleData data, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return new ParticleGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(),
				data.color.getBlue(), data.alpha, data.scale, data.life, this.spriteSet);

	}

	public static ParticleOptions createData(ParticleColor color, float s, float a, int l) {
		return new EmberParticleData(HutosLibParticleInit.ember.get(), color, a, s, l);
	}

}