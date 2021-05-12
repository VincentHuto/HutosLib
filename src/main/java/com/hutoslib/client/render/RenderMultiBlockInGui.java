package com.hutoslib.client.render;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class RenderMultiBlockInGui {
	@SuppressWarnings("deprecation")
	public static void renderPatternInGUI(MatrixStack ms, Minecraft mc, LabeledBlockPattern pattern) {
		GlStateManager.pushMatrix();
		ms.push();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableBlend();
		List<BlockPosBlockPair> patternList = pattern.getBlockPosBlockList();
		GlStateManager.rotatef(45, 0, 0, 0);
		GlStateManager.scaled(0.5, 0.5, 0.5);

		GlStateManager.translatef(0, 2, 0);
		for (BlockPosBlockPair pair : patternList) {
			GlStateManager.translatef(1, 1, pair.getPos().getZ());
			mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(pair.getBlock()), pair.getPos().getX() * -16,
					pair.getPos().getY() * -16);
		}
		GlStateManager.disableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.popMatrix();
		ms.pop();
		GlStateManager.popMatrix();

	}

}