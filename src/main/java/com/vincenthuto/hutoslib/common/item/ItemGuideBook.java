package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ItemGuideBook extends Item {
	private Identifier texture;
	private String bookPrefix;
	private Function<Player, Optional<IBookKnowledge>> knowledgeProvider =
			player -> Optional.of(BookKnowledgeProvider.get(player));
	private IBookPageFilter pageFilterOverride;

	public ItemGuideBook(Properties prop, Identifier loc) {
		super(prop);
		this.texture = loc;
	}

	public static BookAnimState getOrCreateState(java.util.UUID entityUuid) {
		return new BookAnimState();
	}

	public static void clearState(java.util.UUID entityUuid) {
	}

	public BookCodeModel applyVisibilityFilters(BookCodeModel loadedBook, Player player) {
		IBookPageFilter filter = pageFilterOverride != null ? pageFilterOverride : loadedBook.getPageFilter();
		BookCodeModel filtered = filter.filter(loadedBook, player);
		IBookKnowledge knowledge = getKnowledgeProvider().apply(player).orElse(null);
		filtered = knowledge != null
				? EntryGatedBookFilter.INSTANCE.filter(filtered, knowledge)
				: EntryGatedBookFilter.INSTANCE.filter(filtered, player);
		filtered.setPageFilter(filter);
		filtered.setTheme(loadedBook.getTheme());
		return filtered;
	}

	public ItemGuideBook withBookPrefix(String prefix) {
		this.bookPrefix = prefix;
		return this;
	}

	public ItemGuideBook withKnowledgeProvider(Function<Player, Optional<IBookKnowledge>> provider) {
		this.knowledgeProvider = provider;
		return this;
	}

	public ItemGuideBook withPageFilter(IBookPageFilter filter) {
		this.pageFilterOverride = filter;
		return this;
	}

	public String getBookPrefix() {
		return bookPrefix;
	}

	public Function<Player, Optional<IBookKnowledge>> getKnowledgeProvider() {
		return knowledgeProvider;
	}

	public Identifier getTexture() {
		return texture;
	}

	public Set<Identifier> collectVisiblePageIds(Player player) {
		String prefix = bookPrefix == null || bookPrefix.isBlank() ? "guide" : bookPrefix.replaceAll("/+$", "");
		BookCodeModel loaded = BookPlaceboReloadListener.INSTANCE.getBookByTitle(HutosLib.rloc(prefix));
		if (loaded == null) {
			return Set.of();
		}
		BookCodeModel filtered = applyVisibilityFilters(loaded, player);
		Set<Identifier> ids = new HashSet<>();
		List<ChapterTemplate> chapters = filtered.getChapters();
		if (chapters == null) {
			return Set.of();
		}
		for (ChapterTemplate chapter : chapters) {
			if (chapter.getPages() == null) {
				continue;
			}
			for (BookDataTemplate page : chapter.getPages()) {
				if (page instanceof PageTemplate && page.getId() != null) {
					ids.add(page.getId());
				}
			}
		}
		return ids;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
