package com.vincenthuto.hutoslib.common.data.book;

import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.guide.IBookPageRenderer;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.shadow.TypeKeyed.TypeKeyedBase;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class BookDataTemplate extends TypeKeyedBase<BookDataTemplate> {
	ResourceLocation location;
	int ordinality;

	/** Optional custom renderer; {@code null} means use the default layout. */
	@Nullable
	private transient IBookPageRenderer pageRenderer;

	// So GSON.toJson doesnt like nonprimatives so imma split this like Im doing the
	// icon item thing
	public BookDataTemplate(int ordinality) {
		this.ordinality = ordinality;
	}

	public int getOrdinality() {
		return ordinality;
	}

	public void setOrdinality(int ordinality) {
		this.ordinality = ordinality;
	}

	/**
	 * Returns the custom page renderer, or {@code null} if the default
	 * title/subtitle/body layout should be used.
	 */
	@Nullable
	public IBookPageRenderer getPageRenderer() {
		return pageRenderer;
	}

	/**
	 * Attaches a custom renderer to this page template. Pass {@code null} to
	 * revert to the default layout.
	 */
	public void setPageRenderer(@Nullable IBookPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
	}

	public abstract void setChapter(String chapterName);

	public abstract void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter);

	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter,
			@Nullable BookReadTracker tracker, @Nullable UUID viewerUuid,
			@Nullable IBookKnowledge knowledge) {
		getPageScreen(pageNum, book, chapter);
	}

}
