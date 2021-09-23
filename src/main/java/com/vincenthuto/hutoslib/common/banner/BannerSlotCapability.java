package com.vincenthuto.hutoslib.common.banner;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BannerSlotCapability {

	public static final ResourceLocation ANY_SLOT = new ResourceLocation("forge:any");
	public static final ImmutableSet<ResourceLocation> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);

	@CapabilityInject(IBannerSlotItem.class)
	public static Capability<IBannerSlotItem> INSTANCE = null;

}
