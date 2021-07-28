package com.hutoslib.client.particle.factory;

import com.hutoslib.client.particle.HutosLibParticleInit;
import com.hutoslib.client.particle.ParticleLightning;
import com.hutoslib.client.particle.data.ColorLightningData;
import com.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;

public class LightningParticleFactory implements ParticleProvider<ColorLightningData> {
	protected final SpriteSet spriteSet;

	public LightningParticleFactory(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	public Particle createParticle(ColorLightningData typeIn, ClientLevel worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		ParticleLightning particle = new ParticleLightning(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet,
				typeIn.color.getRed(), typeIn.color.getGreen(), typeIn.color.getBlue(), typeIn.speed, typeIn.maxAge,
				typeIn.fract, typeIn.maxOffset);
		return particle;
	}

	public static ParticleOptions createData(ParticleColor color, float s, int a, int f, float o) {
		return new ColorLightningData(HutosLibParticleInit.lightning_bolt.get(), color, s, a, f, o);
	}
}
