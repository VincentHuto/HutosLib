package com.vincenthuto.hutoslib.common.block;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HLBlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HutosLib.MOD_ID);
	public static final DeferredRegister<Item> BLOCKITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			HutosLib.MOD_ID);

	public static final RegistryObject<Block> display_pedestal = HLBlockUtils.registerBlockItems("display_pedestal",
			() -> new BlockDisplayPedestal(
					Block.Properties.of(Material.STONE).strength(50f, 2000f).sound(SoundType.STONE).noOcclusion()),
			new Item.Properties().tab(HutosLibItemGroup.instance), BLOCKS, BLOCKITEMS);

	public static final RegistryObject<Block> display_glass = HLBlockUtils.registerBlockItems("display_glass",
			() -> new BlockDisplayGlass(
					Block.Properties.of(Material.GLASS).strength(0.1f, 1f).sound(SoundType.GLASS).noOcclusion()),
			new Item.Properties().tab(HutosLibItemGroup.instance), BLOCKS, BLOCKITEMS);

}
