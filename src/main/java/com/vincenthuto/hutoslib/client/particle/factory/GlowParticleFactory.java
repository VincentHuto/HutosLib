package com.vincenthuto.hutoslib.client.particle.factory;

import com.vincenthuto.hutoslib.client.particle.ParticleGlow;
import com.vincenthuto.hutoslib.client.particle.data.ColorParticleData;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class GlowParticleFactory implements ParticleProvider<ColorParticleData> {
	public static final String NAME = "glow";
	public static ParticleOptions createData(ParticleColor color) {
		return new ColorParticleData(HLParticleInit.glow.get(), color);
	}

	private final SpriteSet spriteSet;

	public GlowParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle createParticle(ColorParticleData data, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return new ParticleGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(),
				data.color.getBlue(), 1.0f, .035f, 136, this.spriteSet);

	}

}