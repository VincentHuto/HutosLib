package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.HLModTiers;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.item.ItemHLGuideBook;
import com.vincenthuto.hutoslib.common.item.ItemKnapper;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HLItemInit {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HutosLib.MOD_ID);
	public static final DeferredRegister.Items HANDHELDITEMS = DeferredRegister.createItems(HutosLib.MOD_ID);
	public static final DeferredRegister.Items SPECIALITEMS = DeferredRegister.createItems(HutosLib.MOD_ID);
	public static final DeferredRegister<BannerPattern> BANNERPATTERNS = DeferredRegister
			.create(Registries.BANNER_PATTERN, HutosLib.MOD_ID);

	public static final TagKey<Item> TAG_KNAPPERS = TagKey.create(Registries.ITEM,
			HutosLib.rloc("knappers"));

	// Test Tome
	public static Identifier hl_guide_book_text = HutosLib.rloc(
			"textures/gui/guide/hl_guide_model.png");
	public static final DeferredItem<? extends Item> hl_guide_book = SPECIALITEMS.registerItem("hl_guide_book",
			properties -> new ItemHLGuideBook(properties.stacksTo(1),
					hl_guide_book_text)
					.withBookPrefix("guide/"));

	// Materials
	public static final DeferredItem<Item> raw_clay_flask = ITEMS.registerSimpleItem("raw_clay_flask");
	public static final DeferredItem<Item> cured_clay_flask = ITEMS.registerSimpleItem("cured_clay_flask");

	// Block items
	public static final DeferredItem<BlockItem> display_glass = ITEMS.registerSimpleBlockItem(HLBlockInit.display_glass);
	public static final DeferredItem<BlockItem> display_pedestal = ITEMS.registerSimpleBlockItem(HLBlockInit.display_pedestal);

	// Karma
//	public static final DeferredHolder<Item, Item> node_of_actualization = ITEMS.register("node_of_actualization",
//			() -> new ItemNodeOfActualization(new Item.Properties()));

	// Knappers
	public static final DeferredItem<? extends Item> iron_knapper = HANDHELDITEMS.registerItem("iron_knapper",
			properties -> new ItemKnapper(35f, 1, 0, HLModTiers.IRON, properties));
	public static final DeferredItem<? extends Item> diamond_knapper = HANDHELDITEMS.registerItem("diamond_knapper",
			properties -> new ItemKnapper(50f, 1, 0, HLModTiers.DIAMOND, properties));
	public static final DeferredItem<? extends Item> netherite_knapper = HANDHELDITEMS.registerItem("netherite_knapper",
			properties -> new ItemKnapper(65f, 1, 0, HLModTiers.NETHERITE, properties));
	public static final DeferredItem<Item> obsidian_flakes = ITEMS.registerSimpleItem("obsidian_flakes");

	// Banners and Patterns

	public static final DeferredHolder<BannerPattern, BannerPattern> logo = BANNERPATTERNS.register("hutoslib_logo",
			() -> new BannerPattern(HutosLib.rloc("hutoslib_logo"), "hutoslib_logo"));

	public static final DeferredItem<Item> hutoslib_logo = ITEMS.registerSimpleItem("hutoslib_logo");

	public static final DeferredItem<? extends Item> leather_arm_banner = SPECIALITEMS.registerItem("leather_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.LEATHER,
					HutosLib.rloc( "textures/entity/arm_banner/leather_arm_banner.png")));
	public static final DeferredItem<? extends Item> iron_arm_banner = SPECIALITEMS.registerItem("iron_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.IRON,
					HutosLib.rloc( "textures/entity/arm_banner/iron_arm_banner.png")));
	public static final DeferredItem<? extends Item> gold_arm_banner = SPECIALITEMS.registerItem("gold_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.GOLD,
					HutosLib.rloc( "textures/entity/arm_banner/gold_arm_banner.png")));
	public static final DeferredItem<? extends Item> diamond_arm_banner = SPECIALITEMS.registerItem("diamond_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.DIAMOND,
					HutosLib.rloc( "textures/entity/arm_banner/diamond_arm_banner.png")));

	public static final DeferredItem<? extends Item> obsidian_arm_banner = SPECIALITEMS.registerItem("obsidian_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.DIAMOND,
					HutosLib.rloc( "textures/entity/arm_banner/obsidian_arm_banner.png")));

	public static final DeferredItem<? extends Item> netherite_arm_banner = SPECIALITEMS.registerItem("netherite_arm_banner",
			properties -> new ItemArmBanner(properties, ArmorMaterials.NETHERITE,
					HutosLib.rloc( "textures/entity/arm_banner/netherite_arm_banner.png")));

}
