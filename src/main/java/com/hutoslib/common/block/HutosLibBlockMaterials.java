package com.hutoslib.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class HutosLibBlockMaterials {
	public static final Material OBSIDIAN = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
	public static Tag.Named<Block> knapper = BlockTags.bind("mineable/knapper");
}
