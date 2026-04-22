package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class HLItemModelProvider extends ItemModelProvider {
public HLItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
super(generator, HutosLib.MOD_ID, existingFileHelper);
}

@Override
protected void registerModels() {
for (var b : HLBlockInit.BLOCKS.getEntries()) {
registerBlockModel(b.get());
}
for (var b : HLBlockInit.MODELEDBLOCKS.getEntries()) {
registerBlockModel(b.get());
}

for (var item : HLItemInit.ITEMS.getEntries()) {
registerBasicItem(item.get());
}
for (var item : HLItemInit.HANDHELDITEMS.getEntries()) {
registerHandheldItem(item.get());
}
}

private void registerBlockModel(Block block) {
String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
}

private void registerBasicItem(Item item) {
String path = BuiltInRegistries.ITEM.getKey(item).getPath();
singleTexture(path, mcLoc("item/generated"), "layer0", modLoc("item/" + path));
}

private void registerHandheldItem(Item item) {
String path = BuiltInRegistries.ITEM.getKey(item).getPath();
singleTexture(path, mcLoc("item/handheld"), "layer0", modLoc("item/" + path));
}

@Override
public String getName() {
return "Item Models";
}
}
