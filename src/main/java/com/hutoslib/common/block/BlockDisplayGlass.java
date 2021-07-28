package com.hutoslib.common.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.GlassBlock;

public class BlockDisplayGlass extends GlassBlock {
	public BlockDisplayGlass(BlockBehaviour.Properties properties) {
		super(properties.noOcclusion());
	}

}
