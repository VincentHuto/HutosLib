package com.vincenthuto.hutoslib.common.data;

import java.util.concurrent.CompletableFuture;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HLItemTagProvider extends ItemTagsProvider {

    public HLItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(packOutput, lookupProvider, blockTags.contentsGetter(), HutosLib.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}
