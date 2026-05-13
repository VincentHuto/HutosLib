package com.vincenthuto.hutoslib.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HLRecipeJsonProvider implements DataProvider {
	private static final List<String> ARM_BANNERS = List.of(
			"leather_arm_banner",
			"iron_arm_banner",
			"gold_arm_banner",
			"diamond_arm_banner",
			"obsidian_arm_banner",
			"netherite_arm_banner");

	private final PackOutput.PathProvider recipes;

	public HLRecipeJsonProvider(PackOutput output) {
		this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		List<CompletableFuture<?>> futures = new ArrayList<>();
		futures.add(DataProvider.saveStable(output, glimmerRecipe(), recipePath("glimmer")));
		futures.add(DataProvider.saveStable(output, armBannerCraftRecipe(), recipePath("arm_banner_craft")));
		for (String armBanner : ARM_BANNERS) {
			futures.add(DataProvider.saveStable(output, armBannerDecorationRecipe(armBanner),
					recipePath(armBanner + "_decoration")));
		}
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return "HutosLib component recipes";
	}

	private Path recipePath(String path) {
		return recipes.json(HutosLib.rloc(path));
	}

	private static JsonObject glimmerRecipe() {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "minecraft:crafting_shapeless");

		JsonArray ingredients = new JsonArray();
		ingredients.add("minecraft:book");
		ingredients.add("minecraft:glowstone_dust");
		ingredients.add("minecraft:glow_ink_sac");
		recipe.add("ingredients", ingredients);

		JsonObject storedEnchantments = new JsonObject();
		storedEnchantments.addProperty("hutoslib:glimmer", 1);

		JsonObject components = new JsonObject();
		components.add("minecraft:stored_enchantments", storedEnchantments);

		JsonObject result = new JsonObject();
		result.addProperty("id", "minecraft:enchanted_book");
		result.add("components", components);
		recipe.add("result", result);

		return recipe;
	}

	private static JsonObject armBannerCraftRecipe() {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "hutoslib:arm_banner_craft");
		return recipe;
	}

	private static JsonObject armBannerDecorationRecipe(String armBanner) {
		String itemId = HutosLib.MOD_ID + ":" + armBanner;
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "minecraft:crafting_special_shielddecoration");
		recipe.addProperty("banner", "#minecraft:banners");
		recipe.addProperty("target", itemId);

		JsonObject result = new JsonObject();
		result.addProperty("id", itemId);
		recipe.add("result", result);

		return recipe;
	}
}
