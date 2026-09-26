package com.vincenthuto.hutoslib.common.data.book;

import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.BookReaderHooks;
import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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

	/** Optional visual theme for this book's screens. {@code null} = use default textures/colors. */
	@Nullable
	private BookTheme theme;
	private BookSourceIndex sourceIndex = BookSourceIndex.EMPTY;
	private List<GlossaryTermTemplate> glossary = List.of();
	private BookReaderHooks readerHooks = BookReaderHooks.DEFAULT;
	private static final java.util.Set<ResourceLocation> WARNED_MISSING_REVEAL = java.util.concurrent.ConcurrentHashMap.newKeySet();

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

	public BookCodeModel copyWithChapters(List<ChapterTemplate> visibleChapters) {
		BookCodeModel copy = new BookCodeModel(resourceLocation, template);
		copy.chapters = new java.util.ArrayList<>(visibleChapters);
		copy.pageFilter = pageFilter;
		copy.theme = theme;
		copy.sourceIndex = sourceIndex;
		copy.glossary = glossary;
		copy.readerHooks = readerHooks;
		return copy;
	}

	public BookSourceIndex getSourceIndex() { return sourceIndex; }
	public void setSourceIndex(BookSourceIndex sourceIndex) { this.sourceIndex = sourceIndex; }
	public List<GlossaryTermTemplate> getGlossary() { return glossary; }
	public void setGlossary(List<GlossaryTermTemplate> glossary) { this.glossary = List.copyOf(glossary); }
	public BookReaderHooks getReaderHooks() { return readerHooks; }

	public void setRedactionPredicate(BiPredicate<Player, Integer> predicate) {
		readerHooks = new BookReaderHooks(predicate, readerHooks.requirement(), readerHooks.owner(), readerHooks.status());
	}
	public void setRevealRequirementLabel(IntFunction<Component> label) {
		readerHooks = new BookReaderHooks(readerHooks.reveal(), label, readerHooks.owner(), readerHooks.status());
	}
	public void setOwnerLine(Function<Player, Component> owner) {
		readerHooks = new BookReaderHooks(readerHooks.reveal(), readerHooks.requirement(), owner, readerHooks.status());
	}
	public void setStatusLine(Function<Player, Component> status) {
		readerHooks = new BookReaderHooks(readerHooks.reveal(), readerHooks.requirement(), readerHooks.owner(), status);
	}
	public boolean canReveal(Player player, int level) {
		if (level == 0) return true;
		if (level < 0) return false;
		if (readerHooks.reveal() != null) return readerHooks.reveal().test(player, level);
		if (WARNED_MISSING_REVEAL.add(resourceLocation)) {
			org.apache.logging.log4j.LogManager.getLogger(BookCodeModel.class)
					.warn("Book {} has gated content but no reveal predicate; concealing it", resourceLocation);
		}
		return false;
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
	 * {@code resourceLocation.getPath() + "/"}. Used by
	 * {@link com.vincenthuto.hutoslib.client.book.BookReadTracker} and similar
	 * utilities to scope queries to this book's entries.
	 */
	public String getEntryPrefix() {
		return resourceLocation.getPath() + "/";
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
