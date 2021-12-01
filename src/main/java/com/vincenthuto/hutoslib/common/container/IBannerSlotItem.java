package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public interface IBannerSlotItem {
	@Nonnull
	default ImmutableSet<ResourceLocation> getAcceptableSlots(@Nonnull ItemStack stack) {
		return BannerSlotCapability.ANY_SLOT_LIST;
	}

	default void onWornTick(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}

	default void onEquipped(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}

	default void onUnequipped(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}

	default boolean canEquip(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
		return true;
	}

	default boolean canUnequip(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, stack) <= 0;
	}
}
