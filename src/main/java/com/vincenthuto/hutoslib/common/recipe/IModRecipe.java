package com.vincenthuto.hutoslib.common.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface IModRecipe {
	Identifier getId();

	List<Ingredient> getInputs();

	ItemStack getOutput();
}