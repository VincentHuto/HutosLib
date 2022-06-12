package com.vincenthuto.hutoslib.common.block;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.world.item.BlockItem;
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

	public static final RegistryObject<Block> display_pedestal = registerBlockItems("display_pedestal",
			() -> new BlockDisplayPedestal(
					Block.Properties.of(Material.STONE).strength(50f, 2000f).sound(SoundType.STONE).noOcclusion()));

	public static final RegistryObject<Block> display_glass = registerBlockItems("display_glass",
			() -> new BlockDisplayGlass(
					Block.Properties.of(Material.GLASS).strength(0.1f, 1f).sound(SoundType.GLASS).noOcclusion()));

	public static RegistryObject<Block> registerBlockItems(String name, final Supplier<? extends Block> sup) {
		RegistryObject<Block> regBlock = BLOCKS.register(name, sup);
		BLOCKITEMS.register(name,
				() -> new BlockItem(regBlock.get(), new Item.Properties().tab(HutosLibItemGroup.instance)));
		return regBlock;

	}

}
