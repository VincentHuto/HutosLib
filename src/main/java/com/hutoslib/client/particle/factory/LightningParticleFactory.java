package com.hutoslib.client.particle.factory;

import com.hutoslib.client.particle.ParticleInit;
import com.hutoslib.client.particle.ParticleLightning;
import com.hutoslib.client.particle.data.ColorLightningData;
import com.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class LightningParticleFactory implements IParticleFactory<ColorLightningData> {
	protected final IAnimatedSprite spriteSet;

	public LightningParticleFactory(IAnimatedSprite sprite) {
		this.spriteSet = sprite;
	}

	public Particle makeParticle(ColorLightningData typeIn, ClientWorld worldIn, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		ParticleLightning particle = new ParticleLightning(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet,
				typeIn.color.getRed(), typeIn.color.getGreen(), typeIn.color.getBlue(), typeIn.speed, typeIn.maxAge,
				typeIn.fract, typeIn.maxOffset);
		return particle;
	}

	public static IParticleData createData(ParticleColor color, float s, int a, int f, float o) {
		return new ColorLightningData(ParticleInit.lightning_bolt.get(), color, s, a, f, o);
	}
}
