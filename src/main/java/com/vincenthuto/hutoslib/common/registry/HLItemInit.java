package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.HLModTiers;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.item.ItemHLGuideBook;
import com.vincenthuto.hutoslib.common.item.ItemKnapper;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HLItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, HutosLib.MOD_ID);
	public static final DeferredRegister<Item> HANDHELDITEMS = DeferredRegister.create(Registries.ITEM, HutosLib.MOD_ID);
	public static final DeferredRegister<Item> SPECIALITEMS = DeferredRegister.create(Registries.ITEM,
			HutosLib.MOD_ID);
	public static final DeferredRegister<BannerPattern> BANNERPATTERNS = DeferredRegister
			.create(Registries.BANNER_PATTERN, HutosLib.MOD_ID);

	public static final TagKey<Item> TAG_KNAPPERS = TagKey.create(Registries.ITEM,
			HutosLib.rloc("knappers"));

	// Test Tome
	public static Identifier hl_guide_book_text = HutosLib.rloc(
			"textures/gui/guide/hl_guide_model.png");
	public static final DeferredHolder<Item, ? extends Item> hl_guide_book = SPECIALITEMS.register("hl_guide_book",
			() -> new ItemHLGuideBook(new Item.Properties().stacksTo(1),
					hl_guide_book_text));

	// Materials
	public static final DeferredHolder<Item, Item> raw_clay_flask = ITEMS.register("raw_clay_flask",
			() -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> cured_clay_flask = ITEMS.register("cured_clay_flask",
			() -> new Item(new Item.Properties()));

	// Karma
//	public static final DeferredHolder<Item, Item> node_of_actualization = ITEMS.register("node_of_actualization",
//			() -> new ItemNodeOfActualization(new Item.Properties()));

	// Knappers
	public static final DeferredHolder<Item, ? extends Item> iron_knapper = HANDHELDITEMS.register("iron_knapper",
			() -> new ItemKnapper(35f, 1, 0, HLModTiers.IRON, new Item.Properties()));
	public static final DeferredHolder<Item, ? extends Item> diamond_knapper = HANDHELDITEMS.register("diamond_knapper",
			() -> new ItemKnapper(50f, 1, 0, HLModTiers.DIAMOND,
					new Item.Properties()));
	public static final DeferredHolder<Item, Item> obsidian_flakes = ITEMS.register("obsidian_flakes",
			() -> new Item(new Item.Properties()));

	// Banners and Patterns

	public static final DeferredHolder<BannerPattern, BannerPattern> logo = BANNERPATTERNS.register("hutoslib_logo",
			() -> new BannerPattern(HutosLib.rloc("hutoslib_logo"), "hutoslib_logo"));

	public static final DeferredHolder<Item, Item> hutoslib_logo = ITEMS.register("hutoslib_logo",
			() -> new BannerPatternItem(
					TagKey.create(Registries.BANNER_PATTERN,
							HutosLib.rloc( "pattern_item/hutoslib_logo")),
					new Item.Properties()));

	public static final DeferredHolder<Item, ? extends Item> leather_arm_banner = SPECIALITEMS.register("leather_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.LEATHER,
					HutosLib.rloc( "textures/entity/arm_banner/leather_arm_banner.png")));
	public static final DeferredHolder<Item, ? extends Item> iron_arm_banner = SPECIALITEMS.register("iron_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.IRON,
					HutosLib.rloc( "textures/entity/arm_banner/iron_arm_banner.png")));
	public static final DeferredHolder<Item, ? extends Item> gold_arm_banner = SPECIALITEMS.register("gold_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.GOLD,
					HutosLib.rloc( "textures/entity/arm_banner/gold_arm_banner.png")));
	public static final DeferredHolder<Item, ? extends Item> diamond_arm_banner = SPECIALITEMS.register("diamond_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.DIAMOND,
					HutosLib.rloc( "textures/entity/arm_banner/diamond_arm_banner.png")));

	public static final DeferredHolder<Item, ? extends Item> obsidian_arm_banner = SPECIALITEMS.register("obsidian_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.DIAMOND,
					HutosLib.rloc( "textures/entity/arm_banner/obsidian_arm_banner.png")));

	public static final DeferredHolder<Item, ? extends Item> netherite_arm_banner = SPECIALITEMS.register("netherite_arm_banner",
			() -> new ItemArmBanner(new Item.Properties(), ArmorMaterials.NETHERITE,
					HutosLib.rloc( "textures/entity/arm_banner/netherite_arm_banner.png")));

}
