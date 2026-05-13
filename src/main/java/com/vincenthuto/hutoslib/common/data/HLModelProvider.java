package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class HLModelProvider extends ModelProvider {
	public HLModelProvider(PackOutput output) {
		super(output, HutosLib.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		registerBlockStates(blockModels);
		registerBlockItems(blockModels);
		registerItems(itemModels);
	}

	private static void registerBlockStates(BlockModelGenerators blockModels) {
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
				HLBlockInit.display_glass.get(),
				BlockModelGenerators.plainVariant(HutosLib.rloc("block/display_glass"))));
		blockModels.blockStateOutput.accept(MultiVariantGenerator
				.dispatch(HLBlockInit.display_pedestal.get(),
						BlockModelGenerators.plainVariant(HutosLib.rloc("block/display_pedestal")))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
	}

	private static void registerBlockItems(BlockModelGenerators blockModels) {
		blockModels.registerSimpleItemModel(HLBlockInit.display_glass.get(), HutosLib.rloc("block/display_glass"));
		blockModels.registerSimpleItemModel(HLBlockInit.display_pedestal.get(), HutosLib.rloc("block/display_pedestal"));
	}

	private static void registerItems(ItemModelGenerators itemModels) {
		HLItemInit.ITEMS.getEntries().stream()
				.map(Holder::value)
				.filter(item -> !(item instanceof BlockItem))
				.forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
		HLItemInit.HANDHELDITEMS.getEntries().forEach(item ->
				itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM));
		HLItemInit.SPECIALITEMS.getEntries().forEach(item ->
				itemModels.itemModelOutput.accept(
						item.get(),
						ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item.get()))));
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.concat(
				HLBlockInit.BLOCKS.getEntries().stream(),
				HLBlockInit.MODELEDBLOCKS.getEntries().stream());
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		return Stream.of(
						HLItemInit.ITEMS.getEntries().stream(),
						HLItemInit.HANDHELDITEMS.getEntries().stream(),
						HLItemInit.SPECIALITEMS.getEntries().stream())
				.flatMap(stream -> stream);
	}
}
