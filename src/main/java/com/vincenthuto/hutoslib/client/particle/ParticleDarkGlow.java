package com.vincenthuto.hutoslib.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

public class ParticleDarkGlow extends SingleQuadParticle {
	public float colorR = 0;
	public float colorG = 0;
	public float colorB = 0;
	public float initScale = 0;
	public float initAlpha = 0;

	public ParticleDarkGlow(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r,
			float g, float b, float a, float scale, int lifetime, SpriteSet sprite) {
		super(worldIn, x, y, z, vx * 2.0f, vy * 2.0f, vz * 2.0f, sprite.get(worldIn.getRandom()));
		this.colorR = r;
		this.colorG = g;
		this.colorB = b;
		if (this.colorR > 1.0) {
			this.colorR = this.colorR / 255.0f;
		}
		if (this.colorG > 1.0) {
			this.colorG = this.colorG / 255.0f;
		}
		if (this.colorB > 1.0) {
			this.colorB = this.colorB / 255.0f;
		}
		this.setColor(colorR, colorG, colorB);
		this.lifetime = (int) (lifetime * 0.5f);
		this.quadSize = scale / 8;
		this.initScale = scale;
		this.initAlpha = a;
	}

	@Override
	protected int getLightCoords(float pTicks) {
		return 255;
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	public boolean isAlive() {
		return this.age < this.lifetime;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.random.nextInt(6) == 0) {
			this.age++;
		}
		float lifeCoeff = (float) this.age / (float) this.lifetime;
		this.quadSize = initScale - initScale * lifeCoeff;
		this.alpha = initAlpha * (1.0f - lifeCoeff);
		this.oRoll = roll;
		roll += 1.0f;
	}
}
