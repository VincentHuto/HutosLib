package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuideTitlePage;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class HLGuideBookClient {
	public static void open(ItemGuideBook item, Player player) {
		BookCodeModel book = new BookCodeModel(HutosLib.rloc(item.getBookPrefix() != null ? item.getBookPrefix() : "guide"),
				new BookTemplate(
						"hutoslib:textures/gui/guide/hl_guide_book_background.png",
						"hutoslib:textures/gui/guide/hl_guide_book_overlay.png",
						"HutosLib Library Mod",
						"",
						"",
						"hutoslib:hl_guide_book"));
		book.setChapters(List.of());
		HLGuiGuideTitlePage.openScreenViaItem(book);
	}
}
