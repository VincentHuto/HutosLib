package com.vincenthuto.hutoslib.client.render.block.render;

import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class RenderMultiBlockInGui {
	public static void renderPatternInGUI(PoseStack ms, Minecraft mc, LabeledBlockPattern pattern) {
		ms.pushPose();
		Lighting.setupFor3DItems();
		List<BlockPosBlockPair> patternList = pattern.getBlockPosBlockList();
		ms.mulPose(new Quaternion(Vector3f.ZERO, 45, true));
		ms.scale(0.5f, 0.5f, 0.5f);
		ms.translate(0, 2, 0);
		for (BlockPosBlockPair pair : patternList) {
			ms.translate(1, 1, pair.getPos().getZ());
			mc.getItemRenderer().renderAndDecorateFakeItem(new ItemStack(pair.getBlock()), pair.getPos().getX() * -16,
					pair.getPos().getY() * -16);

		}
		ms.popPose();

	}

}
