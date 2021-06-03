package com.hutoslib.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.GlassBlock;

public class BlockDisplayGlass extends GlassBlock {
	public BlockDisplayGlass(AbstractBlock.Properties properties) {
		super(properties.notSolid());
	}

}
