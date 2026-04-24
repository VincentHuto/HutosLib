package com.vincenthuto.hutoslib.common.recipe;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IModRecipe {
	Identifier getId();

	List<Ingredient> getInputs();

	ItemStack getOutput();
}