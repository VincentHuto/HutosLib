package com.vincenthuto.hutoslib.common.block.entity;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.HutosLibBlockInit;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HutosLibBlockEntityInit {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, HutosLib.MOD_ID);

	public static final RegistryObject<BlockEntityType<DisplayPedestalBlockEntity>> display_pedestal = BLOCK_ENTITIES
			.register("display_pedestal", () -> BlockEntityType.Builder
					.of(DisplayPedestalBlockEntity::new, HutosLibBlockInit.display_pedestal.get()).build(null));

}
