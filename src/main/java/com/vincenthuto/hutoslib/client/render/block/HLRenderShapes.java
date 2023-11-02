package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.client.HLClientUtils;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;

public class RenderShapes {
	public static double getSpeed(Player e) {
		Vector3 lastPos = new Vector3(e.xOld, e.yOld, e.zOld);
		Vector3 vertex = new Vector3(e.getX(), e.getY(), e.getZ());

		return Math.abs(lastPos.distanceTo(vertex) * 20d);
	}

	public static void renderSizedCube(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale) {

		renderSizedRectangle(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset, yOffset,
				zOffset, xScale, yScale, xScale);
	}

	public static void renderSizedCubes(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset, float yOffset, float zOffset,
			float xScale, float yScale) {

		for (int i = 0; i < amount; i++) {
			renderSizedCube(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale);
		}

	}

	public static void renderSizedHouse(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale, float zScale, float cubeXScale, float cubeYScale, float cubeZScale) {

		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);
		float pyramidXOffset = cubeXScale / cubeXScale - (xScale);
		float pyramidYOffset = cubeYScale;
		float pyramidZOffset = -cubeZScale / cubeZScale + (zScale);
		// Bottom
		builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0, -cubeZScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0, -cubeZScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// North
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0.0f, -cubeZScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -cubeZScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, cubeYScale, -cubeZScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, -cubeZScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, cubeYScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, -cubeZScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), cubeXScale, 0f, -cubeZScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, cubeYScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, cubeYScale, -cubeZScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -cubeZScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// Roof
		matrixStackIn.translate(pyramidXOffset, pyramidYOffset, pyramidZOffset);
		// North
		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0.0f, -zScale * cubeXScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale * cubeXScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f * cubeXScale, 0.0f, 0 * cubeXScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0f, 0 * cubeXScale).color(r, g, b, 255)
				.uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0.0f, 0 * cubeXScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0f, -zScale * cubeXScale).color(r, g, b, 255)
				.uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// West
		builder.vertex(matrixStackIn.last().pose(), 0f * cubeXScale, 0.0f, 0 * cubeXScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale * cubeXScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();
	}

	public static void renderSizedOctahedron(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float baseScale, float xOffset, float yOffset,
			float zOffset, float xScale, float yScale, float zScale) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Middle
		/*
		 * builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b,
		 * 255).uv(1, 1) .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 * builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0,
		 * 0).color(r, g, b, 255).uv(1, 0)
		 * .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 * builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0, -zScale *
		 * baseScale).color(r, g, b, 255) .uv(0,
		 * 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 * builder.vertex(matrixStackIn.last().pose(), 0 * baseScale, 0, -zScale *
		 * baseScale).color(r, g, b, 255) .uv(0,
		 * 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 */

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// West
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// Bottom Half
		// North
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// West
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedPanel(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale, float zScale) {

		matrixStackIn.pushPose();
		matrixStackIn.translate(-0.625, -0.1, 0.0625);
		Player player = HLClientUtils.getClientPlayer();
		float rotatSpeed = (float) (getSpeed(player) * 3);
		matrixStackIn.translate(0, rotatSpeed / 360, -rotatSpeed / 360);
		matrixStackIn.mulPose(Vector3.XP.rotationDegrees(rotatSpeed + 25).toMoj());
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset - 1, zOffset);
		double time = Minecraft.getInstance().level.getGameTime();
		float sin = (float) -Math.abs(Math.sin(time * 0.05) * 0.05);
		int length = 2;

		yScale *= 2;

		Vector3 vec = new Vector3(xScale, 0.0f, -zScale - sin);
		Vector3 vec1 = new Vector3(0f, 0, -zScale - sin);
		Vector3 vec2 = new Vector3(0f + 0, yScale, -zScale + 0);
		Vector3 vec3 = new Vector3(xScale + 0, yScale, -zScale + 0);

		for (int i = 0; i < length; i++) {

			builder.vertex(matrixStackIn.last().pose(), vec.x, vec.y, vec.z).color(r, g, b, 255).uv(1, 1)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).color(r, g, b, 255).uv(1, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).color(r, g, b, 255).uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).color(r, g, b, 255).uv(0, 1)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			float speedMult = (float) getSpeed(player) * 0.35f;
			speedMult = speedMult > 4 ? 4 : speedMult;
			float mod = -sin * (i * 2.15f) * speedMult;
			vec.add(0, 0, mod);
			vec1.add(0, 0, mod);
			vec2.add(0, 0.25f, 0);
			vec3.add(0, 0.25f, 0);

			matrixStackIn.translate(0, -0.25, 0);
			vec.setY(vec2.y + 0.25f);
			vec1.setY(vec3.y + 0.25f);

			builder.vertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).color(r, g, b, 255).uv(1, 1)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).color(r, g, b, 255).uv(1, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec.x, vec.y, vec.z).color(r, g, b, 255).uv(0, 0)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

			builder.vertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).color(r, g, b, 255).uv(0, 1)
					.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
					.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
			vec.add(0, -0.25f, 0);
			vec1.add(0, -0.25f, 0);
			matrixStackIn.translate(0, 0.25, 0);
			vec2.setY(vec2.y + 0.25f);
			vec3.setY(vec3.y + 0.25f);
		}

		// Top
		/*
		 * for (int i = 0; i < 3; i++) { builder.vertex(matrixStackIn.last().pose(),
		 * vec.x, vec.y, vec.z).color(r, g, b, 255) .uv(1,
		 * 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 *
		 * builder.vertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).color(r,
		 * g, b, 255) .uv(1,
		 * 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 *
		 * builder.vertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).color(r,
		 * g, b, 255) .uv(0,
		 * 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 *
		 * builder.vertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).color(r,
		 * g, b, 255) .uv(0,
		 * 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
		 * .normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		 *
		 * vec.add(0, 0.25f, 0); vec1.add(0, 0.25f, 0); vec2.add(0, 0.25f, 0);
		 * vec3.add(0, 0.25f, 0);
		 *
		 * }
		 */

		matrixStackIn.popPose();

	}

	public static void renderSizedPyramid(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float baseScale, float xOffset, float yOffset,
			float zOffset, float xScale, float yScale, float zScale) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Bottom
		builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0, -zScale * baseScale).color(r, g, b, 255)
				.uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0 * baseScale, 0, -zScale * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255)
				.uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).color(r, g, b, 255)
				.uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// West
		builder.vertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedRectangle(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale, float zScale) {

		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Top
		builder.vertex(matrixStackIn.last().pose(), 0, yScale, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, yScale, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// Bottom
		builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedRectangles(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset, float yOffset, float zOffset,
			float xScale, float yScale, float zScale) {

		for (int i = 0; i < amount; i++) {
			renderSizedRectangle(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale, zScale);
		}

	}

	public static void renderSizedSlantedCube(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale, float xSlant, float ySlant) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Top

		builder.vertex(matrixStackIn.last().pose(), 0, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, yScale * ySlant, -xScale + xSlant).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// Bottom
		builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, -xScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0, -xScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, -xScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -xScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -xScale + xSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, -xScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -xScale + xSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -xScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedSlantedCubes(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset, float yOffset, float zOffset,
			float xScale, float yScale, float xSlant, float ySlant) {

		for (int i = 0; i < amount; i++) {
			renderSizedSlantedCube(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale, xSlant, ySlant);
		}

	}

	public static void renderSizedSlantedRectangle(PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset,
			float zOffset, float xScale, float yScale, float zScale, float xSlant, float ySlant, float zSlant) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Top
		builder.vertex(matrixStackIn.last().pose(), 0, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, yScale * ySlant, -zScale + zSlant).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// Bottom
		builder.vertex(matrixStackIn.last().pose(), 0, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedSlantedRectangles(PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset,
			float yOffset, float zOffset, float xScale, float yScale, float zScale, float xSlant, float ySlant,
			float zSlant) {

		for (int i = 0; i < amount; i++) {
			renderSizedSlantedRectangle(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale, zScale, xSlant, ySlant, zSlant);
		}

	}

	public static void renderSizedSlantedTunnel(PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset,
			float zOffset, float xScale, float yScale, float zScale, float xSlant, float ySlant, float zSlant) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// North
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		matrixStackIn.popPose();

	}

	public static void renderSizedSlantedTunnels(PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset,
			float yOffset, float zOffset, float xScale, float yScale, float zScale, float xSlant, float ySlant,
			float zSlant) {
		for (int i = 0; i < amount; i++) {
			renderSizedSlantedTunnel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale, zScale, xSlant, ySlant, zSlant);
		}

	}

	public static void renderSizedTunnel(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, float xOffset, float yOffset, float zOffset, float xScale,
			float yScale, float zScale) {

		// Chest Panel
		matrixStackIn.pushPose();
		VertexConsumer builder = builderIn;
		int color = 0xB6B900;
		int r = color >> 16 & 255, g = color >> 8 & 255, b = color & 255;
		matrixStackIn.translate(xOffset, yOffset, zOffset);

		// Cube
		// Bottom
		// North
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, 0, -zScale).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// South
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, 0).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, 0).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();

		// East
		builder.vertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		// West
		builder.vertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).color(r, g, b, 255).uv(1, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, 0).color(r, g, b, 255).uv(1, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).color(r, g, b, 255).uv(0, 0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		builder.vertex(matrixStackIn.last().pose(), 0, 0f, -zScale).color(r, g, b, 255).uv(0, 1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLightIn)
				.normal(matrixStackIn.last().normal(), 0, 1, 0).endVertex();
		matrixStackIn.popPose();

	}

	public static void renderSizedTunnels(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
			int combinedOverlayIn, VertexConsumer builderIn, int amount, float xOffset, float yOffset, float zOffset,
			float xScale, float yScale, float zScale) {

		for (int i = 0; i < amount; i++) {
			renderSizedTunnel(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, builderIn, xOffset,
					(yOffset) * i, zOffset, xScale, yScale, zScale);
		}

	}

}
