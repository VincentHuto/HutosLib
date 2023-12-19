package com.vincenthuto.hutoslib.client.render.block;

import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.math.BlockPosBlockPair;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public class RenderMultiBlockInGui {
	public static void renderPatternInGUI(PoseStack ms, Minecraft mc, MultiblockPattern pattern) {
		ms.pushPose();
		Lighting.setupFor3DItems();
		List<BlockPosBlockPair> patternList = pattern.getBlockPosBlockList();
		ms.mulPose(new Quaternion(Vector3.ZERO, 45, true).toMoj());
		ms.scale(0.5f, 0.5f, 0.5f);
		ms.translate(0, 2, 0);
		for (BlockPosBlockPair pair : patternList) {
			ms.translate(1, 1, pair.getPos().getZ());
		      MultiBufferSource.BufferSource multibuffersource$buffersource =mc.renderBuffers().bufferSource();
			GuiGraphics graph = new GuiGraphics(mc, multibuffersource$buffersource);
			graph.renderItem(new ItemStack(pair.getBlock()), pair.getPos().getX() * -16,
					pair.getPos().getY() * -16);

		}
		ms.popPose();

	}

}
