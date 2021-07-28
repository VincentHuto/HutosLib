package com.hutoslib.client.particle.factory;

import com.hutoslib.client.particle.ParticleGlow;
import com.hutoslib.client.particle.HutosLibParticleInit;
import com.hutoslib.client.particle.data.ColorParticleData;
import com.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;

public class GlowParticleFactory implements ParticleProvider<ColorParticleData> {
	private final SpriteSet spriteSet;
	public static final String NAME = "glow";

	public GlowParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle createParticle(ColorParticleData data, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return new ParticleGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(),
				data.color.getBlue(), 1.0f, .035f, 136, this.spriteSet);

	}

	public static ParticleOptions createData(ParticleColor color) {
		return new ColorParticleData(HutosLibParticleInit.glow.get(), color);
	}

}