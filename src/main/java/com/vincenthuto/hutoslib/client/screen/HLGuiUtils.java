package com.vincenthuto.hutoslib.client.screen;

import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3d;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Transformation;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.client.render.block.BlockPosBlockPair;
import com.vincenthuto.hutoslib.client.render.block.MultiblockPattern;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HLGuiUtils {

	private static void drawLine(PoseStack stack, double x1, double y1, double x2, double y2, ParticleColor color,
			int displace) {

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
		/*
		 * float f = 0.01090625F; float f1 = 0.01090625F;
		 */
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(x + 0, y + height, 1)
				.uv((textureX + 0) * scaleIn, (textureY + height) * scaleIn)
				.endVertex();
		bufferbuilder.vertex(x + width, y + height, 1)
				.uv((textureX + width) * scaleIn, (textureY + height) * scaleIn)
				.endVertex();
		bufferbuilder.vertex(x + width, y + 0, 1)
				.uv((textureX + width) * scaleIn, (textureY + 0) * scaleIn)
				.endVertex();
		bufferbuilder.vertex(x + 0, y + 0, 1)
				.uv((textureX + 0) * scaleIn, (textureY + 0) * scaleIn).endVertex();
		tessellator.end();
	}

	public static int drawText(Font fontIn, FormattedCharSequence reorderingProcessor, float x, float y, int color,
			Matrix4f matrix, boolean drawShadow) {
		MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource
				.immediate(Tesselator.getInstance().getBuilder());
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
		/*
		 * float f = 0.00390625F; float f1 = 0.00390625F;
		 */
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(x + 0, y + height, 1)
				.uv((textureX + 0) * 0.00390625F, (textureY + height) * 0.00390625F)
				.endVertex();
		bufferbuilder.vertex(x + width, y + height, 1)
				.uv((textureX + width) * 0.00390625F,
						(textureY + height) * 0.00390625F)
				.endVertex();
		bufferbuilder.vertex(x + width, y + 0, 1)
				.uv((textureX + width) * 0.00390625F, (textureY + 0) * 0.00390625F)
				.endVertex();
		bufferbuilder.vertex(x + 0, y + 0, 1)
				.uv((textureX + 0) * 0.00390625F, (textureY + 0) * 0.00390625F)
				.endVertex();
		tessellator.end();
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
	public static void renderItemStackInGui(PoseStack ms, ItemStack stack, int x, int y) {
		transferMsToGl(ms, () -> Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(ms,stack, x, y));
	}

	// MULTIBLOCK STUFF
	public static void renderMultiBlock(PoseStack matrices, MultiblockPattern pattern, float partialTicks,
			BlockAndTintGetter getter, double relX, double relY) {
		matrices.pushPose();
		MultiBufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
		BlockEntityRenderDispatcher d = Minecraft.getInstance().getBlockEntityRenderDispatcher();
		pattern.getBlockPosBlockList().forEach((box) -> {
			box.render(pattern, matrices, partialTicks, getter, src, d);
		});
		matrices.popPose();
		PoseStack stack = RenderSystem.getModelViewStack();
		stack.pushPose();
		stack.translate(relX, relY, 100);
		stack.scale(8, 8, 8);
		stack.scale(1, -1, 1);
		RenderSystem.applyModelViewMatrix();
		Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
		stack.popPose();
		RenderSystem.applyModelViewMatrix();

	}

	public static void renderPatternInGUI(PoseStack ms, Minecraft mc, MultiblockPattern pattern, double xOff,
			double yOff) {
		PoseStack viewModelPose = RenderSystem.getModelViewStack();
		viewModelPose.pushPose();
		Lighting.setupFor3DItems();
		List<BlockPosBlockPair> patternList = pattern.getBlockPosBlockList();
		viewModelPose.scale(0.5f, 0.5f, -1f);
		viewModelPose.mulPose(new Quaternion(Vector3.YP, -5, true).toMoj());
		for (BlockPosBlockPair pair : patternList) {
			renderItemStackInGui(ms, new ItemStack(pair.getBlock()), pair.getPos().getX() * -16,
					pair.getPos().getZ() * 16);
		}
		viewModelPose.popPose();
	}

	public static void transferMsToGl(PoseStack ms, Runnable toRun) {
		PoseStack mvs = RenderSystem.getModelViewStack();
		mvs.pushPose();
		mvs.mulPoseMatrix(ms.last().pose());
		RenderSystem.applyModelViewMatrix();
		toRun.run();
		mvs.popPose();
		RenderSystem.applyModelViewMatrix();
	}

}
