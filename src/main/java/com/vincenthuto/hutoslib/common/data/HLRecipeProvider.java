package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class HLRecipeProvider extends RecipeProvider {
	public HLRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
		generateItemRecipes();
		generateToolRecipes();
		generateArmBannerRecipes();
		generateBlockRecipes();
	}

	private void generateItemRecipes() {
		shaped(RecipeCategory.MISC, Blocks.CRYING_OBSIDIAN)
				.pattern(" X ")
				.pattern("XLX")
				.pattern(" X ")
				.define('X', HLItemInit.obsidian_flakes.get())
				.define('L', Items.LAPIS_LAZULI)
				.unlockedBy("has_obsidian_flakes", has(HLItemInit.obsidian_flakes.get()))
				.save(output, recipeKey("crying_obsidian_obsidian_flakes"));

		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(HLItemInit.raw_clay_flask.get()),
						RecipeCategory.MISC,
						CookingBookCategory.MISC,
						HLItemInit.cured_clay_flask.get(),
						1.0F,
						200)
				.unlockedBy("has_raw_clay_flask", has(HLItemInit.raw_clay_flask.get()))
				.save(output, recipeKey("cured_clay_flask"));

		shapeless(RecipeCategory.MISC, HLItemInit.hl_guide_book.get())
				.requires(Items.BOOK)
				.requires(Blocks.OBSIDIAN)
				.unlockedBy("has_book", has(Items.BOOK))
				.save(output, recipeKey("hutoslib_guide"));

		shapeless(RecipeCategory.MISC, HLItemInit.hutoslib_logo.get())
				.requires(Items.PAPER)
				.requires(HLItemInit.obsidian_flakes.get())
				.unlockedBy("has_obsidian_flakes", has(HLItemInit.obsidian_flakes.get()))
				.save(output, recipeKey("hutoslib_pattern"));

		shaped(RecipeCategory.MISC, Blocks.OBSIDIAN)
				.pattern("XX")
				.pattern("XX")
				.define('X', HLItemInit.obsidian_flakes.get())
				.unlockedBy("has_obsidian_flakes", has(HLItemInit.obsidian_flakes.get()))
				.save(output, recipeKey("obsidian_obsidian_flakes"));

		shaped(RecipeCategory.MISC, HLItemInit.raw_clay_flask.get())
				.pattern(" C ")
				.pattern("C C")
				.pattern("CCC")
				.define('C', Items.CLAY_BALL)
				.unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
				.save(output, recipeKey("raw_clay_flask"));
	}

	private void generateToolRecipes() {
		shaped(RecipeCategory.MISC, HLItemInit.diamond_knapper.get())
				.pattern("  P")
				.pattern(" N ")
				.pattern("N  ")
				.define('N', Items.STICK)
				.define('P', Items.DIAMOND)
				.unlockedBy("has_diamond", has(Items.DIAMOND))
				.save(output, recipeKey("diamond_knapper"));

		shaped(RecipeCategory.MISC, HLItemInit.iron_knapper.get())
				.pattern("  P")
				.pattern(" N ")
				.pattern("N  ")
				.define('N', Items.STICK)
				.define('P', Tags.Items.INGOTS_IRON)
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.save(output, recipeKey("iron_knapper"));

		SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
						Ingredient.of(HLItemInit.diamond_knapper.get()),
						Ingredient.of(Items.NETHERITE_INGOT),
						RecipeCategory.TOOLS,
						HLItemInit.netherite_knapper.get())
				.unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
				.save(output, recipeKey("netherite_knapper"));
	}

	private void generateArmBannerRecipes() {
		armBanner("diamond_arm_banner_forge", HLItemInit.diamond_arm_banner.get(), Items.DIAMOND);
		armBanner("gold_arm_banner_forge", HLItemInit.gold_arm_banner.get(), Items.GOLD_INGOT);
		armBanner("iron_arm_banner_forge", HLItemInit.iron_arm_banner.get(), Items.IRON_INGOT);
		armBanner("leather_arm_banner_forge", HLItemInit.leather_arm_banner.get(), Items.LEATHER);
		armBanner("obsidian_arm_banner_forge", HLItemInit.obsidian_arm_banner.get(), Blocks.OBSIDIAN);

		SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
						Ingredient.of(HLItemInit.diamond_arm_banner.get()),
						Ingredient.of(Items.NETHERITE_INGOT),
						RecipeCategory.MISC,
						HLItemInit.netherite_arm_banner.get())
				.unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
				.save(output, recipeKey("netherite_arm_banner"));
	}

	private void generateBlockRecipes() {
		shaped(RecipeCategory.MISC, HLBlockInit.display_glass.get(), 4)
				.pattern(" C ")
				.pattern("CSC")
				.pattern(" C ")
				.define('C', Blocks.GLASS)
				.define('S', Items.GLOWSTONE_DUST)
				.unlockedBy("has_glowstone_dust", has(Items.GLOWSTONE_DUST))
				.save(output, recipeKey("display_glass"));

		shaped(RecipeCategory.MISC, HLBlockInit.display_pedestal.get())
				.pattern("ECE")
				.pattern(" S ")
				.pattern("ECE")
				.define('E', Items.ENDER_PEARL)
				.define('C', Blocks.CHISELED_STONE_BRICKS)
				.define('S', Blocks.STONE_BRICKS)
				.unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS))
				.save(output, recipeKey("display_pedestal"));
	}

	private void armBanner(String recipeName, net.minecraft.world.level.ItemLike result,
			net.minecraft.world.level.ItemLike material) {
		shaped(RecipeCategory.MISC, result)
				.pattern("XXB")
				.pattern(" XS")
				.define('X', material)
				.define('S', Items.STICK)
				.define('B', Tags.Items.SLIME_BALLS)
				.unlockedBy("has_material", has(material))
				.save(output, recipeKey(recipeName));
	}

	private static ResourceKey<Recipe<?>> recipeKey(String path) {
		return ResourceKey.create(Registries.RECIPE, HutosLib.rloc(path));
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new HLRecipeProvider(registries, output);
		}

		@Override
		public String getName() {
			return "HutosLib Recipes";
		}
	}
}
