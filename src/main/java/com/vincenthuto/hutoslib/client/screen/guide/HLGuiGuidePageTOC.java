package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HLGuiGuidePageTOC extends Screen {
	private static final Identifier BACKGROUND = HutosLib.rloc("textures/gui/guide/hl_guide_book_background.png");
	private final BookCodeModel book;
	private final ChapterTemplate chapter;
	private int left;
	private int top;

	public HLGuiGuidePageTOC(BookCodeModel book, ChapterTemplate chapterTemplate) {
		this(book, chapterTemplate, null, null, null);
	}

	public HLGuiGuidePageTOC(BookCodeModel book, ChapterTemplate chapterTemplate,
			@Nullable BookReadTracker tracker, @Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge) {
		super(Component.literal(chapterTemplate != null ? safe(chapterTemplate.getTitle()) : "Chapter"));
		this.book = book;
		this.chapter = chapterTemplate;
	}

	@Override
	protected void init() {
		this.left = (this.width - 256) / 2;
		this.top = (this.height - 192) / 2;
		this.clearWidgets();
		this.addRenderableWidget(Button.builder(Component.literal("Back"),
				button -> Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.book)))
				.bounds(this.left + 24, this.top + 154, 64, 20)
				.build());
		List<BookDataTemplate> pages = this.chapter != null && this.chapter.getPages() != null
				? this.chapter.getPages()
				: Collections.emptyList();
		for (int i = 0; i < pages.size(); i++) {
			BookDataTemplate page = pages.get(i);
			String label = page instanceof PageTemplate template ? safe(template.getTitle()) : "Page " + (i + 1);
			int y = this.top + 42 + i * 20;
			int pageNum = i;
			this.addRenderableWidget(Button.builder(Component.literal(label),
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(pageNum, this.book, this.chapter)))
					.bounds(this.left + 44, y, 168, 18)
					.build());
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		this.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.left, this.top, 0, 0, 256, 192, 256, 256);
		graphics.centeredText(this.font, this.title, this.left + 128, this.top + 18, 0x3F2B1F);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	public static void openScreenViaItem(int pageNum, BookCodeModel book, ChapterTemplate chapterTemplate) {
		Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(book, chapterTemplate));
	}

	public static void openScreenViaItem(BookCodeModel book, ChapterTemplate chapterTemplate,
			BookReadTracker tracker, UUID viewerUuid, IBookKnowledge knowledge) {
		Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(book, chapterTemplate, tracker, viewerUuid, knowledge));
	}

	private static String safe(String value) {
		return value == null || value.isBlank() ? "Untitled" : value;
	}
}
