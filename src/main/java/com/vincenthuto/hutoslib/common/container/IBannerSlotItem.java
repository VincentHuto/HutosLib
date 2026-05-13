package com.vincenthuto.hutoslib.common.container;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import javax.annotation.Nonnull;

public interface IBannerSlotItem {
	default boolean canEquip(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
		return true;
	}

	default boolean canUnequip(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
		var enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments != null) {
			return enchantments.keySet().stream()
				.noneMatch(h -> h.is(Enchantments.BINDING_CURSE));
		}
		return true;
	}

	@Nonnull
	default ImmutableSet<Identifier> getAcceptableSlots(@Nonnull ItemStack stack) {
		return BannerSlotCapability.ANY_SLOT_LIST;
	}

	default void onEquipped(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}

	default void onUnequipped(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}

	default void onWornTick(@Nonnull ItemStack stack, @Nonnull IBannerSlot slot) {
	}
}
