package com.vincenthuto.hutoslib.common.data.shadow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface IPlatformRecipeHelper {
    <T extends CraftingRecipe> int getWidth(T recipe);
    <T extends CraftingRecipe> int getHeight(T recipe);


    Optional<ResourceLocation> getRegistryNameForRecipe(Recipe<?> recipe);

}