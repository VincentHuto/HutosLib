package com.vincenthuto.hutoslib.common.data.book;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	public static List<BookCodeModel> books = new ArrayList<BookCodeModel>();
	public static List<BookCodeModel> clientBooks = new ArrayList<BookCodeModel>();

	public static List<DataResource<DataTemplate>> resources = new ArrayList<DataResource<DataTemplate>>();

	protected String resourceName() {
		return "books";
	}

	public static BookCodeModel getBookByTitle(ResourceLocation rl) {
		Optional<BookCodeModel> optional = books.parallelStream().filter(p -> p.resourceLocation.equals(rl))
				.findFirst();
		return optional.isPresent() ? optional.get() : null;
	}

	protected List<DataTemplate> deserialize(ResourceLocation resourceLocation, Resource resource) throws IOException {
		// System.out.println("Deserialize:");
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
		return data;
	}

	public static void setBooks(List<BookCodeModel> books) {
		BookManager.books = books;
	}

	public List<DataResource<DataTemplate>> load(ResourceManager resourceManager) {
		HutosLib.LOGGER.info("Loading book data:");
		return bindBooks(resourceManager);
	}

	public void reload(ResourceManager resourceManager) {
		HutosLib.LOGGER.info("Reloading book data:");
		books.clear();
		bindBooks(resourceManager);
	}

	public DataResource findResourceByResourceLocation(ResourceLocation loc) {
		for (DataResource<DataTemplate> dataResource : resources) {
			if (dataResource.resourceLocation() == loc) {
				return dataResource;
			}
		}
		return null;
	}

	public List<DataResource<DataTemplate>> bindBooks(ResourceManager resourceManager) {
		HutosLib.LOGGER.info("Binding Books:");
		Map<ResourceLocation, Resource> listResources = resourceManager.listResources(resourceName(),
				resourceLocation -> resourceLocation.getPath().endsWith(".json"));
		List<DataResource<DataTemplate>> dataResourceList = new ArrayList<>();
		listResources.forEach((resourceLocation, resource) -> {
			// HutosLib.LOGGER.info("Resource Location {}", resourceLocation);
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
				// System.out.println("this is a Page: " + dr);
			} else if (dr.getChapter() != null) {
				chapters.add(dr);
				// System.out.println("this is a Chapter: " + dr);
			} else if (dr.getTitle() != null) {
				titles.add(dr);
				// System.out.println("this is a Title: " + dr);
			}
		}
		// Bind books
		for (int i = 0; i < titles.size(); i++) {
			if (titles.get(i).data().get(0) instanceof BookTemplate b) {
				List<BookChapterTemplate> chapters1 = new ArrayList<BookChapterTemplate>();
				List<BookPageTemplate> pages1 = new ArrayList<BookPageTemplate>();
				String title = titles.get(i).getTitle();
				for (int j = 0; j < chapters.size(); j++) {
					if (chapters.get(j).getTitle().equals(title)) {
						BookChapterTemplate chap = ((BookChapterTemplate) chapters.get(j).data().get(0));
						for (int k = 0; k < pages.size(); k++) {
							boolean doesBelongToBook = pages.get(k).getTitle().equals(title)
									&& pages.get(k).getChapter().equals(chapters.get(j).getChapter());
							if (doesBelongToBook) {
								pages1.add((BookPageTemplate) pages.get(k).data().get(0));
							}
						}
						chap.setPages(pages1);
						chapters1.add(chap);
					}
				}

				BookCodeModel book = new BookCodeModel(
						new ResourceLocation(titles.get(i).resourceLocation().getNamespace(), title), b, chapters1);
				System.out.println(book);

				books.add(book);
			}
		}
		HutosLib.LOGGER.info(books.size() + " Books bound!");
		resources = dataResourceList;
		return dataResourceList;
	}

}