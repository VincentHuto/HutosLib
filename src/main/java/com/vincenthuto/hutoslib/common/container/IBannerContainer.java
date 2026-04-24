package com.vincenthuto.hutoslib.common.container;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public interface IBannerContainer {
	@NonNull
	LivingEntity getOwner();

	@NonNull
	ImmutableList<BannerSlotItemHandler> getSlots();

	void onContentsChanged(BannerSlotItemHandler slot);
}
