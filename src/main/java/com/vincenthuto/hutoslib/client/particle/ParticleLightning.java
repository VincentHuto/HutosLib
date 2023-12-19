
package com.vincenthuto.hutoslib.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ParticleLightning extends TextureSheetParticle {
	private static final ParticleRenderType LIGHTNING_BOLT_RENDER = new ParticleRenderType() {

		@Override
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			ParticleLightning.beginRenderCommon(bufferBuilder, textureManager);
		}

		@Override
		public void end(Tesselator tessellator) {
			tessellator.end();
			ParticleLightning.endRenderCommon();
		}

		@Override
		public String toString() {
			return "hutoslib:lightning_bolt";
		}

	};
	@SuppressWarnings("deprecation")
	private static void beginRenderCommon(BufferBuilder buffer, TextureManager textureManager) {
		RenderSystem.depthMask(false);
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(770, 1);
	    RenderSystem.setShader(GameRenderer::getParticleShader);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	}
	@SuppressWarnings("deprecation")
	private static void endRenderCommon() {
		Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();
		// RenderSystem.alphaFunc((int) 516, (float) 0.1f);
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.depthMask(true);
	}
	private ParticleLightningStorage data;
	public float colorR = 0;





	public float colorG = 0;

	public float colorB = 0;

	public ParticleLightning(ClientLevel worldIn, double startX, double startY, double startZ, double endX, double endY,
			double endZ, SpriteSet sprite, float r, float g, float b) {
		super(worldIn, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		this.sprite = sprite.get(this.random);
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.random.nextLong());
		this.age = this.data.getMaxAge() + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;
		this.data.setMaxOffset(0.2f);
		this.data.fractalize();
		this.data.finalize();
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
		super(worldIn, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		this.sprite = sprite.get(this.random);
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.random.nextLong(), speed, maxAge);
		this.age = maxAge + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;
		this.data.setMaxOffset(0.2f);
		this.data.fractalize();
		this.data.finalize();
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
		super(worldIn, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		this.sprite = sprite.get(this.random);
		this.data = new ParticleLightningStorage(new Vector3(startX, startY, startZ), new Vector3(endX, endY, endZ),
				worldIn.random.nextLong(), speed, maxAge, fract, off);
		this.age = maxAge + 5;
		this.setPos(startX, startY, startZ);
		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;

		this.data.fractalize();
		this.data.finalize();
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
	public ParticleRenderType getRenderType() {
		return LIGHTNING_BOLT_RENDER;
	}

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		Vec3 vec3d = renderInfo.getPosition();
		float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3d.x);
		float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3d.y);
		float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3d.z);
		Vector3 posOffset = new Vector3(f, f1, f2);
		Vector3 particleOrigin = new Vector3(this.x, this.y, this.z);
		int count = 0;
		int maxIndex = (int) Math.ceil((this.data.getAge() + partialTicks) / this.data.getMaxAge()
				* this.data.numSegments());
		Vector3 lastEnd1 = null;
		Vector3 lastEnd2 = null;
		for (Segment s : this.data.getSegments()) {
			if (count > maxIndex)
				break;
			float width = Math.min(0.01f * this.data.getLength(), 0.04f);
			Vector3 start = s.getStart().subtract(particleOrigin);
			Vector3 end = s.getEnd().subtract(particleOrigin);
			Vector3 dir = end.subtract(start).normalize().scale(this.data.getLength() * 1.0E-4f);
			Vector3[] avector3f = new Vector3[] {
					lastEnd1 == null ? start.add(new Vector3(-width, 0.0, -width)) : lastEnd1.subtract(dir),
					lastEnd2 == null ? start.add(new Vector3(-width, 0.0, width)) : lastEnd2.subtract(dir),
					end.add(new Vector3(width, 0.0, width)), end.add(new Vector3(width, 0.0, -width)) };
			lastEnd1 = avector3f[2];
			lastEnd2 = avector3f[3];
			for (int i = 0; i < 4; ++i) {
				avector3f[i] = avector3f[i].add(posOffset);
			}
			float minU = this.getU0();
			float maxU = this.getU1();
			float minV = this.getV0();
			float maxV = this.getV1();
			int j = 0xF00000;
			buffer.vertex(avector3f[3].x, avector3f[3].y, avector3f[3].z).uv(maxU, maxV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[2].x, avector3f[2].y, avector3f[2].z).uv(maxU, minV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[0].x, avector3f[0].y, avector3f[0].z).uv(minU, minV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[1].x, avector3f[1].y, avector3f[1].z).uv(minU, maxV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[3].x, avector3f[3].y, avector3f[3].z).uv(maxU, maxV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[2].x, avector3f[2].y, avector3f[2].z).uv(maxU, minV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[0].x, avector3f[0].y, avector3f[0].z).uv(minU, minV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();
			buffer.vertex(avector3f[1].x, avector3f[1].y, avector3f[1].z).uv(minU, maxV)
					.color(this.colorR, this.colorG, this.colorB, this.alpha).uv2(j).endVertex();

			++count;
		}
	}

	@Override
	public boolean shouldCull() {
		return false;
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
