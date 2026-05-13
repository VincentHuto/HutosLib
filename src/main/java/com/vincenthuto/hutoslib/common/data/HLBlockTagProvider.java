package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemKnapper;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class HLBlockTagProvider extends BlockTagsProvider {

    public HLBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, HutosLib.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(HLBlockInit.display_pedestal.get())
				.add(HLBlockInit.display_glass.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(HLBlockInit.display_pedestal.get());
		tag(ItemKnapper.EFFECTIVE_ON)
				.add(Blocks.OBSIDIAN)
				.add(Blocks.CRYING_OBSIDIAN);
	}
}
