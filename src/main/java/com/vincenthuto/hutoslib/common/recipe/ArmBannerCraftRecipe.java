package com.vincenthuto.hutoslib.common.recipe;

import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ArmBannerCraftRecipe extends CustomRecipe {
	public static final MapCodec<ArmBannerCraftRecipe> MAP_CODEC = MapCodec.unit(ArmBannerCraftRecipe::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ArmBannerCraftRecipe> STREAM_CODEC =
			StreamCodec.unit(new ArmBannerCraftRecipe());
	public static final RecipeSerializer<ArmBannerCraftRecipe> SERIALIZER =
			new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public ArmBannerCraftRecipe() {
		super();
	}

	public static ItemStack assembleArmBanner(CraftingInput inv) {
		ItemStack sourceBanner = ItemStack.EMPTY;
		ItemStack outputArmBanner = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BannerItem) {
					if (!sourceBanner.isEmpty()) {
						return ItemStack.EMPTY;
					}
					sourceBanner = stack;
				} else if (stack.getItem() instanceof ItemArmBanner) {
					if (!outputArmBanner.isEmpty()) {
						return ItemStack.EMPTY;
					}
					outputArmBanner = stack;
				} else {
					return ItemStack.EMPTY;
				}
			}
		}

		if (outputArmBanner.isEmpty() || sourceBanner.isEmpty()) {
			return ItemStack.EMPTY;
		}

		BannerPatternLayers currentPatterns = outputArmBanner.getOrDefault(
				DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
		if (!currentPatterns.layers().isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result = outputArmBanner.copyWithCount(1);
		return copyBannerComponents(sourceBanner, result) ? result : ItemStack.EMPTY;
	}

	public static boolean copyBannerComponentsFromCraftingContainer(Container craftMatrix, ItemStack result) {
		if (!(result.getItem() instanceof ItemArmBanner)) {
			return false;
		}

		ItemStack sourceBanner = ItemStack.EMPTY;
		for (int i = 0; i < craftMatrix.getContainerSize(); ++i) {
			ItemStack stack = craftMatrix.getItem(i);
			if (stack.getItem() instanceof BannerItem) {
				if (!sourceBanner.isEmpty()) {
					return false;
				}
				sourceBanner = stack;
			}
		}

		return !sourceBanner.isEmpty() && copyBannerComponents(sourceBanner, result);
	}

	private static boolean copyBannerComponents(ItemStack sourceBanner, ItemStack result) {
		if (!(sourceBanner.getItem() instanceof BannerItem bannerItem)
				|| !(result.getItem() instanceof ItemArmBanner)) {
			return false;
		}

		BannerPatternLayers patterns = sourceBanner.get(DataComponents.BANNER_PATTERNS);
		if (patterns != null) {
			result.set(DataComponents.BANNER_PATTERNS, patterns);
		} else {
			result.remove(DataComponents.BANNER_PATTERNS);
		}
		DyeColor baseColor = bannerItem.getColor();
		result.set(DataComponents.BASE_COLOR, baseColor);
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		return assembleArmBanner(inv);
	}

	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<ArmBannerCraftRecipe> getSerializer() {
		return HlContainerInit.arm_banner_craft.get();
	}

	@Override
	public boolean matches(CraftingInput inv, Level worldIn) {
		return !assembleArmBanner(inv).isEmpty();
	}
}
