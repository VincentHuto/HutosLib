package com.vincenthuto.hutoslib.common.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class DataManager<T> {
    protected abstract String resourceName();

    public List<DataResource<T>> load(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> listResources = resourceManager.listResources(resourceName(), resourceLocation -> resourceLocation.getPath().endsWith(".json"));
        List<DataResource<T>> dataResourceList = new ArrayList<>();

        listResources.forEach((resourceLocation, resource) -> {
            HutosLib.LOGGER.info("Resource Location {}", resourceLocation);
            try {
                T resourceTemplate = this.deserialize(resourceLocation, resource);
                dataResourceList.add(new DataResource<>(resourceLocation, resource, resourceTemplate));
            } catch (Exception e) {
            	HutosLib.LOGGER.error("Failed to load resources template {}", resourceLocation, e);
            }
        });

        return dataResourceList;
    }

    protected abstract T deserialize(ResourceLocation resourceLocation, Resource resource) throws IOException;

    protected abstract void reload(ResourceManager resourceManager);
}
