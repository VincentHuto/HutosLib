package com.vincenthuto.hutoslib.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface IModRecipe {
	ResourceLocation getId();

	List<Ingredient> getInputs();

	ItemStack getOutput();
}