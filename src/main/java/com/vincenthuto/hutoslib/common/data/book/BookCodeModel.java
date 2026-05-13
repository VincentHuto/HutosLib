package com.vincenthuto.hutoslib.common.data.book;

import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.List;

public class BookCodeModel {

	/**
	 * A no-op {@link IBookPageFilter} that returns the source book unchanged.
	 * Use this as the default when no content gating is required.
	 */
	public static final IBookPageFilter UNFILTERED = (source, player) -> source;

	Identifier Identifier;
	BookTemplate template;
	List<ChapterTemplate> chapters;

	/** Optional filter applied when this book is opened. {@code null} = no filtering. */
	@Nullable
	private IBookPageFilter pageFilter;

	/** Optional visual theme for this book's screens. {@code null} = use default textures/colors. */
	@Nullable
	private BookTheme theme;

	public BookCodeModel(Identifier Identifier, BookTemplate template) {
		this.Identifier = Identifier;
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


	public Identifier getResourceLocation() {
		return Identifier;
	}

	public void setResourceLocation(Identifier Identifier) {
		this.Identifier = Identifier;
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

	/**
	 * Returns the visual theme for this book, or {@code null} if using defaults.
	 */
	@Nullable
	public BookTheme getTheme() {
		return theme;
	}

	/**
	 * Assigns a visual theme to this book. Pass {@code null} to revert to the
	 * default look.
	 */
	public void setTheme(@Nullable BookTheme theme) {
		this.theme = theme;
	}

	/**
	 * Returns the canonical entry-prefix string for this book, i.e.
	 * {@code Identifier.getPath() + "/"}. Used by
	 * {@link com.vincenthuto.hutoslib.client.book.BookReadTracker} and similar
	 * utilities to scope queries to this book's entries.
	 */
	public String getEntryPrefix() {
		return Identifier.getPath() + "/";
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
		return "Book Title: " + Identifier.getPath() + ", Book Name: " + template.getTitle() + " it has "
				+ chapters.size() + " Chapters, and " + getTotalPages() + " pages.";
	}
	public void encodeToBuf(FriendlyByteBuf buf) {
		// Write Book location
		buf.writeIdentifier(Identifier);

		// Write book json
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.overlayLoc);
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);

	}

}
