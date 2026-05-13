package com.vincenthuto.hutoslib.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HLDataGeneration {
	public static void gatherClientData(GatherDataEvent.Client event) {
		PackOutput packOutput = event.getGenerator().getPackOutput();

		event.addProvider(new HLModelProvider(packOutput));
		event.addProvider(new HLLanguageProvider(packOutput, "en_us"));
		addServerProviders(packOutput, event.getLookupProvider(), event::addProvider);
	}

	public static void gatherServerData(GatherDataEvent.Server event) {
		PackOutput packOutput = event.getGenerator().getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		addServerProviders(packOutput, lookupProvider, event::addProvider);
	}

	private static void addServerProviders(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
			Consumer<DataProvider> providers) {
		providers.accept(new HLBlockTagProvider(packOutput, lookupProvider));
		providers.accept(new HLItemTagProvider(packOutput, lookupProvider));
		providers.accept(new HLRecipeProvider.Runner(packOutput, lookupProvider));
		providers.accept(new HLRecipeJsonProvider(packOutput));
		providers.accept(new LootTableProvider(packOutput, Collections.emptySet(),
				List.of(new LootTableProvider.SubProviderEntry(HLBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
				lookupProvider));
	}
}
