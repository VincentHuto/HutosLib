package com.vincenthuto.hutoslib.common.data.book;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.DataResource;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class BookManager {
	private static final Gson GSON = new Gson();
	List<BookCodeModel> books = new ArrayList<BookCodeModel>();

	protected String resourceName() {
		return "books";
	}

	@SuppressWarnings("rawtypes")
	public List<DataResource<DataTemplate>> load(ResourceManager resourceManager) {
		System.out.println("Load:");
		Map<ResourceLocation, Resource> listResources = resourceManager.listResources(resourceName(),
				resourceLocation -> resourceLocation.getPath().endsWith(".json"));
		List<DataResource<DataTemplate>> dataResourceList = new ArrayList<>();
		listResources.forEach((resourceLocation, resource) -> {
			HutosLib.LOGGER.info("Resource Location {}", resourceLocation);
			try {
				List<DataTemplate> resourceTemplates = this.deserialize(resourceLocation, resource);
				dataResourceList.add(new DataResource<>(resourceLocation, resource, resourceTemplates));

			} catch (Exception e) {
				HutosLib.LOGGER.error("Failed to load resources template {}", resourceLocation, e);
			}
		});
		// Sort resources
		HutosLib.LOGGER.info("Sorting resources");
		List<DataResource> titles = new ArrayList<DataResource>();
		List<DataResource> chapters = new ArrayList<DataResource>();
		List<DataResource> pages = new ArrayList<DataResource>();
		for (DataResource<?> dr : dataResourceList) {
			if (dr.getPage() != null) {
				pages.add(dr);
				System.out.println("this is a Page: " + dr);
			} else if (dr.getChapter() != null) {
				chapters.add(dr);
				System.out.println("this is a Chapter: " + dr);
			} else if (dr.getTitle() != null) {
				titles.add(dr);
				System.out.println("this is a Title: " + dr);
			}
		}
		// Bind books
		HutosLib.LOGGER.info("Binding Books");
		List<BookCodeModel> books = new ArrayList<BookCodeModel>();
		for (int i = 0; i < titles.size(); i++) {
			if (titles.get(i).data().get(0) instanceof BookTemplate b) {
				List<BookChapterTemplate> chapters1 = new ArrayList<BookChapterTemplate>();
				List<BookPageTemplate> pages1 = new ArrayList<BookPageTemplate>();
				String title = titles.get(i).getTitle();
				
				
				for (int j = 0; j < chapters.size(); j++) {
					System.out.println("chapters TITLE: " + chapters.get(j).getTitle());
					System.out.println("book TITLE: " + title);
					if (chapters.get(j).getTitle().equals(title)) {
						chapters1.add((BookChapterTemplate) chapters.get(j).data().get(0));
					}
				}
				
				for (int k = 0; k < pages.size(); k++) {
					System.out.println("page TITLE: " + pages.get(k).getTitle());
					System.out.println("book TITLE: " + title);
					if (pages.get(k).getTitle().equals(title)) {
						pages1.add((BookPageTemplate) pages.get(k).data().get(0));
						System.out.println("ttdwqdw");

					}
				}
				BookCodeModel book = new BookCodeModel(b, chapters1, pages1);
				books.add(book);
			}
		}
		HutosLib.LOGGER.info("BOOOKS");
		System.out.println(books);

		HutosLib.LOGGER.info("DataResourceList");
		System.out.println(dataResourceList);
		return dataResourceList;
	}

	protected List<DataTemplate> deserialize(ResourceLocation resourceLocation, Resource resource) throws IOException {
		System.out.println("Deserialize:");
		List<DataTemplate> data = new ArrayList<DataTemplate>();
		BufferedReader bufferedReader = resource.openAsReader();
		String[] test = resourceLocation.getPath().split("/");
		if (test.length == 3) {
			BookTemplate page = GSON.fromJson(bufferedReader, BookTemplate.class);
			data.add(page);
		} else if (test.length == 4) {
			BookChapterTemplate page = GSON.fromJson(bufferedReader, BookChapterTemplate.class);
			data.add(page);
		} else if (test.length == 5) {
			BookPageTemplate page = GSON.fromJson(bufferedReader, BookPageTemplate.class);
			String input = resourceLocation.getPath();
			String regex = "(?<=\\/\\w+\\/)(.*?)(?=\\/\\w+)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			String chapterName = "";
			if (matcher.find()) {
				String match = matcher.group(1);
				chapterName = match;
			}
			page.setChapter(chapterName);
			data.add(page);
		}
		Collections.reverse(data);
		return data;
	}

	public void reload(ResourceManager resourceManager) {
		System.out.println("Reload:");
		List<DataResource<DataTemplate>> dataResources = this.load(resourceManager);
		System.out.println("ResourceManager: " + dataResources);
	}
}