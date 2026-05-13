package com.vincenthuto.hutoslib.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class HLRecipeJsonProvider implements DataProvider {
	private final PackOutput.PathProvider recipes;

	public HLRecipeJsonProvider(PackOutput output) {
		this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return DataProvider.saveStable(output, glimmerRecipe(), recipePath("glimmer"));
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
}
