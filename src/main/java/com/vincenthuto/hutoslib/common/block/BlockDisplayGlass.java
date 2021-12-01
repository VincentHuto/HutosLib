package com.vincenthuto.hutoslib.common.block;

import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockDisplayGlass extends GlassBlock {
	public BlockDisplayGlass(BlockBehaviour.Properties properties) {
		super(properties.noOcclusion());
	}

}
