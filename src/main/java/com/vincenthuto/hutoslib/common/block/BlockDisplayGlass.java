package com.vincenthuto.hutoslib.common.block;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockDisplayGlass extends HalfTransparentBlock {
	public BlockDisplayGlass(BlockBehaviour.Properties properties) {
		super(properties.noOcclusion());
	}

}
