package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HLItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HutosLib.MOD_ID);
	public static final DeferredRegister<Item> SPECIALITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			HutosLib.MOD_ID);

	public static final TagKey<Item> TAG_KNAPPERS = TagKey.create(Registry.ITEM_REGISTRY,
			new ResourceLocation("hutoslib:knappers"));

	// Test Tome
	public static ResourceLocation hl_guide_book_text = new ResourceLocation(HutosLib.MOD_ID,
			"textures/gui/guide/hl_guide_model.png");
	public static final RegistryObject<Item> hl_guide_book = SPECIALITEMS.register("hl_guide_book",
			() -> new ItemHLGuideBook(new Item.Properties().tab(HutosLibItemGroup.instance).stacksTo(1),
					hl_guide_book_text));

	// Materials
	public static final RegistryObject<Item> raw_clay_flask = ITEMS.register("raw_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> cured_clay_flask = ITEMS.register("cured_clay_flask",
			() -> new Item(new Item.Properties().tab(HutosLibItemGroup.instance)));

	// Karma
	public static final RegistryObject<Item> node_of_actualization = ITEMS.register("node_of_actualization",
			() -> new ItemNodeOfActualization(new Item.Properties().tab(HutosLibItemGroup.instance)));

	// Knappers
	public static final RegistryObject<Item> iron_knapper = ITEMS.register("iron_knapper",
			() -> new ItemKnapper(35f, 1, 0, HLModTiers.IRON, new Item.Properties().tab(HutosLibItemGroup.instance)));
	public static final RegistryObject<Item> diamond_knapper = ITEMS.register("diamond_knapper",
			() -> new ItemKnapper(50f, 1, 0, HLModTiers.DIAMOND,
					new Item.Properties().tab(HutosLibItemGroup.instance)));
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
	
	public static final RegistryObject<Item> obsidian_arm_banner = SPECIALITEMS.register("obsidian_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.DIAMOND,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/obsidian_arm_banner.png")));
	
	public static final RegistryObject<Item> netherite_arm_banner = SPECIALITEMS.register("netherite_arm_banner",
			() -> new ItemArmBanner(new Item.Properties().tab(HutosLibItemGroup.instance), ArmorMaterials.NETHERITE,
					new ResourceLocation(HutosLib.MOD_ID, "textures/entity/arm_banner/netherite_arm_banner.png")));

}
