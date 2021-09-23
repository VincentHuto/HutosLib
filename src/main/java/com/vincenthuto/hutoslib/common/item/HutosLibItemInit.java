package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HutosLibItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HutosLib.MOD_ID);
	public static final DeferredRegister<Item> SPECIALITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			HutosLib.MOD_ID);

	// Materials
	public static final RegistryObject<Item> raw_clay_flask = ITEMS.register("raw_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> cured_clay_flask = ITEMS.register("cured_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));

	// Knappers
	public static final RegistryObject<Item> iron_knapper = ITEMS.register("iron_knapper",
			() -> new ItemKnapper(25f, 1, 0, Tiers.IRON, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> diamond_knapper = ITEMS.register("diamond_knapper",
			() -> new ItemKnapper(50f, 1, 0, Tiers.DIAMOND, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> obsidian_flakes = ITEMS.register("obsidian_flakes",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));

	// Banners and Patterns
	public static final BannerPattern logo = BannerPattern.create("hutoslib_logo".toUpperCase(), "hutoslib_logo",
			"logo", true);
	public static final RegistryObject<Item> logo_pattern = ITEMS.register("logo_pattern",
			() -> new BannerPatternItem(logo, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> leather_arm_banner = SPECIALITEMS.register("leather_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.LEATHER,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/leather_arm_banner.png")));
	public static final RegistryObject<Item> iron_arm_banner = SPECIALITEMS.register("iron_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.IRON,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/iron_arm_banner.png")));
	public static final RegistryObject<Item> gold_arm_banner = SPECIALITEMS.register("gold_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.GOLD,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/gold_arm_banner.png")));
	public static final RegistryObject<Item> diamond_arm_banner = SPECIALITEMS.register("diamond_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.DIAMOND,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/diamond_arm_banner.png")));
	public static final RegistryObject<Item> netherite_arm_banner = SPECIALITEMS.register("netherite_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.NETHERITE,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/netherite_arm_banner.png")));

}
