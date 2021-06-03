package com.hutoslib.client.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiUtils {

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height) {
		/*
		 * float f = 0.00390625F; float f1 = 0.00390625F;
		 */
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) 1)
				.tex((float) ((float) (textureX + 0) * 0.00390625F),
						(float) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) 1)
				.tex((float) ((float) (textureX + width) * 0.00390625F),
						(float) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) 1)
				.tex((float) ((float) (textureX + width) * 0.00390625F), (float) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 1)
				.tex((float) ((float) (textureX + 0) * 0.00390625F), (float) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawScaledTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height, float scaleIn) {
		/*
		 * float f = 0.01090625F; float f1 = 0.01090625F;
		 */
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) 1)
				.tex((float) ((float) (textureX + 0) * scaleIn), (float) ((float) (textureY + height) * scaleIn))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) 1)
				.tex((float) ((float) (textureX + width) * scaleIn), (float) ((float) (textureY + height) * scaleIn))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) 1)
				.tex((float) ((float) (textureX + width) * scaleIn), (float) ((float) (textureY + 0) * scaleIn))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 1)
				.tex((float) ((float) (textureX + 0) * scaleIn), (float) ((float) (textureY + 0) * scaleIn))
				.endVertex();
		tessellator.draw();
	}

	/*
	 * Vanilla copy of wrap to max width to allow for drop shadow and readable name
	 */
	public static void drawMaxWidthString(FontRenderer fontIn, ITextProperties text, int x, int y, int maxLength,
			int color, boolean dropShadow) {
		Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();
		for (IReorderingProcessor ireorderingprocessor : fontIn.trimStringToWidth(text, maxLength)) {
			drawText(fontIn, ireorderingprocessor, (float) x, (float) y, color, matrix4f, dropShadow);
			y += 9;
		}

	}

	private static int drawText(FontRenderer fontIn, IReorderingProcessor reorderingProcessor, float x, float y,
			int color, Matrix4f matrix, boolean drawShadow) {
		IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer
				.getImpl(Tessellator.getInstance().getBuffer());
		int i = fontIn.drawEntityText(reorderingProcessor, x, y, color, drawShadow, matrix, irendertypebuffer$impl,
				false, 0, 15728880);
		irendertypebuffer$impl.finish();
		return i;
	}

	public static void drawLine(double src_x, double src_y, double dst_x, double dst_y, int zLevel, int color,
			int displace) {
		GL11.glDisable((int) 3553);
		GL11.glLineWidth((float) 1.0f);
		GL11.glColor3f((float) ((float) ((color & 0xFF0000) >> 16) / 255.0f),
				(float) ((float) ((color & 0xFF00) >> 8) / 255.0f), (float) ((float) (color & 0xFF) / 255.0f));
		GL11.glBegin((int) 1);
		GL11.glVertex3f((float) src_x, (float) src_y, (float) zLevel);
		GL11.glVertex3f((float) dst_x, (float) dst_y, (float) zLevel);
		GL11.glEnd();
		GL11.glColor3f((float) 1.0f, (float) 1.0f, (float) 1.0f);
		GL11.glEnable((int) 3553);
	}

	public static void fracLine(double src_x, double src_y, double dst_x, double dst_y, int zLevel, int color,
			int displace, double detail) {
		if (displace < detail) {
			drawLine(src_x, src_y, dst_x, dst_y, zLevel, color, displace);
		} else {
			Random rand = new Random();
			double mid_x = (dst_x + src_x) / 2;
			double mid_y = (dst_y + src_y) / 2;
			mid_x = ((double) mid_x + ((double) rand.nextFloat() - 0.5) * (double) displace * 0.5);
			mid_y = ((double) mid_y + ((double) rand.nextFloat() - 0.5) * (double) displace * 0.5);
			fracLine(src_x, src_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);
			fracLine(dst_x, dst_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);

		}
	}

}
