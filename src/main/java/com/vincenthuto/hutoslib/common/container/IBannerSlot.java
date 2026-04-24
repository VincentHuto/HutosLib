package com.vincenthuto.hutoslib.common.container;

import org.jspecify.annotations.NonNull;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public interface IBannerSlot {
	static boolean isAcceptableSlot(@NonNull IBannerSlot slot, @NonNull ItemStack stack,
			@NonNull IBannerSlotItem extItem) {
		ImmutableSet<Identifier> slots = extItem.getAcceptableSlots(stack);
		return slots.contains(BannerSlotCapability.ANY_SLOT) || slots.contains(slot.getType());
	}

	default boolean canEquip(@NonNull ItemStack stack) {
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			return IBannerSlot.isAcceptableSlot(this, stack, extItem) && extItem.canEquip(stack, this);
		}
		return false;
	}

	default boolean canUnequip(@NonNull ItemStack stack) {
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			return extItem.canUnequip(stack, this) && !hasCurseOfBinding(stack);
		}
		return true;
	}

	private static boolean hasCurseOfBinding(ItemStack stack) {
		var enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null) return false;
		return enchantments.keySet().stream().anyMatch(h -> h.is(Enchantments.BINDING_CURSE));
	}

	@NonNull
	IBannerContainer getContainer();

	@NonNull
	ItemStack getContents();

	@NonNull
	Identifier getType();

	void onContentsChanged();

	void setContents(@NonNull ItemStack stack);
}
