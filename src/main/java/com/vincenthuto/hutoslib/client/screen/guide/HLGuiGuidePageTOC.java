package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import com.vincenthuto.hutoslib.common.util.HLResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HLGuiGuidePageTOC extends Screen {
	private static final Identifier BACKGROUND = HutosLib.rloc("textures/gui/guide/page.png");
	private static final Identifier PAGE_BUTTONS = HLResourceUtils.guiPrefix("page.png");
	private static final int PAGE_WIDTH = 174;
	private static final int PAGE_HEIGHT = 228;
	private static final int BODY_COLOR = 0xFFFFFFFF;
	private static final int ENTRY_X = 5;
	private static final int ENTRY_TOP = 18;
	private static final int ENTRY_STEP = 15;
	private static final int ENTRY_WIDTH = 148;
	private static final int ENTRY_HEIGHT = 14;
	private static final int ENTRY_LABEL_X = ENTRY_X + 30;
	private static final int PAGE_NUMBER_X = ENTRY_X + 5;
	private static final int LIST_BOTTOM = 210;
	private final BookCodeModel book;
	private final ChapterTemplate chapter;
	@Nullable
	private final BookReadTracker tracker;
	@Nullable
	private final UUID viewerUuid;
	@Nullable
	private final IBookKnowledge knowledge;
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
		this.tracker = tracker;
		this.viewerUuid = viewerUuid;
		this.knowledge = knowledge;
	}

	@Override
	protected void init() {
		this.left = (this.width - PAGE_WIDTH) / 2;
		this.top = (this.height - PAGE_HEIGHT) / 2;
		this.clearWidgets();
		List<BookDataTemplate> pages = this.chapter != null && this.chapter.getPages() != null
				? this.chapter.getPages()
				: Collections.emptyList();
		int visibleEntries = Math.min(pages.size(), maxVisibleEntries());
		for (int i = 0; i < visibleEntries; i++) {
			int y = this.top + ENTRY_TOP + i * ENTRY_STEP;
			int pageNum = i;
			HLButtonTextured button = new HLButtonTextured(PAGE_BUTTONS, i + 1, this.left + ENTRY_X, y, 163, ENTRY_HEIGHT, 5, 228,
					Component.empty(),
					pressed -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(pageNum, this.book, this.chapter,
							this.tracker, resolveViewerUuid(), resolveKnowledge())));
			this.addRenderableWidget(button);
		}
		if (!pages.isEmpty()) {
			this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.FORWARD, 1,
					this.left + PAGE_WIDTH - 18, this.top + PAGE_HEIGHT - 7,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(0, this.book, this.chapter,
							this.tracker, resolveViewerUuid(), resolveKnowledge()))));
		}
		this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.BACKWARD, 0,
				this.left, this.top + PAGE_HEIGHT - 7,
				button -> Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.book, this.tracker,
						resolveViewerUuid(), resolveKnowledge()))));

		Identifier tabTexture = resolveTabTexture();
		HLButtonTextured titleButton = new HLButtonTextured(tabTexture, 2,
				this.left - 24, this.top + 2, 24, 16, 24, 0,
				button -> Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.book, this.tracker,
						resolveViewerUuid(), resolveKnowledge())));
		titleButton.setTooltip(Tooltip.create(Component.literal("Return to Categories")));
		this.addRenderableWidget(titleButton);
		HLButtonTextured closeButton = new HLButtonTextured(tabTexture, 3,
				this.left - 24, this.top + 20, 24, 16, 24, 32, button -> this.onClose());
		closeButton.setTooltip(Tooltip.create(Component.literal("Close Book")));
		this.addRenderableWidget(closeButton);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundOf(this.chapter), this.left, this.top, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		List<BookDataTemplate> pages = this.chapter != null && this.chapter.getPages() != null
				? this.chapter.getPages()
				: Collections.emptyList();
		int visibleEntries = Math.min(pages.size(), maxVisibleEntries());
		graphics.enableScissor(this.left + ENTRY_X, this.top + ENTRY_TOP - 2, this.left + ENTRY_X + 163, this.top + LIST_BOTTOM);
		try {
			for (int i = 0; i < visibleEntries; i++) {
				BookDataTemplate page = pages.get(i);
				String label = page instanceof PageTemplate template ? safe(template.getTitle()) : "Page " + (i + 1);
				int entryY = this.top + ENTRY_TOP + i * ENTRY_STEP;
				graphics.text(this.font, Component.literal("Pg." + (i + 1)), this.left + PAGE_NUMBER_X, entryY + 2, BODY_COLOR, false);
				graphics.text(this.font, Component.literal(trimToEntry(label)), this.left + ENTRY_LABEL_X, entryY + 3, BODY_COLOR, false);
			}
		} finally {
			graphics.disableScissor();
		}
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

	private String trimToEntry(String value) {
		return this.font.plainSubstrByWidth(value, ENTRY_WIDTH - ENTRY_LABEL_X + ENTRY_X - 4);
	}

	private static int maxVisibleEntries() {
		return Math.max(0, (LIST_BOTTOM - ENTRY_TOP - ENTRY_HEIGHT) / ENTRY_STEP + 1);
	}

	private static Identifier backgroundOf(ChapterTemplate chapter) {
		return chapter != null && chapter.getTexture() != null && !chapter.getTexture().isBlank()
				? chapter.getTextureLocation()
				: BACKGROUND;
	}

	private Identifier resolveTabTexture() {
		BookTheme theme = this.book != null ? this.book.getTheme() : null;
		if (theme != null && theme.tabTexture() != null) {
			return theme.tabTexture();
		}
		return HLResourceUtils.guiPrefix("book_tabs.png");
	}

	@Nullable
	private UUID resolveViewerUuid() {
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		return this.viewerUuid != null ? this.viewerUuid : (localPlayer != null ? localPlayer.getUUID() : null);
	}

	@Nullable
	private IBookKnowledge resolveKnowledge() {
		if (this.knowledge != null) {
			return this.knowledge;
		}
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		return localPlayer != null ? BookKnowledgeProvider.get(localPlayer) : null;
	}
}
