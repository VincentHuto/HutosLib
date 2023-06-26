/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
package com.vincenthuto.hutoslib.client.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class HLRenderHelper {
	public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");

	public static int color(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null
				? IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack)
				: 0;
	}

	public static int density(FluidStack stack) {

		return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getDensity(stack) : 0;
	}

	public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {
		if (fluid.isEmpty()) {
			return;
		}
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		int color = color(fluid);
		setPosTexShader();
		setBlockTextureSheet();
		setSahderColorFromInt(color);
		drawTiledTexture(x, y, getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid)),
				width, height);
	}

	public static void drawScaledTexturedModalRectFromSprite(int x, int y, TextureAtlasSprite icon, int width,
			int height) {

		if (icon == null) {
			return;
		}
		float minU = icon.getU0();
		float maxU = icon.getU1();
		float minV = icon.getV0();
		float maxV = icon.getV1();

		float u = minU + (maxU - minU) * width / 16F;
		float v = minV + (maxV - minV) * height / 16F;

		BufferBuilder buffer = tesselator().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x, y + height, 0).uv(minU, v).endVertex();
		buffer.vertex(x + width, y + height, 0).uv(u, v).endVertex();
		buffer.vertex(x + width, y, 0).uv(u, minV).endVertex();
		buffer.vertex(x, y, 0).uv(minU, minV).endVertex();

		tesselator().end();
	}

	public static void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {
		int drawHeight;
		int drawWidth;
		for (int i = 0; i < width; i += 16) {
			for (int j = 0; j < height; j += 16) {
				drawWidth = Math.min(width - i, 16);
				drawHeight = Math.min(height - j, 16);
				drawScaledTexturedModalRectFromSprite(x + i, y + j, icon, drawWidth, drawHeight);
			}
		}
		resetShaderColor();
	}

	public static TextureAtlasSprite getFluidTexture(Fluid fluid) {

		return getTexture(IClientFluidTypeExtensions.of(fluid).getStillTexture());
	}

	public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {

		return getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));
	}

	public static TextureAtlasSprite getTexture(ResourceLocation location) {

		return textureMap().getSprite(location);
	}

	public static TextureAtlasSprite getTexture(String location) {

		return textureMap().getSprite(new ResourceLocation(location));
	}

	public static void resetShaderColor() {

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void setBlockTextureSheet() {

		setShaderTexture0(MC_BLOCK_SHEET);
	}

	public static void setPosTexShader() {

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	public static void setSahderColorFromInt(int color) {

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		RenderSystem.setShaderColor(red, green, blue, 1.0F);
	}

	private static void setShaderTexture0(ResourceLocation mcBlockSheet) {
		RenderSystem.setShaderTexture(0, mcBlockSheet);
	}

	public static Tesselator tesselator() {

		return Tesselator.getInstance();
	}

	public static TextureAtlas textureMap() {

		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
	}

	public static void drawRepeatedFluidSpriteGui(MultiBufferSource buffer, PoseStack transform, FluidStack fluid,
			float x, float y, float w, float h) {
		RenderType renderType = HLRenderStateShards.getGuiTranslucent(InventoryMenu.BLOCK_ATLAS);
		VertexConsumer builder = buffer.getBuffer(renderType);
		drawRepeatedFluidSprite(builder, transform, fluid, x, y, w, h);
	}

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
		innerBuilder.vertex(x, y + h, 0).uv(u0, v1).endVertex();
		innerBuilder.vertex(x + w, y + h, 0).uv(u1, v1).endVertex();
		innerBuilder.vertex(x + w, y, 0).uv(u1, v0).endVertex();
		innerBuilder.vertex(x, y, 0).uv(u0, v0).endVertex();
		innerBuilder.unsetDefaultColor();
	}

	public static TextureAtlasSprite getSprite(ResourceLocation rl) {
		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(rl);
	}

}
