package com.vincenthuto.hutoslib.client.render.block;

import com.vincenthuto.hutoslib.math.BlockPosBlockPair;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class RenderMultiBlockInGui {
	public static void renderPatternInGUI(GuiGraphics graphics, MultiblockPattern pattern, int xOffset, int yOffset) {
		for (BlockPosBlockPair pair : pattern.getBlockPosBlockList()) {
			int x = xOffset + pair.getPos().getX() * -16;
			int y = yOffset + pair.getPos().getY() * -16;
			graphics.renderFakeItem(new ItemStack(pair.getBlock()), x, y);
		}
	}

}
