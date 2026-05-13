package com.vincenthuto.hutoslib.common.banner;

import com.google.common.collect.ImmutableSet;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;

public class BannerSlotCapability {

public static final Identifier ANY_SLOT = Identifier.tryParse("neoforge:any");
public static final ImmutableSet<Identifier> ANY_SLOT_LIST = ImmutableSet.of(ANY_SLOT);

public static AttachmentType<?> getBannerSlotAttachmentType() {
return HLAttachmentTypes.BANNER_SLOT.get();
}
}
