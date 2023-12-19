package com.vincenthuto.hutoslib.common.recipe;

import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ArmBannerCraftRecipe extends CustomRecipe {
	public ArmBannerCraftRecipe(ResourceLocation idIn, CraftingBookCategory pCategory) {
		super(idIn, CraftingBookCategory.MISC);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess p_267165_) {
		ItemStack itemstack = ItemStack.EMPTY;
		ItemStack itemstack1 = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
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
			CompoundTag CompoundTag = itemstack.getTagElement("BlockEntityTag");
			CompoundTag CompoundTag1 = CompoundTag == null ? new CompoundTag() : CompoundTag.copy();
			CompoundTag1.putInt("Base", ((BannerItem) itemstack.getItem()).getColor().getId());
			itemstack1.addTagElement("BlockEntityTag", CompoundTag1);
			return itemstack1;
		}
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return HlContainerInit.arm_banner_craft.get();
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		ItemStack itemstack1 = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack itemstack2 = inv.getItem(i);
			if (!itemstack2.isEmpty()) {
				if (itemstack2.getItem() instanceof BannerItem) {
					if (!itemstack1.isEmpty()) {
						return false;
					}

					itemstack1 = itemstack2;
				} else {
					if (!(itemstack2.getItem() instanceof ItemArmBanner) || !itemstack.isEmpty() || (itemstack2.getTagElement("BlockEntityTag") != null)) {
						return false;
					}

					itemstack = itemstack2;
				}
			}
		}

		return !itemstack.isEmpty() && !itemstack1.isEmpty();
	}


}