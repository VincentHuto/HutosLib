package com.vincenthuto.hutoslib.common.banner;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;

public class BannerSlotCapability {

	public static final ResourceLocation ANY_SLOT = new ResourceLocation("forge:any");
	public static final ImmutableSet<ResourceLocation> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);

    public static final Capability<IBannerSlotItem> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(BannerSlotCapability.class);
    }
}
