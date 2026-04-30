package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BookCodeModel {

	/**
	 * A no-op {@link IBookPageFilter} that returns the source book unchanged.
	 * Use this as the default when no content gating is required.
	 */
	public static final IBookPageFilter UNFILTERED = (source, player) -> source;

	ResourceLocation resourceLocation;
	BookTemplate template;
	List<ChapterTemplate> chapters;

	/** Optional filter applied when this book is opened. {@code null} = no filtering. */
	@Nullable
	private IBookPageFilter pageFilter;

	public BookCodeModel(ResourceLocation resourceLocation, BookTemplate template) {
		this.resourceLocation = resourceLocation;
		this.template = template;
	}

	public BookTemplate getTemplate() {
		return template;
	}

	public void setTemplate(BookTemplate template) {
		this.template = template;
	}

	public List<ChapterTemplate> getChapters() {
		return chapters;
	}

	public void setChapters(List<ChapterTemplate> chapters) {
		this.chapters = chapters;
	}


	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	/**
	 * Returns the active page filter, or {@link #UNFILTERED} if none has been set.
	 */
	public IBookPageFilter getPageFilter() {
		return pageFilter != null ? pageFilter : UNFILTERED;
	}

	/**
	 * Assigns a page filter to this book. Pass {@code null} to clear back to
	 * {@link #UNFILTERED}.
	 */
	public void setPageFilter(@Nullable IBookPageFilter pageFilter) {
		this.pageFilter = pageFilter;
	}

	public int getTotalPages() {
		int count = 0;
		if (chapters != null) {
			for (ChapterTemplate chapter : chapters) {
				if (chapter.getPages() != null) {
					for (BookDataTemplate page : chapter.getPages()) {
						count++;
					}
				}
			}
		}

		return count;
	}


	@Override
	public String toString() {
		return "Book Title: " + resourceLocation.getPath() + ", Book Name: " + template.getTitle() + " it has "
				+ chapters.size() + " Chapters, and " + getTotalPages() + " pages.";
	}
	public void encodeToBuf(FriendlyByteBuf buf) {
		// Write Book location
		buf.writeResourceLocation(resourceLocation);

		// Write book json
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.overlayLoc);
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);

	}

}
