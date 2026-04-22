package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class HLBlockStateProvider extends BlockStateProvider {

	public HLBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, HutosLib.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(HLBlockInit.display_glass.get());
		for (var b : HLBlockInit.MODELEDBLOCKS.getEntries()) {
			ModelFile mf = new ModelFile(
					HutosLib.rloc("block/" + b.get().asItem().getDescriptionId().replace("block.hutoslib.", ""))) {

				@Override
				protected boolean exists() {
					return true;
				}
			};
			horizontalBlock(b.get(), mf);
		}
	}

}
