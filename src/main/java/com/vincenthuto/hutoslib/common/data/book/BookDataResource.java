package com.vincenthuto.hutoslib.common.data.book;

import net.minecraft.resources.ResourceLocation;

public record BookDataResource(ResourceLocation resourceLocation,  BookDataTemplate template) {

	
	public String[] getSplitPath() {
		String input = resourceLocation.getPath();
		String[] split = resourceLocation.getPath().split("/");
		return split;
	}

	
	public String getBook() {
		String input = resourceLocation.getPath();
		String[] split = resourceLocation.getPath().split("/");
		if (split.length == 2) {
			return split[0].replace("/", "");
		}
		return null;
	}

	public String getChapter() {
		String input = resourceLocation.getPath();
		String[] split = resourceLocation.getPath().split("/");
		if (split.length == 3) {
			return split[1].replace("/", "");
		}
		return null;
	}

	public String getPage() {
		String input = resourceLocation.getPath();
		String[] split = resourceLocation.getPath().split("/");
		if (split.length == 4) {
			return split[3].replace("/", "");
		}
		return null;
	}

}
