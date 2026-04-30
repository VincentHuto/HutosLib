package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow.ArrowDirection;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HLGuiGuidePage extends Screen {

	// -------------------------------------------------------------------------
	// Layout constants
	// -------------------------------------------------------------------------
	/** Y offset from (top + guiHeight) at which the title row begins. */
	protected static final int TEXT_TOP_OFFSET  = 220;
	/** Additional pixels dropped for a subtitle row beneath the title. */
	protected static final int SUBTITLE_Y_DELTA = 10;
	/** Additional pixels dropped for the body row beneath the subtitle (or title). */
	protected static final int BODY_Y_DELTA     = 10;
	/** Maximum width (pixels) available for title and subtitle text (slightly wider than body). */
	protected static final int HEADING_MAX_WIDTH = 165;
	/** Maximum width (pixels) available for body text. */
	protected static final int TEXT_MAX_WIDTH   = 160;
	/** Padding from the left edge of the gui. */
	protected static final int TEXT_LEFT_OFFSET = 180;

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------
	public int left;
	public int top;
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown    = 0;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	public int pageNum;
	public int guiHeight = 228;
	public int guiWidth  = 174;
	HLButtonArrow arrowF, arrowB;
	HLButtonTextured buttonTitle, buttonCloseTab;

	protected Minecraft mc = Minecraft.getInstance();
	BookDataTemplate pageTemplate;
	public BookCodeModel book;
	public ChapterTemplate chapter;

	public HLGuiGuidePage(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		super(Component.literal(""));
		this.pageNum      = pageNum;
		this.book         = book;
		this.chapter      = chapter;
		this.pageTemplate = chapter.getPages().get(pageNum);
	}

	// -------------------------------------------------------------------------
	// Screen lifecycle
	// -------------------------------------------------------------------------

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		this.clearWidgets();
		if (pageNum != (chapter.getPages().size() - 1)) {
			this.addRenderableWidget(arrowF = new HLButtonArrow(ArrowDirection.FORWARD, ARROWF,
					left + guiWidth - 18, top + guiHeight - 7, (press) -> {
						if (pageNum != (chapter.getPages().size() - 1)) {
							chapter.getPages().get(pageNum + 1).getPageScreen(pageNum + 1, book, chapter);
						} else {
							chapter.getPages().get(pageNum + 1).getPageScreen(pageNum, book, chapter);
						}
					}));
		}
		this.addRenderableWidget(
				arrowB = new HLButtonArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {
					if (pageNum > 0) {
						chapter.getPages().get(pageNum - 1).getPageScreen(pageNum - 1, book, chapter);
					} else {
						mc.setScreen(new HLGuiGuidePageTOC(book, chapter));
					}
				}));

		ResourceLocation tabTex = resolveTabTexture();
		this.addRenderableWidget(buttonTitle = new HLButtonTextured(tabTex,
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0,
				(press) -> mc.setScreen(new HLGuiGuideTitlePage(book))));

		this.addRenderableWidget(buttonCloseTab = new HLButtonTextured(tabTex,
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 192 - 16, 24, 16, 24, 32,
				(press) -> this.onClose()));
		super.init();

		// Acknowledge this page as read when the player actually views it.
		net.minecraft.world.entity.player.Player localPlayer = mc.player;
		if (localPlayer != null && pageTemplate.getId() != null) {
			BookReadTracker.acknowledge(localPlayer.getUUID(), pageTemplate.getId());
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	// -------------------------------------------------------------------------
	// Rendering
	// -------------------------------------------------------------------------

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderMenuBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, resolvePageTexture());

		int accentColor = resolveAccentColor();

		// Page number
		HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (pageNum + 1)),
				left + guiWidth - 26, top + guiHeight - 15, 50, accentColor, true);

		// Icon item
		graphics.renderFakeItem(((PageTemplate) pageTemplate).getIconItem(),
				left + guiWidth - 32, top + guiHeight - 220);

		// Page content – delegate to custom renderer if present, else default layout
		IBookPageRenderer customRenderer = pageTemplate.getPageRenderer();
		if (customRenderer != null) {
			customRenderer.render(graphics, pageTemplate, this, mouseX, mouseY, partialTicks);
		} else {
			renderDefaultPageContent(graphics, accentColor);
		}

		if (pageNum != (chapter.getPages().size() - 1)) {
			arrowF.render(graphics, mouseX, mouseY, partialTicks);
		}
		if (pageNum >= 0) {
			arrowB.render(graphics, mouseX, mouseY, partialTicks);
		}
		buttonTitle.render(graphics, mouseX, mouseY, partialTicks);
		buttonCloseTab.render(graphics, mouseX, mouseY, partialTicks);

		// Icon tooltip
		if (mouseX >= left + guiWidth - 32 && mouseX <= left + guiWidth - 10
				&& mouseY >= top + guiHeight - 220 && mouseY <= top + guiHeight - 200) {
			List<Component> text = new ArrayList<>();
			if (!((PageTemplate) pageTemplate).getIconItem().isEmpty()) {
				text.add(Component.literal(I18n.get(
						((PageTemplate) pageTemplate).getIconItem().getHoverName().getString())));
				graphics.renderComponentTooltip(font, text, left + guiWidth - 32, top + guiHeight - 220);
			}
		}

		// Tab button tooltips
		List<Component> titlePage = new ArrayList<>();
		titlePage.add(Component.literal("Title"));
		titlePage.add(Component.literal("Return to Categories"));
		if (buttonTitle.isHovered()) {
			graphics.renderComponentTooltip(font, titlePage, mouseX, mouseY);
		}
		List<Component> closePage = new ArrayList<>();
		closePage.add(Component.literal("Close Book"));
		if (buttonCloseTab.isHoveredOrFocused()) {
			graphics.renderComponentTooltip(font, closePage, mouseX, mouseY);
		}
	}

	/**
	 * Renders title → subtitle → body in a single sequential pass so each
	 * element is positioned relative to its predecessor's height instead of
	 * being hardcoded.
	 */
	protected void renderDefaultPageContent(GuiGraphics graphics, int color) {
		PageTemplate page    = (PageTemplate) pageTemplate;
		int textX            = left - guiWidth + TEXT_LEFT_OFFSET;
		int currentY         = top + guiHeight - TEXT_TOP_OFFSET;

		if (!page.getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(page.getTitle())),
					textX, currentY, HEADING_MAX_WIDTH, color, true);
			currentY += SUBTITLE_Y_DELTA;
		}

		if (!page.getSubtitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(page.getSubtitle())),
					textX, currentY, HEADING_MAX_WIDTH, color, true);
			currentY += BODY_Y_DELTA;
		}

		if (!page.getText().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(page.getText())),
					textX, currentY, TEXT_MAX_WIDTH, color, true);
		}
	}

	@Override
	protected void renderMenuBackground(GuiGraphics graphics) {
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		graphics.blit(resolvePageTexture(), left, top, 0, 0, guiWidth, guiHeight);
	}

	// -------------------------------------------------------------------------
	// Theme helpers
	// -------------------------------------------------------------------------

	/** Returns the background texture, preferring the theme's value when set. */
	protected ResourceLocation resolvePageTexture() {
		BookTheme theme = book.getTheme();
		if (theme != null && theme.backgroundTexture() != null) {
			return theme.backgroundTexture();
		}
		return ((PageTemplate) pageTemplate).getTextureLocation();
	}

	/** Returns the tab sprite-sheet, preferring the theme's value when set. */
	protected ResourceLocation resolveTabTexture() {
		BookTheme theme = book.getTheme();
		if (theme != null && theme.tabTexture() != null) {
			return theme.tabTexture();
		}
		return HLLocHelper.guiPrefix("book_tabs.png");
	}

	/** Returns the accent colour from the theme, or white if none is set. */
	protected int resolveAccentColor() {
		BookTheme theme = book.getTheme();
		if (theme != null) {
			return theme.accentColor() != 0 ? theme.accentColor() : BookTheme.DEFAULT_ACCENT;
		}
		return BookTheme.DEFAULT_ACCENT;
	}

	// -------------------------------------------------------------------------
	// Drag
	// -------------------------------------------------------------------------

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown    -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

	// -------------------------------------------------------------------------
	// Accessors / static openers
	// -------------------------------------------------------------------------

	public BookDataTemplate getPageTemplate() {
		return pageTemplate;
	}

	public void setPageTemplate(BookDataTemplate pageTemplate) {
		this.pageTemplate = pageTemplate;
	}

	public static void openScreenViaItem(int pNum, BookCodeModel pBook, ChapterTemplate pChapterTemplate) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new HLGuiGuidePage(pNum, pBook, pChapterTemplate));
	}
}
