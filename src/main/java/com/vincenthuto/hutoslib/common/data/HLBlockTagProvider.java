package com.vincenthuto.hutoslib.common.data;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HLBlockTagProvider extends BlockTagsProvider {

    public HLBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HutosLib.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(HLBlockInit.display_pedestal.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(HLBlockInit.display_pedestal.get());
	}
}