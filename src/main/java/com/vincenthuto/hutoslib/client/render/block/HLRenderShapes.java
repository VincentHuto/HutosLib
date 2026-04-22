package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.client.HLClientUtils;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;

public class HLRenderShapes {
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
		builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0, -cubeZScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0, -cubeZScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// North
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0.0f, -cubeZScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -cubeZScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, cubeYScale, -cubeZScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, -cubeZScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, cubeYScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, cubeYScale, -cubeZScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), cubeXScale, 0f, -cubeZScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, cubeYScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, cubeYScale, -cubeZScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -cubeZScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// Roof
		matrixStackIn.translate(pyramidXOffset, pyramidYOffset, pyramidZOffset);
		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0.0f, -zScale * cubeXScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale * cubeXScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f * cubeXScale, 0.0f, 0 * cubeXScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0f, 0 * cubeXScale).setColor(r, g, b, 255)
				.setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0.0f, 0 * cubeXScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale, 0f, -zScale * cubeXScale).setColor(r, g, b, 255)
				.setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f * cubeXScale, 0.0f, 0 * cubeXScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * cubeXScale / 2, yScale, -zScale * cubeXScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale * cubeXScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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
		 * builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b,
		 * 255).setUv(1, 1) .setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 * builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0,
		 * 0).setColor(r, g, b, 255).setUv(1, 0)
		 * .setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 * builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0, -zScale *
		 * baseScale).setColor(r, g, b, 255) .setUv(0,
		 * 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 * builder.addVertex(matrixStackIn.last().pose(), 0 * baseScale, 0, -zScale *
		 * baseScale).setColor(r, g, b, 255) .setUv(0,
		 * 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 */

		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// Bottom Half
		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, -yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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

			builder.addVertex(matrixStackIn.last().pose(), vec.x, vec.y, vec.z).setColor(r, g, b, 255).setUv(1, 1)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).setColor(r, g, b, 255).setUv(1, 0)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).setColor(r, g, b, 255).setUv(0, 0)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).setColor(r, g, b, 255).setUv(0, 1)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

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

			builder.addVertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).setColor(r, g, b, 255).setUv(1, 1)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).setColor(r, g, b, 255).setUv(1, 0)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec.x, vec.y, vec.z).setColor(r, g, b, 255).setUv(0, 0)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);

			builder.addVertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).setColor(r, g, b, 255).setUv(0, 1)
					.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
					.setNormal(0, 1, 0);
			vec.add(0, -0.25f, 0);
			vec1.add(0, -0.25f, 0);
			matrixStackIn.translate(0, 0.25, 0);
			vec2.setY(vec2.y + 0.25f);
			vec3.setY(vec3.y + 0.25f);
		}

		// Top
		/*
		 * for (int i = 0; i < 3; i++) { builder.addVertex(matrixStackIn.last().pose(),
		 * vec.x, vec.y, vec.z).setColor(r, g, b, 255) .setUv(1,
		 * 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 *
		 * builder.addVertex(matrixStackIn.last().pose(), vec1.x, vec1.y, vec1.z).setColor(r,
		 * g, b, 255) .setUv(1,
		 * 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 *
		 * builder.addVertex(matrixStackIn.last().pose(), vec2.x, vec2.y, vec2.z).setColor(r,
		 * g, b, 255) .setUv(0,
		 * 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
		 *
		 * builder.addVertex(matrixStackIn.last().pose(), vec3.x, vec3.y, vec3.z).setColor(r,
		 * g, b, 255) .setUv(0,
		 * 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
		 * .setNormal(0, 1, 0);
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
		builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0 * baseScale, 0, -zScale * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale * baseScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, 0 * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255)
				.setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale, 0f, -zScale * baseScale).setColor(r, g, b, 255)
				.setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f * baseScale, 0.0f, 0 * baseScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale * baseScale / 2, yScale, -zScale * baseScale / 2)
				.setColor(r, g, b, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale * baseScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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
		builder.addVertex(matrixStackIn.last().pose(), 0, yScale, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// Bottom
		builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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

		builder.addVertex(matrixStackIn.last().pose(), 0, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, yScale * ySlant, -xScale + xSlant).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// Bottom
		builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, -xScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0, -xScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, -xScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -xScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -xScale + xSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -xScale + xSlant)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, -xScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -xScale + xSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -xScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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
		builder.addVertex(matrixStackIn.last().pose(), 0, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, yScale * ySlant, -zScale + zSlant).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// Bottom
		builder.addVertex(matrixStackIn.last().pose(), 0, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// North
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.setColor(r, g, b, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale * xSlant, yScale * ySlant, -zScale + zSlant)
				.setColor(r, g, b, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale * ySlant, -zScale + zSlant).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

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
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, -zScale).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0, -zScale).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// South
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, 0).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, 0).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);

		// East
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), xScale, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		// West
		builder.addVertex(matrixStackIn.last().pose(), 0f, 0.0f, 0).setColor(r, g, b, 255).setUv(1, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, 0).setColor(r, g, b, 255).setUv(1, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0f, yScale, -zScale).setColor(r, g, b, 255).setUv(0, 0)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
		builder.addVertex(matrixStackIn.last().pose(), 0, 0f, -zScale).setColor(r, g, b, 255).setUv(0, 1)
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLightIn)
				.setNormal(0, 1, 0);
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
