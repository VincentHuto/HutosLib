package com.vincenthuto.hutoslib.common.banner;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class BannerSlotCapability {

	public static final ResourceLocation ANY_SLOT = new ResourceLocation("forge:any");
	public static final ImmutableSet<ResourceLocation> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);

    public static final Capability<IBannerSlotItem> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

}
