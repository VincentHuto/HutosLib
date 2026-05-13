package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Collections;
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
		return Collections.emptySet();
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
