package com.hutoslib.common.item;

import com.hutoslib.HutosLib;
import com.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class HutosLibItemInit {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HutosLib.MOD_ID);

	public static final RegistryObject<Item> raw_clay_flask = ITEMS.register("raw_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> cured_clay_flask = ITEMS.register("cured_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));

	public static final RegistryObject<Item> iron_knapper = ITEMS.register("iron_knapper",
			() -> new ItemKnapper(25f, 1, 0, Tiers.IRON, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> diamond_knapper = ITEMS.register("diamond_knapper",
			() -> new ItemKnapper(50f, 1, 0, Tiers.DIAMOND, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> obsidian_flakes = ITEMS.register("obsidian_flakes",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));
	/*
	 * public static final RegistryObject<Item> stick_of_holding =
	 * ITEMS.register("stick_of_holding", () -> new ItemStickOfHolding(new
	 * Item.Properties().tab(HutosLibItemGroup.instance)));
	 */

}
