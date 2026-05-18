package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HLGuiGuidePage extends Screen {
	private static final Identifier BACKGROUND = HutosLib.rloc("textures/gui/guide/page.png");
	private static final int PAGE_WIDTH = 174;
	private static final int PAGE_HEIGHT = 228;
	private static final int TEXT_LEFT = 6;
	private static final int TITLE_TOP = 8;
	private static final int SUBTITLE_OFFSET = 10;
	private static final int BODY_OFFSET = 10;
	private static final int HEADING_WIDTH = 165;
	private static final int BODY_WIDTH = 160;
	private static final int ICON_X = PAGE_WIDTH - 32;
	private static final int ICON_Y = 8;
	private static final int PAGE_NUMBER_X = PAGE_WIDTH - 26;
	private static final int PAGE_NUMBER_Y = PAGE_HEIGHT - 15;
	protected final int pageNum;
	protected final BookCodeModel book;
	protected final ChapterTemplate chapter;
	@Nullable
	private final BookReadTracker tracker;
	@Nullable
	private final UUID viewerUuid;
	@Nullable
	private final IBookKnowledge knowledge;
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
		this.tracker = tracker;
		this.viewerUuid = viewerUuid;
		this.knowledge = knowledge;
	}

	@Override
	protected void init() {
		this.left = (this.width - PAGE_WIDTH) / 2;
		this.top = (this.height - PAGE_HEIGHT) / 2;
		this.clearWidgets();
		this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.BACKWARD, 1,
				this.left, this.top + PAGE_HEIGHT - 7,
				button -> {
					if (this.pageNum > 0) {
						Minecraft.getInstance().setScreen(new HLGuiGuidePage(this.pageNum - 1, this.book, this.chapter,
								this.tracker, resolveViewerUuid(), resolveKnowledge()));
					} else {
						Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(this.book, this.chapter,
								this.tracker, resolveViewerUuid(), resolveKnowledge()));
					}
				}));
		List<BookDataTemplate> pages = this.chapter != null ? this.chapter.getPages() : null;
		if (pages != null && this.pageNum + 1 < pages.size()) {
			this.addRenderableWidget(new HLButtonArrow(HLButtonArrow.ArrowDirection.FORWARD, 2,
					this.left + PAGE_WIDTH - 18, this.top + PAGE_HEIGHT - 7,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePage(this.pageNum + 1, this.book, this.chapter,
							this.tracker, resolveViewerUuid(), resolveKnowledge()))));
		}
		Identifier tabTexture = resolveTabTexture();
		HLButtonTextured titleButton = new HLButtonTextured(tabTexture, 3,
				this.left - 24, this.top + 2, 24, 16, 24, 0,
				button -> Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.book, this.tracker,
						resolveViewerUuid(), resolveKnowledge())));
		titleButton.setTooltip(Tooltip.create(Component.literal("Return to Categories")));
		this.addRenderableWidget(titleButton);
		HLButtonTextured closeButton = new HLButtonTextured(tabTexture, 4,
				this.left - 24, this.top + 20, 24, 16, 24, 32, button -> this.onClose());
		closeButton.setTooltip(Tooltip.create(Component.literal("Close Book")));
		this.addRenderableWidget(closeButton);
		acknowledgePage();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		BookDataTemplate page = page();
		graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundOf(page, this.chapter), this.left, this.top, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 256, 256);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		int accentColor = 0xFF000000 | resolveAccentColor();
		HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal("Pg." + (this.pageNum + 1)),
				this.left + PAGE_NUMBER_X, this.top + PAGE_NUMBER_Y, 50, accentColor, true);
		if (page instanceof PageTemplate template) {
			drawIcon(graphics, template, mouseX, mouseY);
			drawPageContent(graphics, template, accentColor);
		} else {
			HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal("Page " + (this.pageNum + 1)),
					this.left + TEXT_LEFT, this.top + TITLE_TOP, HEADING_WIDTH, accentColor, true);
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

	private static Identifier backgroundOf(BookDataTemplate page, ChapterTemplate chapter) {
		if (page instanceof PageTemplate template && template.getTexture() != null && !template.getTexture().isBlank()) {
			return template.getTextureLocation();
		}
		if (chapter != null && chapter.getTexture() != null && !chapter.getTexture().isBlank()) {
			return chapter.getTextureLocation();
		}
		return BACKGROUND;
	}

	private void drawPageContent(GuiGraphicsExtractor graphics, PageTemplate template, int color) {
		int textX = this.left + TEXT_LEFT;
		int currentY = this.top + TITLE_TOP;
		String title = template.getTitle();
		if (title != null && !title.isBlank()) {
			HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal(I18n.get(title)),
					textX, currentY, HEADING_WIDTH, color, true);
			currentY += SUBTITLE_OFFSET;
		}
		String subtitle = template.getSubtitle();
		if (subtitle != null && !subtitle.isBlank()) {
			HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal(I18n.get(subtitle)),
					textX, currentY, HEADING_WIDTH, color, true);
			currentY += BODY_OFFSET;
		}
		String body = template.getText();
		if (body != null && !body.isBlank()) {
			HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal(I18n.get(body)),
					textX, currentY, BODY_WIDTH, color, true);
		}
	}

	private void drawIcon(GuiGraphicsExtractor graphics, PageTemplate template, int mouseX, int mouseY) {
		if (template.getIconItem().isEmpty()) {
			return;
		}
		int iconX = this.left + ICON_X;
		int iconY = this.top + ICON_Y;
		graphics.fakeItem(template.getIconItem(), iconX, iconY);
		if (mouseX >= iconX && mouseX <= iconX + 22 && mouseY >= iconY && mouseY <= iconY + 20) {
			graphics.setComponentTooltipForNextFrame(this.font, List.of(template.getIconItem().getHoverName()),
					mouseX, mouseY);
		}
	}

	private Identifier resolveTabTexture() {
		BookTheme theme = this.book != null ? this.book.getTheme() : null;
		if (theme != null && theme.tabTexture() != null) {
			return theme.tabTexture();
		}
		return HLResourceUtils.guiPrefix("book_tabs.png");
	}

	private int resolveAccentColor() {
		BookTheme theme = this.book != null ? this.book.getTheme() : null;
		if (theme != null) {
			return theme.accentColor() != 0 ? theme.accentColor() : BookTheme.DEFAULT_ACCENT;
		}
		return BookTheme.DEFAULT_ACCENT;
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

	private void acknowledgePage() {
		BookDataTemplate page = page();
		if (page == null) {
			return;
		}
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		if (localPlayer != null && page.getId() != null) {
			BookReadTracker.acknowledge(localPlayer.getUUID(), page.getId());
		}
	}
}
