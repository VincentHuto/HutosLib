package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public interface IBannerSlot {
	@Nonnull
	IBannerContainer getContainer();

	@Nonnull
	ResourceLocation getType();

	@Nonnull
	ItemStack getContents();

	void setContents(@Nonnull ItemStack stack);

	void onContentsChanged();

	default boolean canEquip(@Nonnull ItemStack stack) {
		return stack.getCapability(BannerSlotCapability.INSTANCE, null)
				.map((extItem) -> IBannerSlot.isAcceptableSlot(this, stack, extItem) && extItem.canEquip(stack, this))
				.orElse(false);
	}

	default boolean canUnequip(@Nonnull ItemStack stack) {
		return stack.getCapability(BannerSlotCapability.INSTANCE, null)
				.map((extItem) -> extItem.canUnequip(stack, this)
						&& EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, stack) <= 0)
				.orElse(true);
	}

	static boolean isAcceptableSlot(@Nonnull IBannerSlot slot, @Nonnull ItemStack stack,
			@Nonnull IBannerSlotItem extItem) {
		ImmutableSet<ResourceLocation> slots = extItem.getAcceptableSlots(stack);
		return slots.contains(BannerSlotCapability.ANY_SLOT) || slots.contains(slot.getType());
	}
}
