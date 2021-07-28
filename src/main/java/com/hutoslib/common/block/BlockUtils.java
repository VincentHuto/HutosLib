package com.hutoslib.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;

public class BlockUtils {

	public static boolean isCrop(LevelReader worldIn, BlockPos blockPos) {
		if (worldIn.getBlockState(blockPos).getBlock() instanceof BonemealableBlock) {
			if (worldIn.getBlockState(blockPos).getBlock() instanceof CropBlock) {
				return true;
			}
		}
		return false;

	}

	/*
	 * public static boolean isCropFullyGrown(LevelReader worldIn, BlockPos
	 * blockPos) { CropBlock crop = (CropBlock)
	 * worldIn.getBlockState(blockPos).getBlock(); if
	 * (worldIn.getBlockState(blockPos).getProperties().contains(CropBlock.AGE)) {
	 * if (worldIn.getBlockState(blockPos).get(CropBlock.AGE) >= crop.getMaxAge()) {
	 * return true; } } return false; }
	 * 
	 * public static boolean isBeetFullyGrown(LevelReader worldIn, BlockPos
	 * blockPos) { CropBlock crop = (CropsBlock)
	 * worldIn.getBlockState(blockPos).getBlock(); if ((crop instanceof
	 * BeetrootBlock)) { BeetrootBlock beets = (BeetrootBlock) crop; if
	 * (worldIn.getBlockState(blockPos).getProperties().contains(BeetrootBlock.AGE))
	 * { if (worldIn.getBlockState(blockPos).get(BeetrootBlock.AGE) ==
	 * beets.getMaxAge()) { return true; } } } return false;
	 * 
	 * }
	 */

}
