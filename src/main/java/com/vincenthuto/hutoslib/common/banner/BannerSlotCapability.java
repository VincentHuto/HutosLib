package com.vincenthuto.hutoslib.common.banner;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;

public class BannerSlotCapability {

public static final ResourceLocation ANY_SLOT = ResourceLocation.tryParse("neoforge:any");
public static final ImmutableSet<ResourceLocation> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);

public static AttachmentType<?> getBannerSlotAttachmentType() {
return HLAttachmentTypes.BANNER_SLOT.get();
}
}
