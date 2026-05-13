package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class HLGuiGuideTitlePage extends Screen {
	private static final Identifier FALLBACK_BACKGROUND = HutosLib.rloc("textures/gui/guide/title.png");
	private static final int COVER_WIDTH = 185;
	private static final int COVER_HEIGHT = 240;
	private static final int TITLE_COLOR = 0xFFFFFFFF;
	private static final int SUBTITLE_COLOR = 0xFFE6E6E6;
	private final BookCodeModel book;
	private final Supplier<BookCodeModel> refresher;
	private int left;
	private int top;

	public HLGuiGuideTitlePage(BookCodeModel book) {
		this(book, null, null, null, null);
	}

	public HLGuiGuideTitlePage(BookCodeModel book, @Nullable BookReadTracker tracker,
			@Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge) {
		this(book, tracker, viewerUuid, knowledge, null);
	}

	public HLGuiGuideTitlePage(BookCodeModel book, @Nullable BookReadTracker tracker,
			@Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge, @Nullable Supplier<BookCodeModel> refresher) {
		super(Component.literal(titleOf(book)));
		this.book = book;
		this.refresher = refresher;
	}

	@Override
	protected void init() {
		this.left = (this.width - COVER_WIDTH) / 2;
		this.top = (this.height - COVER_HEIGHT) / 2;
		this.clearWidgets();
		List<ChapterTemplate> chapters = this.book.getChapters() != null ? this.book.getChapters() : Collections.emptyList();
		for (int i = 0; i < chapters.size(); i++) {
			ChapterTemplate chapter = chapters.get(i);
			int x = this.left + COVER_WIDTH - 1 + (i % 2 == 0 ? 0 : 7);
			int y = this.top + 34 + i * 18;
			this.addRenderableWidget(new HLTomeCategoryTab(chapter.getChapterRGB(), safe(chapter.getTitle()), i,
					x, y, 0, 0,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(this.book, chapter))));
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundOf(this.book), this.left, this.top, 0, 0, 256, 256, 256, 256);
		BookTemplate template = this.book.getTemplate();
		if (template != null && template.getOverlayLoc() != null && !template.getOverlayLoc().isBlank()) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, template.getOverlayImage(), this.left, this.top, 0, 0, 256, 256, 256, 256);
		}
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		graphics.centeredText(this.font, this.title, this.left + COVER_WIDTH / 2, this.top + 36, TITLE_COLOR);
		if (template != null && template.getSubtitle() != null) {
			graphics.centeredText(this.font, Component.literal(template.getSubtitle()), this.left + COVER_WIDTH / 2, this.top + 51, SUBTITLE_COLOR);
		}
	}

	public void refresh() {
		if (this.refresher != null) {
			Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.refresher.get(), null, null, null, this.refresher));
		}
	}

	public static void openScreenViaItem(BookCodeModel book) {
		openScreen(book, true);
	}

	public static void openScreen(BookCodeModel book, boolean ignoreNextMouseClick) {
		Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(book));
	}

	public static void openScreen(BookCodeModel book, BookReadTracker tracker, UUID viewerUuid, IBookKnowledge knowledge) {
		Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(book, tracker, viewerUuid, knowledge));
	}

	public static void openScreen(BookCodeModel book, BookReadTracker tracker, UUID viewerUuid,
			IBookKnowledge knowledge, @Nullable Supplier<BookCodeModel> refresher) {
		Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(book, tracker, viewerUuid, knowledge, refresher));
	}

	public static void refreshIfOpen() {
		if (Minecraft.getInstance().screen instanceof HLGuiGuideTitlePage page) {
			page.refresh();
		}
	}

	public static void markEntriesUnreadAndRefreshIfOpen(UUID playerId, Collection<Identifier> entryIds) {
		BookReadTracker.unacknowledge(playerId, entryIds);
		refreshIfOpen();
	}

	public static void openScreenViaItem(int pageNum, BookCodeModel book, ChapterTemplate chapterTemplate) {
		Minecraft.getInstance().setScreen(new HLGuiGuidePage(pageNum, book, chapterTemplate));
	}

	private static String titleOf(BookCodeModel book) {
		return book != null && book.getTemplate() != null ? safe(book.getTemplate().getTitle()) : "Guide";
	}

	private static Identifier backgroundOf(BookCodeModel book) {
		return book != null && book.getTemplate() != null ? book.getTemplate().getCoverImage() : FALLBACK_BACKGROUND;
	}

	private static String safe(String value) {
		return value == null || value.isBlank() ? "Untitled" : value;
	}
}
