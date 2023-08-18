package com.vincenthuto.hutoslib.common.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGeneration {
	public static void generate(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeClient(),	new HLBlockStateProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new HLLanguageProvider(packOutput, "en_us"));
		generator.addProvider(event.includeClient(),new HLItemModelProvider(packOutput, event.getExistingFileHelper()));
		HLBlockTagProvider blockTags = new HLBlockTagProvider(packOutput, lookupProvider, event.getExistingFileHelper());
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(),new HLItemTagProvider(packOutput, lookupProvider, blockTags, event.getExistingFileHelper()));
		generator.addProvider(event.includeServer(), new HLRecipeProvider(packOutput));
		generator.addProvider(event.includeServer(),new LootTableProvider(packOutput, Collections.emptySet(),
				List.of(new LootTableProvider.SubProviderEntry(HLBlockLootTableProvider::new, LootContextParamSets.BLOCK))));
	}

}
