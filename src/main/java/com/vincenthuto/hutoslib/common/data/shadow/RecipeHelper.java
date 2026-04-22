package com.vincenthuto.hutoslib.common.data.shadow;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class RecipeHelper implements IPlatformRecipeHelper {
@Override
public <T extends CraftingRecipe> int getWidth(T recipe) {
if (recipe instanceof ShapedRecipe shapedRecipe) {
return shapedRecipe.getRecipeWidth();
}
return 0;
}

@Override
public <T extends CraftingRecipe> int getHeight(T recipe) {
if (recipe instanceof ShapedRecipe shapedRecipe) {
return shapedRecipe.getRecipeHeight();
}
return 0;
}

@SuppressWarnings("DataFlowIssue")
@Override
public Optional<ResourceLocation> getRegistryNameForRecipe(Recipe<?> recipe) {
ResourceLocation id = recipe.getId();
return Optional.ofNullable(id);
}
}
