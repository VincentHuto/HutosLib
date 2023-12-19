package com.vincenthuto.hutoslib.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;

public class HLBannerPatternItem extends Item implements ItemWithBannerPattern {
	private final TagKey<BannerPattern> pattern;

	public HLBannerPatternItem(TagKey<BannerPattern> pattern, Properties settings) {
		super(settings);
		this.pattern = pattern;
	}

	@Override
	public TagKey<BannerPattern> getBannerPattern() {
		return pattern;
	}
}