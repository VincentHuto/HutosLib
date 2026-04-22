package com.vincenthuto.hutoslib.common.data;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vincenthuto.hutoslib.common.registry.HLBlockInit;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class HLBlockLootTableProvider extends BlockLootSubProvider {

    public HLBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(HLBlockInit.display_glass.get());
        dropSelf(HLBlockInit.display_pedestal.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(
            HLBlockInit.BLOCKS.getEntries().stream().map(e -> e.get()),
            HLBlockInit.MODELEDBLOCKS.getEntries().stream().map(e -> e.get())
        ).collect(Collectors.toList());
    }
}
