package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class HLItemTagProvider extends ItemTagsProvider {

    public HLItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, HutosLib.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(HLItemInit.TAG_KNAPPERS)
            .add(HLItemInit.iron_knapper.get())
            .add(HLItemInit.diamond_knapper.get())
            .add(HLItemInit.netherite_knapper.get());
    }
}
