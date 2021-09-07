package com.hutoslib.common.block;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolAction;

public class HutosLibBlockMaterials {
    public static final ToolAction KNAPPER_DIG = ToolAction.get("knapper_dig");
    public static final Set<ToolAction> DEFAULT_KNAPPER_ACTIONS = Stream.of(KNAPPER_DIG).collect(Collectors.toCollection(Sets::newIdentityHashSet));
	public static final Material OBSIDIAN = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
	public static Tag.Named<Block> knapper = BlockTags.bind("mineable/knapper");
}
