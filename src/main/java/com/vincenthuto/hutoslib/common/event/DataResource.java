package com.vincenthuto.hutoslib.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public record DataResource<T>(ResourceLocation resourceLocation, Resource resource, T data) {}
