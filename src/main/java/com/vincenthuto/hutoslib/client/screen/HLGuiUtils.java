package com.vincenthuto.hutoslib.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class HLGuiUtils {
	public static void drawMaxWidthString(Font font, Component text, int x, int y, int maxWidth, int color,
			boolean drawShadow) {
	}

	public static void drawMaxWidthString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y,
			int maxWidth, int color, boolean drawShadow) {
		graphics.textWithWordWrap(font, text, x, y, maxWidth, color, drawShadow);
	}

	public static void renderItemStackInGui(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
		graphics.fakeItem(stack, x, y);
	}

	public static void renderMultiBlock(PoseStack poseStack, MultiblockPattern pattern, float partialTicks,
			Object getter, double relX, double relY) {
	}

	public static void renderPatternInGUI(GuiGraphicsExtractor graphics, Minecraft mc, MultiblockPattern pattern,
			double xOff, double yOff) {
	}
}
