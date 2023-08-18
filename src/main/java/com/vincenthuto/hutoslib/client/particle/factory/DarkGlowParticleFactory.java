package com.vincenthuto.hutoslib.client.particle.factory;

import com.vincenthuto.hutoslib.client.particle.ParticleDarkGlow;
import com.vincenthuto.hutoslib.client.particle.data.DarkColorParticleData;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class DarkGlowParticleFactory implements ParticleProvider<DarkColorParticleData> {
	public static final String NAME = "dark_glow";
	public static ParticleOptions createData(ParticleColor color) {
		return new DarkColorParticleData(HLParticleInit.dark_glow.get(), color);
	}

	private final SpriteSet spriteSet;

	public DarkGlowParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle createParticle(DarkColorParticleData data, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return new ParticleDarkGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(),
				data.color.getBlue(), 1.0f, .035f, 136, this.spriteSet);

	}

}