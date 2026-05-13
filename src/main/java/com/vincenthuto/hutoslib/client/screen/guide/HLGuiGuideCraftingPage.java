package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import net.minecraft.client.Minecraft;

public class HLGuiGuideCraftingPage extends HLGuiGuidePage {
	public HLGuiGuideCraftingPage(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		super(pageNum, book, chapter);
	}

	public static void openScreenViaItem(int pageNum, BookCodeModel book, ChapterTemplate chapterTemplate) {
		Minecraft.getInstance().setScreen(new HLGuiGuideCraftingPage(pageNum, book, chapterTemplate));
	}
}
