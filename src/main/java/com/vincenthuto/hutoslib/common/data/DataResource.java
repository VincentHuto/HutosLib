package com.vincenthuto.hutoslib.common.data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public record DataResource<T>(ResourceLocation resourceLocation, Resource resource, List<DataTemplate> data) {

	public String getTitle() {
		String input = resourceLocation.getPath();
		String regex = "(?<=\\/)(.*?)(?=\\/)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String match = matcher.group();
			return match;
		}
		return null;
	}

	public String getChapter() {
		String input = resourceLocation.getPath();
		String regex = "(?<=\\/\\w+\\/)(.*?)(?=\\/\\w+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String match = matcher.group(); 
			return match;
		} 
		return null;
	}

	public String getPage() {
		String input = resourceLocation.getPath();
		String regex = "(?<=pages\\/)(.*?)(?=\\.[^.]+$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {
			String match = matcher.group();
			return match;
		}
		return null;
	}

}
