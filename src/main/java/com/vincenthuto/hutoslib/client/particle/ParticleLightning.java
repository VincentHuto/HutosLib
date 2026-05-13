package com.vincenthuto.hutoslib.client.particle;

import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

public class ParticleLightning extends SingleQuadParticle {
	private ParticleLightningStorage data;
	public float colorR = 0;





	public float colorG = 0;

	public float colorB = 0;

	public ParticleLightning(ClientLevel worldIn, double startX, double startY, double startZ, double endX, double endY,
			double endZ, SpriteSet sprite, float r, float g, float b) {
		super(worldIn, startX, startY, startZ, sprite.get(worldIn.getRandom()));
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.getRandom().nextLong());
		this.lifetime = this.data.getMaxAge() + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;
		this.data.setMaxOffset(0.2f);
		this.data.fractalize();
		this.data.finish();
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
	}

	public ParticleLightning(ClientLevel worldIn, double startX, double startY, double startZ, double endX, double endY,
			double endZ, SpriteSet sprite, float r, float g, float b, float speed, int maxAge) {
		super(worldIn, startX, startY, startZ, sprite.get(worldIn.getRandom()));
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.getRandom().nextLong(), speed, maxAge);
		this.lifetime = maxAge + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;
		this.data.setMaxOffset(0.2f);
		this.data.fractalize();
		this.data.finish();
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
	}

	public ParticleLightning(ClientLevel worldIn, double startX, double startY, double startZ, double endX, double endY,
			double endZ, SpriteSet sprite, float r, float g, float b, float speed, int maxAge, int fract, float off) {
		super(worldIn, startX, startY, startZ, sprite.get(worldIn.getRandom()));
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.getRandom().nextLong(), speed, maxAge, fract, off);
		this.lifetime = maxAge + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;

		this.data.fractalize();
		this.data.finish();
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
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	@SuppressWarnings("unused")
	public void tick() {
		super.tick();
		this.data.onUpdate();
		if (this.age > this.getLifetime() - 10) {
			float delta;
			this.alpha = delta = (this.getLifetime() - this.age) / 10.0f;
		}
	}

}
