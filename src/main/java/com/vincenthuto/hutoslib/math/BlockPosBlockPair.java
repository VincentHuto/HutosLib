package com.vincenthuto.hutoslib.math;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPosBlockPair {
	@SuppressWarnings("deprecation")
	private static void renderBlock(PoseStack matrices, MultiblockPattern pattern, BlockState state, double translatex,
			double translatey, double translatez, BlockAndTintGetter getter) {
		matrices.pushPose();
		// Make sure to subtract 0.5 from it because thats the width of the block to get
		// the true center
		matrices.translate((translatex - (pattern.getBlockPattern().getWidth() / 2)) - 0.5,
				(translatey - (pattern.getBlockPattern().getHeight() / 2)) - 0.5,
				(translatez - (pattern.getBlockPattern().getDepth() / 2)) - 0.5);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrices,
				Minecraft.getInstance().renderBuffers().bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY);
		matrices.popPose();
	}
	Block block;

	BlockPos pos;

	public BlockPosBlockPair(Block block, BlockPos pos) {
		this.block = block;
		this.pos = pos;
	}

	public Block getBlock() {
		return block;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void render(MultiblockPattern pattern, PoseStack matrices, float partialTicks, BlockAndTintGetter getter,
			MultiBufferSource src, BlockEntityRenderDispatcher d) {
		if (block != null) {
			renderBlock(matrices, pattern, block.defaultBlockState(), pos.getX(), pos.getY(), pos.getZ(), getter);
		}
	}

	@Override
	public String toString() {
		return block.toString() + "," + pos.toString();
	}
}