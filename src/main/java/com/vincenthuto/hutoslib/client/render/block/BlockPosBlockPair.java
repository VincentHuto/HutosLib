package com.vincenthuto.hutoslib.client.render.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockPosBlockPair {
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

	@Override
	public String toString() {
		return block.toString() + "," + pos.toString();
	}
}