package com.vincenthuto.hutoslib.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public interface ItemWithBannerPattern {
	TagKey<BannerPattern> getBannerPattern();
}