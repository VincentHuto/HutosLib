package com.vincenthuto.hutoslib.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayPedestalBlockEntity extends SimpleInventoryBlockEntity {


	public static void animTick(Level level, BlockPos pos, BlockState state, DisplayPedestalBlockEntity ent) {

	}

	public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
		super(HLBlockEntityInit.display_pedestal.get(), pos, state, 1);

	}



}
