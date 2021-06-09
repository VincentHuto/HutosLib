package com.hutoslib.common.tile;

import com.hutoslib.HutosLib;
import com.hutoslib.common.block.HutosLibBlockInit;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HutosLibTileEntityInit {
	
	public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister
			.create(ForgeRegistries.TILE_ENTITIES, HutosLib.MOD_ID);
	
	public static final RegistryObject<TileEntityType<TileDisplayPedestal>> display_pedestal = TILES.register("display_pedestal",
			() -> TileEntityType.Builder.create(TileDisplayPedestal::new, HutosLibBlockInit.display_pedestal.get()).build(null));
	
	
	
}
