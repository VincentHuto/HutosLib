package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HLItemModelProvider extends ItemModelProvider {
	public HLItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, HutosLib.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (RegistryObject<Block> b : HLBlockInit.BLOCKS.getEntries()) {
			registerBlockModel(b.get());
		}
		for (RegistryObject<Block> b : HLBlockInit.MODELEDBLOCKS.getEntries()) {
			registerBlockModel(b.get());
		}
		
		for (RegistryObject<Item> item : HLItemInit.ITEMS.getEntries()) {
			registerBasicItem(item.get());
		}
		for (RegistryObject<Item> item : HLItemInit.HANDHELDITEMS.getEntries()) {
			registerHandheldItem(item.get());
		}
	}

	private void registerBlockModel(Block block) {
		String path = ForgeRegistries.BLOCKS.getKey(block).getPath();
		getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
	}

	private void registerBasicItem(Item item) {
		String path = ForgeRegistries.ITEMS.getKey(item).getPath();
		singleTexture(path, mcLoc("item/generated"), "layer0", modLoc("item/" + path));

	}

	private void registerHandheldItem(Item item) {
		String path = ForgeRegistries.ITEMS.getKey(item).getPath();
		singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/" + path));
	}

	private void registerSpawnEggItem(Item item) {
		String path = ForgeRegistries.ITEMS.getKey(item).getPath();
		withExistingParent(path, mcLoc("item/template_spawn_egg"));
	}

	@Override
	public String getName() {
		return "Item Models";
	}
}
