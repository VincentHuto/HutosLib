package com.vincenthuto.hutoslib.common.container;

import org.jspecify.annotations.NonNull;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public interface IBannerSlotItem {
	default boolean canEquip(@NonNull ItemStack stack, @NonNull IBannerSlot slot) {
		return true;
	}

	default boolean canUnequip(@NonNull ItemStack stack, @NonNull IBannerSlot slot) {
		var enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments != null) {
			return enchantments.keySet().stream()
				.noneMatch(h -> h.is(Enchantments.BINDING_CURSE));
		}
		return true;
	}

	@NonNull
	default ImmutableSet<Identifier> getAcceptableSlots(@NonNull ItemStack stack) {
		return BannerSlotCapability.ANY_SLOT_LIST;
	}

	default void onEquipped(@NonNull ItemStack stack, @NonNull IBannerSlot slot) {
	}

	default void onUnequipped(@NonNull ItemStack stack, @NonNull IBannerSlot slot) {
	}

	default void onWornTick(@NonNull ItemStack stack, @NonNull IBannerSlot slot) {
	}
}
