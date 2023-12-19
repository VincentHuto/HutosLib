package com.vincenthuto.hutoslib.common.data.shadow;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

public interface IPlatformRecipeHelper {
    <T extends CraftingRecipe> int getWidth(T recipe);
    <T extends CraftingRecipe> int getHeight(T recipe);


    Optional<ResourceLocation> getRegistryNameForRecipe(Recipe<?> recipe);

}