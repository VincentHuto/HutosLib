/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
package com.vincenthuto.hutoslib.client.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class HLRenderHelper {
	public static final Identifier MC_BLOCK_SHEET = InventoryMenu.BLOCK_ATLAS;

	public static int color(FluidStack stack) {
		return !stack.isEmpty() && stack.getFluid() != null
				? IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack)
				: 0;
	}

	public static int density(FluidStack stack) {
		return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getDensity(stack) : 0;
	}

	public static TextureAtlasSprite getFluidTexture(Fluid fluid) {
		return getTexture(IClientFluidTypeExtensions.of(fluid).getStillTexture());
	}

	public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {
		return getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));
	}

	public static TextureAtlasSprite getTexture(Identifier location) {
		return textureMap().getSprite(location);
	}

	public static TextureAtlasSprite getTexture(String location) {
		return textureMap().getSprite(Identifier.parse(location));
	}

	public static Tesselator tesselator() {
		return Tesselator.getInstance();
	}

	public static TextureAtlas textureMap() {
		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
	}

	public static TextureAtlasSprite getSprite(Identifier rl) {
		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(rl);
	}

	// -----------------------------------------------------------------------
	// GUI fluid rendering — uses GuiGraphics + blitSprite in 1.21.11
	// -----------------------------------------------------------------------

	public static void drawRepeatedFluidSpriteGui(GuiGraphics graphics, FluidStack fluid, float x, float y, float w,
			float h) {
		IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
		TextureAtlasSprite sprite = getSprite(props.getStillTexture(fluid));
		int col = props.getTintColor(fluid);
		int iW = sprite.contents().width();
		int iH = sprite.contents().height();
		if (iW > 0 && iH > 0)
			drawRepeatedSpriteGui(graphics, (int) x, (int) y, (int) w, (int) h, iW, iH, sprite, col);
	}

	public static void drawRepeatedSpriteGui(GuiGraphics graphics, int x, int y, int w, int h, int iconWidth,
			int iconHeight, TextureAtlasSprite sprite, int color) {
		int iterMaxW = w / iconWidth;
		int iterMaxH = h / iconHeight;
		int leftoverW = w % iconWidth;
		int leftoverH = h % iconHeight;
		for (int ww = 0; ww <= iterMaxW; ww++) {
			int drawW = (ww < iterMaxW) ? iconWidth : leftoverW;
			if (drawW <= 0) continue;
			for (int hh = 0; hh <= iterMaxH; hh++) {
				int drawH = (hh < iterMaxH) ? iconHeight : leftoverH;
				if (drawH <= 0) continue;
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + ww * iconWidth, y + hh * iconHeight,
						drawW, drawH, color);
			}
		}
	}

	// -----------------------------------------------------------------------
	// 3D / world-render fluid/sprite helpers (use VertexConsumer + PoseStack)
	// -----------------------------------------------------------------------

	public static void drawRepeatedFluidSprite(VertexConsumer builder, PoseStack transform, FluidStack fluid, float x,
			float y, float w, float h) {
		IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
		TextureAtlasSprite sprite = getSprite(props.getStillTexture(fluid));
		int col = props.getTintColor(fluid);
		int iW = sprite.contents().width();
		int iH = sprite.contents().height();
		if (iW > 0 && iH > 0)
			drawRepeatedSprite(builder, transform, x, y, w, h, iW, iH, sprite.getU0(), sprite.getU1(), sprite.getV0(),
					sprite.getV1(), (col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f, 1);
	}

	public static void drawRepeatedSprite(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax, float r, float g,
			float b, float alpha) {
		int iterMaxW = (int) (w / iconWidth);
		int iterMaxH = (int) (h / iconHeight);
		float leftoverW = w % iconWidth;
		float leftoverH = h % iconHeight;
		float leftoverWf = leftoverW / (float) iconWidth;
		float leftoverHf = leftoverH / (float) iconHeight;
		float iconUDif = uMax - uMin;
		float iconVDif = vMax - vMin;
		for (int ww = 0; ww < iterMaxW; ww++) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + hh * iconHeight, iconWidth,
						iconHeight, r, g, b, alpha, uMin, uMax, vMin, vMax);
			drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + iterMaxH * iconHeight, iconWidth,
					leftoverH, r, g, b, alpha, uMin, uMax, vMin, (vMin + iconVDif * leftoverHf));
		}
		if (leftoverW > 0) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + hh * iconHeight, leftoverW,
						iconHeight, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin, vMax);
			drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + iterMaxH * iconHeight, leftoverW,
					leftoverH, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin,
					(vMin + iconVDif * leftoverHf));
		}
	}

	public static void drawTexturedColoredRect(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, float r, float g, float b, float alpha, float u0, float u1, float v0, float v1) {
		TransformingVertexBuilder innerBuilder = new TransformingVertexBuilder(builder, transform,
				DefaultVertexFormat.BLOCK);
		innerBuilder.defaultColor((int) (255 * r), (int) (255 * g), (int) (255 * b), (int) (255 * alpha));
		innerBuilder.setLight(LightTexture.pack(15, 15));
		innerBuilder.setOverlay(OverlayTexture.NO_OVERLAY);
		innerBuilder.setNormal(1, 1, 1);
		innerBuilder.addVertex(x, y + h, 0).setUv(u0, v1);
		innerBuilder.endVertex();
		innerBuilder.addVertex(x + w, y + h, 0).setUv(u1, v1);
		innerBuilder.endVertex();
		innerBuilder.addVertex(x + w, y, 0).setUv(u1, v0);
		innerBuilder.endVertex();
		innerBuilder.addVertex(x, y, 0).setUv(u0, v0);
		innerBuilder.endVertex();
		innerBuilder.unsetDefaultColor();
	}

}
