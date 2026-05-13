package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import com.vincenthuto.hutoslib.common.util.HLResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HLGuiGuidePage extends Screen {
	private static final Identifier BACKGROUND = HutosLib.rloc("textures/gui/guide/page.png");
	private static final Identifier PAGE_BUTTONS = HLResourceUtils.guiPrefix("page.png");
	private static final int PAGE_WIDTH = 174;
	private static final int PAGE_HEIGHT = 256;
	private static final int TITLE_COLOR = 0xFFFFFFFF;
	private static final int SUBTITLE_COLOR = 0xFFE6E6E6;
	private static final int BODY_COLOR = 0xFFFFFFFF;
	private static final int BUTTON_TEXT_COLOR = 0xFFFFFFFF;
	private static final int TEXT_LEFT = 14;
	private static final int TEXT_WIDTH = 146;
	private static final int HEADER_ICON_Y = 16;
	private static final int BODY_BOTTOM = 212;
	private static final int LINE_HEIGHT = 9;
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
		this.left = (this.width - PAGE_WIDTH) / 2;
		this.top = (this.height - PAGE_HEIGHT) / 2;
		this.clearWidgets();
		this.addRenderableWidget(new HLButtonTextured(PAGE_BUTTONS, 0, this.left + 16, this.top + 229, 74, 14, 5, 228,
				Component.empty(),
				button -> Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(this.book, this.chapter))));
		if (this.pageNum > 0) {
			this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.BACKWARD, 1,
					this.left + 115, this.top + 229,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(this.pageNum - 1, this.book, this.chapter))));
		}
		List<BookDataTemplate> pages = this.chapter != null ? this.chapter.getPages() : null;
		if (pages != null && this.pageNum + 1 < pages.size()) {
			this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.FORWARD, 2,
					this.left + 145, this.top + 229,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(this.pageNum + 1, this.book, this.chapter))));
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		BookDataTemplate page = page();
		graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundOf(page, this.chapter), this.left, this.top, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		graphics.centeredText(this.font, Component.literal("Contents"), this.left + 53, this.top + 232, BUTTON_TEXT_COLOR);
		if (page instanceof PageTemplate template) {
			boolean hasIcon = !template.getIconItem().isEmpty();
			boolean hasSubtitle = template.getSubtitle() != null && !template.getSubtitle().isBlank();
			int titleY = hasIcon ? this.top + 38 : this.top + 24;
			if (hasIcon) {
				graphics.fakeItem(template.getIconItem(), this.left + PAGE_WIDTH / 2 - 8, this.top + HEADER_ICON_Y);
			}
			drawLeftAligned(graphics, Component.literal(safe(template.getTitle())), titleY, TITLE_COLOR);
			if (hasSubtitle) {
				drawLeftAligned(graphics, Component.literal(template.getSubtitle()), titleY + 13, SUBTITLE_COLOR);
			}
			int bodyY = titleY + (hasSubtitle ? 31 : 18);
			drawWrappedBody(graphics, Component.literal(template.getText() == null ? "" : template.getText()), bodyY);
		} else {
			graphics.centeredText(this.font, Component.literal("Page " + (this.pageNum + 1)), this.left + PAGE_WIDTH / 2, this.top + 24, TITLE_COLOR);
		}
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

	private void drawWrappedBody(GuiGraphicsExtractor graphics, Component text, int y) {
		int bottomY = this.top + BODY_BOTTOM;
		graphics.enableScissor(this.left + TEXT_LEFT, y, this.left + TEXT_LEFT + TEXT_WIDTH, bottomY);
		try {
			for (FormattedCharSequence line : this.font.split(text, TEXT_WIDTH)) {
				if (y + LINE_HEIGHT > bottomY) {
					return;
				}
				drawLeftAligned(graphics, line, y, BODY_COLOR);
				y += LINE_HEIGHT;
			}
		} finally {
			graphics.disableScissor();
		}
	}

	private void drawLeftAligned(GuiGraphicsExtractor graphics, Component text, int y, int color) {
		graphics.text(this.font, trimToWidth(text), this.left + TEXT_LEFT, y, color, false);
	}

	private void drawLeftAligned(GuiGraphicsExtractor graphics, FormattedCharSequence text, int y, int color) {
		graphics.text(this.font, text, this.left + TEXT_LEFT, y, color, false);
	}

	private Component trimToWidth(Component text) {
		return Component.literal(this.font.plainSubstrByWidth(text.getString(), TEXT_WIDTH));
	}

	private static Identifier backgroundOf(BookDataTemplate page, ChapterTemplate chapter) {
		if (page instanceof PageTemplate template && template.getTexture() != null && !template.getTexture().isBlank()) {
			return template.getTextureLocation();
		}
		if (chapter != null && chapter.getTexture() != null && !chapter.getTexture().isBlank()) {
			return chapter.getTextureLocation();
		}
		return BACKGROUND;
	}
}
