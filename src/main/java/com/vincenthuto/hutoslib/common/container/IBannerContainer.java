package com.vincenthuto.hutoslib.common.container;

import org.jspecify.annotations.NonNull;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.LivingEntity;

public interface IBannerContainer {
	@NonNull
	LivingEntity getOwner();

	@NonNull
	ImmutableList<BannerSlotItemHandler> getSlots();

	void onContentsChanged(BannerSlotItemHandler slot);
}
