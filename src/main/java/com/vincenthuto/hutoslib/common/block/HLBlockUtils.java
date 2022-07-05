package com.vincenthuto.hutoslib.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class HLBlockUtils {

	public static RegistryObject<Block> registerBlockItems(String name, final Supplier<? extends Block> blockSup,
			Item.Properties itemProps, DeferredRegister<Block> blockReg, DeferredRegister<Item> itemReg) {
		RegistryObject<Block> regBlock = blockReg.register(name, blockSup);
		itemReg.register(name, () -> new BlockItem(regBlock.get(), itemProps));
		return regBlock;

	}

	public static boolean isCrop(LevelReader worldIn, BlockPos blockPos) {
		if (worldIn.getBlockState(blockPos).getBlock() instanceof BonemealableBlock) {
			if (worldIn.getBlockState(blockPos).getBlock() instanceof CropBlock) {
				return true;
			}
		}
		return false;

	}

	public static boolean isCropFullyGrown(LevelReader worldIn, BlockPos blockPos) {
		CropBlock crop = (CropBlock) worldIn.getBlockState(blockPos).getBlock();
		if (worldIn.getBlockState(blockPos).getProperties().contains(CropBlock.AGE)) {
			if (worldIn.getBlockState(blockPos).getValue(CropBlock.AGE) >= crop.getMaxAge()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBeetFullyGrown(LevelReader worldIn, BlockPos blockPos) {
		CropBlock crop = (CropBlock) worldIn.getBlockState(blockPos).getBlock();
		if ((crop instanceof BeetrootBlock)) {
			BeetrootBlock beets = (BeetrootBlock) crop;
			if (worldIn.getBlockState(blockPos).getProperties().contains(BeetrootBlock.AGE)) {
				if (worldIn.getBlockState(blockPos).getValue(BeetrootBlock.AGE) == beets.getMaxAge()) {
					return true;
				}
			}
		}
		return false;

	}

}
