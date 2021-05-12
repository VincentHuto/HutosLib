package com.hutoslib.client.gui;

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
	public static void drawScaledTexturedModalRect(float x, float y, float textureX, float textureY,float width, float height,
			float scaleIn) {
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

}
