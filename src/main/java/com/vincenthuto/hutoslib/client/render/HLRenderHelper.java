package com.vincenthuto.hutoslib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;

public class HLRenderHelper {
	public static int getFluidColor(FluidStack stack) {
		return 0xFFFFFFFF;
	}

	public static TextureAtlasSprite getFluidSprite(FluidStack fluid) {
		return null;
	}

	public static TextureAtlasSprite getSprite(Identifier texture) {
		return null;
	}

	public static void drawRepeatedFluidSpriteGui(Object buffer, Object transform, FluidStack fluid, int x, int y,
			int w, int h) {
	}

	public static void renderMultiBlock(PoseStack poseStack, MultiblockPattern pattern, float partialTicks,
			Object getter, double relX, double relY) {
	}

	public static void renderPatternInGUI(GuiGraphicsExtractor graphics, Minecraft minecraft, MultiblockPattern pattern,
			double xOff, double yOff) {
	}
}
