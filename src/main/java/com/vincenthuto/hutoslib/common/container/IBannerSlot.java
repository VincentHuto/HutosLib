package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public interface IBannerSlot {
	static boolean isAcceptableSlot(@Nonnull IBannerSlot slot, @Nonnull ItemStack stack,
			@Nonnull IBannerSlotItem extItem) {
		ImmutableSet<ResourceLocation> slots = extItem.getAcceptableSlots(stack);
		return slots.contains(BannerSlotCapability.ANY_SLOT) || slots.contains(slot.getType());
	}

	default boolean canEquip(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			return IBannerSlot.isAcceptableSlot(this, stack, extItem) && extItem.canEquip(stack, this);
		}
		return false;
	}

	default boolean canUnequip(@Nonnull ItemStack stack) {
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

	@Nonnull
	IBannerContainer getContainer();

	@Nonnull
	ItemStack getContents();

	@Nonnull
	ResourceLocation getType();

	void onContentsChanged();

	void setContents(@Nonnull ItemStack stack);
}
