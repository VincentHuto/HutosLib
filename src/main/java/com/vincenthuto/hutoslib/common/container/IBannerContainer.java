package com.vincenthuto.hutoslib.common.container;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public interface IBannerContainer {
	@Nonnull
	LivingEntity getOwner();

	@Nonnull
	ImmutableList<BannerSlotItemHandler> getSlots();

	void onContentsChanged(BannerSlotItemHandler slot);
}
