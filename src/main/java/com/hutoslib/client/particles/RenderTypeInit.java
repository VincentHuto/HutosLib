package com.hutoslib.client.particles;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderTypeInit {
	public static final IParticleRenderType GLOW_RENDER = new IParticleRenderType() {
		@SuppressWarnings("deprecation")
		@Override
		public void beginRender(BufferBuilder buffer, TextureManager textureManager) {

			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.alphaFunc(516, 0.3f);
			RenderSystem.enableCull();
			textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
			RenderSystem.depthMask(false);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE.param);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void finishRender(Tessellator tessellator) {
			tessellator.draw();
			RenderSystem.enableDepthTest();
			RenderSystem.enableAlphaTest();
			RenderSystem.depthMask(true);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE.param);
			RenderSystem.disableCull();
			RenderSystem.alphaFunc(516, 0.1F);

		}

		@Override
		public String toString() {
			return "hutoslib:glow_rend";
		}
	};
	public static final IParticleRenderType DARK_GLOW_RENDER = new IParticleRenderType() {
		@SuppressWarnings("deprecation")
		@Override
		public void beginRender(BufferBuilder buffer, TextureManager textureManager) {
			RenderSystem.depthMask(true);
			textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.enableCull();
			RenderSystem.depthMask(false);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.alphaFunc(516, 0.003921569F);
			buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}

		@Override
		public void finishRender(Tessellator tessellator) {
			RenderSystem.disableCull();
			RenderSystem.depthMask(true);

			tessellator.draw();

		}

		@Override
		public String toString() {
			return "hutoslib:dark_glow_rend";
		}
	};

}
