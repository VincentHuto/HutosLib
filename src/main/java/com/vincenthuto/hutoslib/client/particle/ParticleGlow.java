package com.vincenthuto.hutoslib.client.particle;

import java.util.Random;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.client.HLRenderTypeInit;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ParticleGlow extends TextureSheetParticle {
	public float colorR = 0;
	public float colorG = 0;
	public float colorB = 0;
	public float initScale = 0;
	public float initAlpha = 0;

	public ParticleGlow(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r,
			float g, float b, float a, float scale, int lifetime, SpriteSet sprite) {
		super(worldIn, x, y, z, 0, 0, 0);
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
		this.lifetime = (int) ((float) lifetime * 0.5f);
		this.quadSize = scale / 8;
		this.initScale = scale;
		this.xd = vx * 2.0f;
		this.yd = vy * 2.0f;
		this.zd = vz * 2.0f;
		this.initAlpha = a;
		this.pickSprite(sprite);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return HLRenderTypeInit.GLOW_RENDER;
	}

	@Override
	public int getLightColor(float pTicks) {
		return 255;
	}

	
	
	@Override
	public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
		// TODO Auto-generated method stub
		super.render(p_107678_, p_107679_, p_107680_);
	}
	
	@Override
	public void tick() {
		super.tick();

		if (new Random().nextInt(6) == 0) {
			this.age++;
		}
		float lifeCoeff = (float) this.age / (float) this.lifetime;
		this.quadSize = initScale - initScale * lifeCoeff;
		this.alpha = initAlpha * (1.0f - lifeCoeff);
		this.oRoll = roll;
		roll += 1.0f;
	}

	@Override
	public boolean isAlive() {
		return this.age < this.lifetime;
	}
}