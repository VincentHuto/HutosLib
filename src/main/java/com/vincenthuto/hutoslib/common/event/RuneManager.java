package com.vincenthuto.hutoslib.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class RuneManager extends DataManager<BookPageTemplate> {
	private static final Gson GSON = new Gson();

	@Override
	protected String resourceName() {
		return "guide";
	}

	@Override
	protected BookPageTemplate deserialize(ResourceLocation resourceLocation, Resource resource) throws IOException {
		BufferedReader bufferedReader = resource.openAsReader();
		return GSON.fromJson(bufferedReader, BookPageTemplate.class);
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		List<DataResource<BookPageTemplate>> dataResources = this.load(resourceManager);
		for (DataResource<BookPageTemplate> dataResource : dataResources) {
			BookPageTemplate newRuneTemplateData = dataResource.data();

			System.out.println(newRuneTemplateData);
			System.out.println(newRuneTemplateData.getIconItem());

		}
	}
}