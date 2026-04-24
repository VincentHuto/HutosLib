package com.vincenthuto.hutoslib.common.container;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.recipe.ArmBannerCraftRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HlContainerInit {

public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU,
HutosLib.MOD_ID);

public static final DeferredRegister<RecipeSerializer<?>> RECIPESERIALIZERS = DeferredRegister
.create(Registries.RECIPE_SERIALIZER, HutosLib.MOD_ID);

public static final DeferredHolder<MenuType<?>, MenuType<BannerSlotContainer>> banner_slot_container = CONTAINERS.register(
"banner_slot_container", () -> new MenuType<>(BannerSlotContainer::new, FeatureFlags.DEFAULT_FLAGS));

public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> arm_banner_craft = RECIPESERIALIZERS
.register("arm_banner_craft", () -> new CustomRecipe.Serializer<>(ArmBannerCraftRecipe::new));
}
