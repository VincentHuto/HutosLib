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
import java.util.List;
import java.util.UUID;

public class HLGuiGuidePage extends Screen {
	private static final Identifier BACKGROUND = HutosLib.rloc("textures/gui/guide/hl_guide_book_background.png");
	protected final int pageNum;
	protected final BookCodeModel book;
	protected final ChapterTemplate chapter;
	protected int left;
	protected int top;

	public HLGuiGuidePage(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		this(pageNum, book, chapter, null, null, null);
	}

	public HLGuiGuidePage(int pageNum, BookCodeModel book, ChapterTemplate chapter,
			@Nullable BookReadTracker tracker, @Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge) {
		super(Component.literal(titleFor(chapter, pageNum)));
		this.pageNum = pageNum;
		this.book = book;
		this.chapter = chapter;
	}

	@Override
	protected void init() {
		this.left = (this.width - 256) / 2;
		this.top = (this.height - 192) / 2;
		this.clearWidgets();
		this.addRenderableWidget(Button.builder(Component.literal("Contents"),
				button -> Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(this.book, this.chapter)))
				.bounds(this.left + 24, this.top + 154, 78, 20)
				.build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		this.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.left, this.top, 0, 0, 256, 192, 256, 256);
		BookDataTemplate page = page();
		if (page instanceof PageTemplate template) {
			graphics.centeredText(this.font, Component.literal(safe(template.getTitle())), this.left + 128, this.top + 20, 0x3F2B1F);
			if (template.getSubtitle() != null && !template.getSubtitle().isBlank()) {
				graphics.centeredText(this.font, Component.literal(template.getSubtitle()), this.left + 128, this.top + 34, 0x5A4638);
			}
			graphics.textWithWordWrap(this.font, Component.literal(template.getText() == null ? "" : template.getText()),
					this.left + 34, this.top + 54, 188, 0x3F2B1F);
		} else {
			graphics.centeredText(this.font, Component.literal("Page " + (this.pageNum + 1)), this.left + 128, this.top + 20, 0x3F2B1F);
		}
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	protected BookDataTemplate page() {
		List<BookDataTemplate> pages = this.chapter != null ? this.chapter.getPages() : null;
		return pages != null && this.pageNum >= 0 && this.pageNum < pages.size() ? pages.get(this.pageNum) : null;
	}

	public static void openScreenViaItem(int pageNum, BookCodeModel book, ChapterTemplate chapterTemplate) {
		Minecraft.getInstance().setScreen(new HLGuiGuidePage(pageNum, book, chapterTemplate));
	}

	public static void openScreenViaItem(int pageNum, BookCodeModel book, ChapterTemplate chapterTemplate,
			@Nullable BookReadTracker tracker, @Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge) {
		Minecraft.getInstance().setScreen(new HLGuiGuidePage(pageNum, book, chapterTemplate, tracker, viewerUuid, knowledge));
	}

	private static String titleFor(ChapterTemplate chapter, int pageNum) {
		return chapter != null ? safe(chapter.getTitle()) + " " + (pageNum + 1) : "Guide Page";
	}

	private static String safe(String value) {
		return value == null || value.isBlank() ? "Untitled" : value;
	}
}
