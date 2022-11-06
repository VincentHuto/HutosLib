package com.vincenthuto.hutoslib.client.particle.factory;

import com.vincenthuto.hutoslib.client.particle.HLParticleInit;
import com.vincenthuto.hutoslib.client.particle.ParticleLightning;
import com.vincenthuto.hutoslib.client.particle.data.ColorLightningData;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class LightningParticleFactory implements ParticleProvider<ColorLightningData> {
	public static ParticleOptions createData(ParticleColor color, float s, int a, int f, float o) {
		return new ColorLightningData(HLParticleInit.lightning_bolt.get(), color, s, a, f, o);
	}

	protected final SpriteSet spriteSet;

	public LightningParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle createParticle(ColorLightningData typeIn, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		ParticleLightning particle = new ParticleLightning(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet,
				typeIn.color.getRed(), typeIn.color.getGreen(), typeIn.color.getBlue(), typeIn.speed, typeIn.maxAge,
				typeIn.fract, typeIn.maxOffset);
		return particle;
	}
}
