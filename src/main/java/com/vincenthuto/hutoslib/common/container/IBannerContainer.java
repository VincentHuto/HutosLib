package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.LivingEntity;

public interface IBannerContainer {
	@Nonnull
	LivingEntity getOwner();

	@Nonnull
	ImmutableList<BannerSlotItemHandler> getSlots();

	void onContentsChanged(BannerSlotItemHandler slot);
}
