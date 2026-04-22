package com.vincenthuto.hutoslib.common.recipe;

import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ArmBannerCraftRecipe extends CustomRecipe {
public ArmBannerCraftRecipe(CraftingBookCategory pCategory) {
super(CraftingBookCategory.MISC);
}

@Override
public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
ItemStack itemstack = ItemStack.EMPTY;
ItemStack itemstack1 = ItemStack.EMPTY;

for (int i = 0; i < inv.size(); ++i) {
ItemStack itemstack2 = inv.getItem(i);
if (!itemstack2.isEmpty()) {
if (itemstack2.getItem() instanceof BannerItem) {
itemstack = itemstack2;
} else if (itemstack2.getItem() instanceof ItemArmBanner) {
itemstack1 = itemstack2.copy();
}
}
}

if (itemstack1.isEmpty()) {
return itemstack1;
} else {
DyeColor baseColor = ((BannerItem) itemstack.getItem()).getColor();
itemstack1.set(DataComponents.BASE_COLOR, baseColor);
BannerPatternLayers patterns = itemstack.get(DataComponents.BANNER_PATTERNS);
if (patterns != null) {
itemstack1.set(DataComponents.BANNER_PATTERNS, patterns);
}
return itemstack1;
}
}

@Override
public boolean canCraftInDimensions(int width, int height) {
return width * height >= 2;
}

@Override
public RecipeSerializer<?> getSerializer() {
return HlContainerInit.arm_banner_craft.get();
}

@Override
	public boolean matches(CraftingInput inv, Level worldIn) {
ItemStack itemstack = ItemStack.EMPTY;
ItemStack itemstack1 = ItemStack.EMPTY;

for (int i = 0; i < inv.size(); ++i) {
ItemStack itemstack2 = inv.getItem(i);
if (!itemstack2.isEmpty()) {
if (itemstack2.getItem() instanceof BannerItem) {
if (!itemstack1.isEmpty()) {
return false;
}
itemstack1 = itemstack2;
} else {
if (!(itemstack2.getItem() instanceof ItemArmBanner)
|| !itemstack.isEmpty()
|| itemstack2.get(DataComponents.BANNER_PATTERNS) != null) {
return false;
}
itemstack = itemstack2;
}
}
}

return !itemstack.isEmpty() && !itemstack1.isEmpty();
}
}
