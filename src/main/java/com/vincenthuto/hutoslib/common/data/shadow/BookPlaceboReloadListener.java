package com.vincenthuto.hutoslib.common.data.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.DataResource;
import com.vincenthuto.hutoslib.common.data.DataTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.resources.ResourceLocation;

public class BookPlaceboReloadListener extends PlaceboJsonReloadListener<DataTemplate> {

	public static final BookPlaceboReloadListener INSTANCE = new BookPlaceboReloadListener();
	private Map<ResourceLocation, DataTemplate> byType = ImmutableMap.of();
	public List<BookCodeModel> books = ImmutableList.of();
	private static final Gson GSON = new Gson();

	public BookPlaceboReloadListener() {
		super(HutosLib.LOGGER, "books", true, true);
	}
	
	public BookCodeModel getBookByTitle(ResourceLocation rl) {
		Optional<BookCodeModel> optional = this.books.parallelStream().filter(p -> p.getResourceLocation().equals(rl))
				.findFirst();
		return optional.isPresent() ? optional.get() : null;
	}

	@Override
	protected void onReload() {
		super.onReload();
		ImmutableMap.Builder<ResourceLocation, DataTemplate> builder = ImmutableMap.builder();
		this.registry.values().forEach(a -> {
			builder.put(a.id, a);
		});
		this.byType = builder.build();
		this.bindBooks(this.byType);
	}

	@Override
	protected void registerBuiltinSerializers() {
		this.registerSerializer(HutosLib.rloc("book"), BookTemplate.SERIALIZER);
		this.registerSerializer(HutosLib.rloc("chapter"), ChapterTemplate.SERIALIZER);
		this.registerSerializer(HutosLib.rloc("page"), PageTemplate.SERIALIZER);
	}

	public Map<ResourceLocation, DataTemplate> getTypeMap() {
		return this.byType;
	}

	public List<BookCodeModel> getBooks() {
		return this.books;
	}

	protected String resourceName() {
		return "books";
	}

	public void bindBooks(Map<ResourceLocation, DataTemplate> resourceManager) {
		HutosLib.LOGGER.info("Binding Books:");
		ImmutableList.Builder<BookCodeModel> builder = ImmutableList.builder();

		// Sort resources
		HutosLib.LOGGER.info("Sorting resources");
		List<DataResource> resources = new ArrayList<DataResource>();
		resourceManager.forEach((rLoc, template) -> {
			resources.add(new DataResource(rLoc, template));
		});

		List<DataResource> bookNames = new ArrayList<DataResource>();
		List<DataResource> chapterNames = new ArrayList<DataResource>();
		List<DataResource> pageNames = new ArrayList<DataResource>();

		for (DataResource resource : resources) {
			if (resource.getBook() != null) {
				bookNames.add(resource);
			} else if (resource.getChapter() != null) {
				chapterNames.add(resource);
			} else if (resource.getPage() != null) {
				pageNames.add(resource);
			}
		}

		for (int i = 0; i < bookNames.size(); i++) {
			if (bookNames.get(i).template() instanceof BookTemplate b) {
				String bookTitle = bookNames.get(i).getBook();
				BookCodeModel book = new BookCodeModel(new ResourceLocation(
						bookNames.get(i).resourceLocation().getNamespace(), bookNames.get(i).getBook()), b);
				List<ChapterTemplate> chapters = new ArrayList<ChapterTemplate>();
				for (int j = 0; j < chapterNames.size(); j++) {
					String chapterTitle = chapterNames.get(j).getSplitPath()[1];
					String chapterBook = chapterNames.get(j).getSplitPath()[0];
					if (chapterNames.get(j).template() instanceof ChapterTemplate c) {
						if (chapterBook.equals(bookTitle)) {
							List<DataTemplate> pages = new ArrayList<DataTemplate>();
							for (int k = 0; k < pageNames.size(); k++) {
								String pageBook = pageNames.get(k).getSplitPath()[0];
								String pageChapter = pageNames.get(k).getSplitPath()[1];
								if (pageBook.equals(bookTitle) && pageChapter.equals(chapterTitle)) {
									pages.add(pageNames.get(k).template());
								}
							}
							Collections.sort(pages,
									(obj1, obj2) -> Integer.compare(obj1.getOrdinality(), obj2.getOrdinality()));
							c.setPages(pages);
							chapters.add(c);

						}
					}
				}
				book.setChapters(chapters);
				builder.add(book);

			}
		}
		this.books = builder.build();

		HutosLib.LOGGER.info(books.size() + " Books bound!");

	}

}
