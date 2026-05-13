package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuideTitlePage;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class HLGuideBookClient {
	public static void open(ItemGuideBook item, Player player) {
		BookCodeModel loaded = BookPlaceboReloadListener.INSTANCE.getBookByTitle(bookId(item));
		BookCodeModel book = loaded != null ? item.applyVisibilityFilters(loaded, player) : fallbackBook(item);
		HLGuiGuideTitlePage.openScreen(book, null, player.getUUID(), null,
				() -> {
					BookCodeModel refreshed = BookPlaceboReloadListener.INSTANCE.getBookByTitle(bookId(item));
					return refreshed != null ? item.applyVisibilityFilters(refreshed, player) : fallbackBook(item);
				});
	}

	private static Identifier bookId(ItemGuideBook item) {
		String prefix = item.getBookPrefix();
		if (prefix == null || prefix.isBlank()) {
			return HutosLib.rloc("guide");
		}
		return HutosLib.rloc(prefix.replaceAll("/+$", ""));
	}

	private static BookCodeModel fallbackBook(ItemGuideBook item) {
		BookCodeModel book = new BookCodeModel(bookId(item),
				new BookTemplate(
						"hutoslib:textures/gui/guide/title.png",
						"hutoslib:textures/gui/guide/guide_overlay.png",
						"HutosLib Library Mod",
						"",
						"",
						"hutoslib:hl_guide_book"));
		book.setChapters(List.of());
		return book;
	}
}
