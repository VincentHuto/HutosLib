package com.vincenthuto.hutoslib.common.data;

import java.util.concurrent.CompletableFuture;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class HLRecipeProvider extends RecipeProvider {

    public HLRecipeProvider(PackOutput output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // Items
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HLItemInit.obsidian_flakes.get(), 4)
            .requires(Tags.Items.OBSIDIANS)
            .unlockedBy("has_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.OBSIDIAN).build()))
            .save(output, HutosLib.rloc("flakes_from_obsidian"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HLItemInit.hutoslib_logo.get())
            .requires(HLItemInit.obsidian_flakes.get())
            .requires(Items.PAPER)
            .unlockedBy("has_paper", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.PAPER).build()))
            .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HLItemInit.hl_guide_book.get())
            .requires(HLItemInit.obsidian_flakes.get())
            .requires(Items.BOOK)
            .unlockedBy("has_paper", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.PAPER).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.raw_clay_flask.get())
            .pattern(" C ")
            .pattern("C C")
            .pattern("CCC")
            .define('C', Items.CLAY_BALL)
            .unlockedBy("has_clay_ball", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.OBSIDIAN)
            .pattern("XX")
            .pattern("XX")
            .define('X', HLItemInit.obsidian_flakes.get())
            .unlockedBy("has_obsidian_flakes", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(HLItemInit.obsidian_flakes.get()).build()))
            .save(output, HutosLib.rloc("obsidian_from_flakes"));

        // Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.iron_knapper.get())
            .pattern("  P")
            .pattern(" N ")
            .pattern("N  ")
            .define('N', Tags.Items.RODS_WOODEN)
            .define('P', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_iron_ingots", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.diamond_knapper.get())
            .pattern("  P")
            .pattern(" N ")
            .pattern("N  ")
            .define('N', Tags.Items.RODS_WOODEN)
            .define('P', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_gem_diamonds", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.GEMS_DIAMOND).build()))
            .save(output);

        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(HLItemInit.diamond_knapper.get()),
                Ingredient.of(Items.NETHERITE_INGOT),
                RecipeCategory.TOOLS,
                HLItemInit.netherite_knapper.get())
            .unlocks("has_netherite_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.NETHERITE_INGOT).build()))
            .save(output, HutosLib.rloc("netherite_knapper"));

        // Banners
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.leather_arm_banner.get())
            .pattern("XXB")
            .pattern(" XS")
            .define('X', Tags.Items.LEATHERS)
            .define('B', Tags.Items.SLIME_BALLS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.LEATHERS).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.gold_arm_banner.get())
            .pattern("XXB")
            .pattern(" XS")
            .define('X', Tags.Items.INGOTS_GOLD)
            .define('B', Tags.Items.SLIME_BALLS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_gold_ingots", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.INGOTS_GOLD).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.iron_arm_banner.get())
            .pattern("XXB")
            .pattern(" XS")
            .define('X', Tags.Items.INGOTS_IRON)
            .define('B', Tags.Items.SLIME_BALLS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_iron_ingots", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.diamond_arm_banner.get())
            .pattern("XXB")
            .pattern(" XS")
            .define('X', Tags.Items.GEMS_DIAMOND)
            .define('B', Tags.Items.SLIME_BALLS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_gem_diamonds", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.GEMS_DIAMOND).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLItemInit.obsidian_arm_banner.get())
            .pattern("XXB")
            .pattern(" XS")
            .define('X', Tags.Items.OBSIDIANS)
            .define('B', Tags.Items.SLIME_BALLS)
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.OBSIDIANS).build()))
            .save(output);

        // Blocks
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, HLBlockInit.display_glass.get())
            .requires(Items.GLOW_BERRIES)
            .requires(Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_glass", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.GLASS_BLOCKS).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HLBlockInit.display_pedestal.get())
            .pattern("CSC")
            .pattern("ESE")
            .pattern("CSC")
            .define('C', Blocks.CHISELED_STONE_BRICKS)
            .define('E', Tags.Items.ENDER_PEARLS)
            .define('S', Items.STONE_BRICKS)
            .unlockedBy("has_stone_bricks", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Items.STONE_BRICKS).build()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.CRYING_OBSIDIAN)
            .pattern(" X ")
            .pattern("XLX")
            .pattern(" X ")
            .define('X', Tags.Items.OBSIDIANS)
            .define('L', Tags.Items.GEMS_LAPIS)
            .unlockedBy("has_gem_lapis", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(Tags.Items.GEMS_LAPIS).build()))
            .save(output, HutosLib.rloc("lapis_cry"));
    }
}
