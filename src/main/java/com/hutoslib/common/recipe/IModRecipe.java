package com.hutoslib.common.recipe;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IModRecipe {
	List<Ingredient> getInputs();

	ItemStack getOutput();

	ResourceLocation getId();
}