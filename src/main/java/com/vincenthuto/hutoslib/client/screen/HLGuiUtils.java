package com.vincenthuto.hutoslib.client.screen;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3d;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HLGuiUtils {

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height) {
		/*
		 * float f = 0.00390625F; float f1 = 0.00390625F;
		 */
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex((double) (x + 0), (double) (y + height), (double) 1)
				.uv((float) ((float) (textureX + 0) * 0.00390625F), (float) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + height), (double) 1)
				.uv((float) ((float) (textureX + width) * 0.00390625F),
						(float) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + 0), (double) 1)
				.uv((float) ((float) (textureX + width) * 0.00390625F), (float) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.vertex((double) (x + 0), (double) (y + 0), 1)
				.uv((float) ((float) (textureX + 0) * 0.00390625F), (float) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.end();
	}

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawScaledTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height, float scaleIn) {
		/*
		 * float f = 0.01090625F; float f1 = 0.01090625F;
		 */
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex((double) (x + 0), (double) (y + height), (double) 1)
				.uv((float) ((float) (textureX + 0) * scaleIn), (float) ((float) (textureY + height) * scaleIn))
				.endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + height), (double) 1)
				.uv((float) ((float) (textureX + width) * scaleIn), (float) ((float) (textureY + height) * scaleIn))
				.endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + 0), (double) 1)
				.uv((float) ((float) (textureX + width) * scaleIn), (float) ((float) (textureY + 0) * scaleIn))
				.endVertex();
		bufferbuilder.vertex((double) (x + 0), (double) (y + 0), 1)
				.uv((float) ((float) (textureX + 0) * scaleIn), (float) ((float) (textureY + 0) * scaleIn)).endVertex();
		tessellator.end();
	}

	/*
	 * Vanilla copy of wrap to max width to allow for drop shadow and readable name
	 */
	public static void drawMaxWidthString(Font fontIn, FormattedText text, int x, int y, int maxLength, int color,
			boolean dropShadow) {
		Matrix4f matrix4f = Transformation.identity().getMatrix();
		for (FormattedCharSequence formattedcharsequence : fontIn.split(text, maxLength)) {
			drawText(fontIn, formattedcharsequence, (float) x, (float) y, color, matrix4f, dropShadow);
			y += 9;
		}

	}

	public static int drawText(Font fontIn, FormattedCharSequence reorderingProcessor, float x, float y, int color,
			Matrix4f matrix, boolean drawShadow) {
		MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource
				.immediate(Tesselator.getInstance().getBuilder());
		int i = fontIn.drawInBatch(reorderingProcessor, x, y, color, drawShadow, matrix, multibuffersource$buffersource,
				false, 0, 15728880);
		multibuffersource$buffersource.endBatch();
		return i;
	}

	private static void drawLine(PoseStack stack, int x1, int y1, int x2, int y2, ParticleColor color, int displace) {
		// RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager._disableTexture();
		GlStateManager._depthMask(false);
		GlStateManager._disableCull();
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		Tesselator var4 = RenderSystem.renderThreadTesselator();
		BufferBuilder var5 = var4.getBuilder();
		RenderSystem.lineWidth(1.0F);
		var5.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		Vector3d vector3f = new Vector3d(x2 - x1, y2 - y1, 0);
		Vector3d vector3f2 = new Vector3d(x1 - x2, y1 - y2, 0);
		int red = (int) color.getRed();
		int green = (int) color.getGreen();
		int blue = (int) color.getBlue();
		var5.vertex(x1, y1, 0.0D).color(red, green, blue, 255).normal((float) vector3f.x, (float) vector3f.y, 0.0F)
				.endVertex();
		var5.vertex(x2, y2, 0.0D).color(red, green, blue, 255).normal((float) vector3f2.x, (float) vector3f2.y, 0.0F)
				.endVertex();
		var4.end();
		GlStateManager._enableCull();
		GlStateManager._depthMask(true);
		GlStateManager._enableTexture();
	}

	public static void fracLine(PoseStack matrix, int src_x, int src_y, int dst_x, int dst_y, int zLevel,
			ParticleColor color, int displace, double detail) {
		if (displace < detail) {
			drawLine(matrix, src_x, src_y, dst_x, dst_y, color, displace);
		} else {
			Random rand = new Random();
			int mid_x = (dst_x + src_x) / 2;
			int mid_y = (dst_y + src_y) / 2;
			mid_x = (int) (mid_x + (rand.nextFloat() - 0.25) * displace * 0.25);
			mid_y = (int) (mid_y + (rand.nextFloat() - 0.25) * displace * 0.25);
			fracLine(matrix, src_x, src_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);
			fracLine(matrix, dst_x, dst_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);

		}
	}

}
