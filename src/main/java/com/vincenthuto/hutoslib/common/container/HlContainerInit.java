package com.vincenthuto.hutoslib.common.container;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.recipe.ArmBannerCraftRecipe;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HlContainerInit {

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			HutosLib.MOD_ID);

	public static final DeferredRegister<RecipeSerializer<?>> RECIPESERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, HutosLib.MOD_ID);

	public static final RegistryObject<MenuType<BannerSlotContainer>> banner_slot_container = CONTAINERS.register("banner_slot_container",
			() -> new MenuType<>(BannerSlotContainer::new));

	public static final RegistryObject<RecipeSerializer<?>> arm_banner_craft = RECIPESERIALIZERS
			.register("arm_banner_craft", () -> new SimpleRecipeSerializer<>(ArmBannerCraftRecipe::new));
}
