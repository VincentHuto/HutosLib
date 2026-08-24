package com.vincenthuto.hutoslib.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Transformation;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.math.BlockPosBlockPair;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.List;
import java.util.Random;

public class HLGuiUtils {

	private static void drawLine(PoseStack stack, double x1, double y1, double x2, double y2, ParticleColor color,
			int displace) {

		GlStateManager._depthMask(false);
		GlStateManager._disableCull();
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		BufferBuilder var5 = Tesselator.getInstance().begin(VertexFormat.Mode.LINES,
				DefaultVertexFormat.POSITION_COLOR_NORMAL);
		RenderSystem.lineWidth(1.0F);
		Vector3d vector3f = new Vector3d(x2 - x1, y2 - y1, 0);
		Vector3d vector3f2 = new Vector3d(x1 - x2, y1 - y2, 0);
		int red = (int) color.getRed();
		int green = (int) color.getGreen();
		int blue = (int) color.getBlue();
		var5.addVertex((float) x1, (float) y1, 0.0F).setColor(red, green, blue, 255)
				.setNormal((float) vector3f.x, (float) vector3f.y, 0.0F);
		var5.addVertex((float) x2, (float) y2, 0.0F).setColor(red, green, blue, 255)
				.setNormal((float) vector3f2.x, (float) vector3f2.y, 0.0F);
		BufferUploader.drawWithShader(var5.buildOrThrow());
		GlStateManager._enableCull();
		GlStateManager._depthMask(true);
	}

	/*
	 * Vanilla copy of wrap to max width to allow for drop shadow and readable name
	 */
	public static void drawMaxWidthString(Font fontIn, FormattedText text, int x, int y, int maxLength, int color,
			boolean dropShadow) {
		Matrix4f matrix4f = Transformation.identity().getMatrix();
		for (FormattedCharSequence formattedcharsequence : fontIn.split(text, maxLength)) {
			drawText(fontIn, formattedcharsequence, x, y, color, matrix4f, dropShadow);
			y += 9;
		}

	}

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawScaledTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height, float scaleIn) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.addVertex(x, y + height, 1.0F).setUv((textureX) * scaleIn, (textureY + height) * scaleIn);
		bufferbuilder.addVertex(x + width, y + height, 1.0F)
				.setUv((textureX + width) * scaleIn, (textureY + height) * scaleIn);
		bufferbuilder.addVertex(x + width, y, 1.0F).setUv((textureX + width) * scaleIn, (textureY) * scaleIn);
		bufferbuilder.addVertex(x, y, 1.0F).setUv((textureX) * scaleIn, (textureY) * scaleIn);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}

	public static int drawText(Font fontIn, FormattedCharSequence reorderingProcessor, float x, float y, int color,
			Matrix4f matrix, boolean drawShadow) {
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers()
				.bufferSource();
		int i = fontIn.drawInBatch(reorderingProcessor, x, y, color, drawShadow, matrix, multibuffersource$buffersource,
				Font.DisplayMode.NORMAL, 0, 15728880);
		multibuffersource$buffersource.endBatch();
		return i;
	}

	/**
	 * Draws a textured rectangle at the current z-value. Ported From past Versions
	 */
	public static void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width,
			float height) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.addVertex(x, y + height, 1.0F)
				.setUv((textureX) * 0.00390625F, (textureY + height) * 0.00390625F);
		bufferbuilder.addVertex(x + width, y + height, 1.0F)
				.setUv((textureX + width) * 0.00390625F, (textureY + height) * 0.00390625F);
		bufferbuilder.addVertex(x + width, y, 1.0F)
				.setUv((textureX + width) * 0.00390625F, (textureY) * 0.00390625F);
		bufferbuilder.addVertex(x, y, 1.0F).setUv((textureX) * 0.00390625F, (textureY) * 0.00390625F);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}

	public static void fracLine(PoseStack matrix, double src_x, double src_y, double dst_x, double dst_y, int zLevel,
			ParticleColor color, int displace, double detail) {
		if (displace < detail) {
			drawLine(matrix, src_x, src_y, dst_x, dst_y, color, displace);
		} else {
			Random rand = new Random();
			double mid_x = (dst_x + src_x) / 2;
			double mid_y = (dst_y + src_y) / 2;
			mid_x = (int) (mid_x + (rand.nextFloat() - 0.25) * displace * 0.25);
			mid_y = (int) (mid_y + (rand.nextFloat() - 0.25) * displace * 0.25);
			fracLine(matrix, src_x, src_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);
			fracLine(matrix, dst_x, dst_y, mid_x, mid_y, zLevel, color, (displace / 2), detail);

		}
	}

	// MATRIX FIXING
	public static void renderItemStackInGui(GuiGraphics graphics, ItemStack stack, int x, int y) {
		graphics.renderFakeItem(stack, x, y);
	}

	// MULTIBLOCK STUFF
	private static long multiblockCycleIndex() {
		return System.currentTimeMillis() / 2000L;
	}

	public static void renderMultiBlock(PoseStack matrices, MultiblockPattern pattern, float partialTicks,
			BlockAndTintGetter getter, double relX, double relY) {
		matrices.pushPose();
		matrices.translate(relX, relY, 100.0D);
		matrices.scale(8.0F, -8.0F, 8.0F);
		MultiBufferSource.BufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
		for (BlockPosBlockPair box : pattern.getDisplayBlockPosBlockList(multiblockCycleIndex())) {
			if (box.getBlock() == null) continue;
			matrices.pushPose();
			matrices.translate((box.getPos().getX() - (pattern.getBlockPattern().getWidth() / 2)) - 0.5,
					(box.getPos().getY() - (pattern.getBlockPattern().getHeight() / 2)) - 0.5,
					(box.getPos().getZ() - (pattern.getBlockPattern().getDepth() / 2)) - 0.5);
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(box.getBlock().defaultBlockState(), matrices,
					src, 0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
			matrices.popPose();
		}
		src.endBatch();
		matrices.popPose();

	}

	public static void renderPatternInGUI(GuiGraphics graphics, Minecraft mc, MultiblockPattern pattern, double xOff,
			double yOff) {
		PoseStack viewModelPose = graphics.pose();
		viewModelPose.pushPose();
		Lighting.setupFor3DItems();
		List<BlockPosBlockPair> patternList = pattern.getDisplayBlockPosBlockList(multiblockCycleIndex());
		viewModelPose.translate(xOff, yOff, 0.0D);
		viewModelPose.scale(0.5f, 0.5f, -1f);
		viewModelPose.mulPose(new Quaternion(Vector3.YP, -5, true).toMoj());
		for (BlockPosBlockPair pair : patternList) {
			renderItemStackInGui(graphics, new ItemStack(pair.getBlock()), pair.getPos().getX() * -16,
					pair.getPos().getZ() * 16);
		}
		viewModelPose.popPose();
	}

	public static void transferMsToGl(PoseStack ms, Runnable toRun) {
		toRun.run();
	}

}
