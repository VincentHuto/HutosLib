package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow.ArrowDirection;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HLGuiGuidePageTOC extends Screen {
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown    = 0;
	protected int left;
	protected int top;
	public int guiHeight = 228, guiWidth = 174;
	HLButtonTextured buttonTitle, buttonCloseTab;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	HLButtonArrow arrowF, arrowB;
	public List<HLButtonTextured> pageButtons = new ArrayList<>();
	private final ChapterTemplate chapterTemplate;
	private final BookCodeModel book;

	/** Optional tracker for highlighting unread pages. */
	@Nullable
	private final BookReadTracker tracker;
	/** UUID of the viewing player (needed by {@link BookReadTracker}). */
	@Nullable
	private final UUID viewerUuid;
	/** Optional knowledge source for counting unread pages per chapter. */
	@Nullable
	private final IBookKnowledge knowledge;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/** Basic constructor – no unread badges. */
	public HLGuiGuidePageTOC(BookCodeModel book, ChapterTemplate chapterTemplate) {
		this(book, chapterTemplate, null, null, null);
	}

	/**
	 * Extended constructor that enables unread-badge rendering.
	 *
	 * @param tracker    the client-side read tracker (or {@code null} to disable)
	 * @param viewerUuid UUID of the player opening the screen
	 * @param knowledge  the player's book knowledge capability
	 */
	public HLGuiGuidePageTOC(BookCodeModel book, ChapterTemplate chapterTemplate,
			@Nullable BookReadTracker tracker, @Nullable UUID viewerUuid,
			@Nullable IBookKnowledge knowledge) {
		super(Component.literal(chapterTemplate.getTitle()));
		this.chapterTemplate = chapterTemplate;
		this.book            = book;
		this.tracker         = tracker;
		this.viewerUuid      = viewerUuid;
		this.knowledge       = knowledge;
	}

	// -------------------------------------------------------------------------
	// Screen lifecycle
	// -------------------------------------------------------------------------

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		int sideLoc    = left + guiWidth;
		int verticalLoc = top + guiHeight;
		this.clearWidgets();
		pageButtons.clear();
		super.init();

		for (int i = 0; i < chapterTemplate.getPages().size(); i++) {
			pageButtons.add(new HLButtonTextured(chapterTemplate.getTextureLocation(), i, sideLoc - (guiWidth - 5),
					(verticalLoc - 210) + (i * 15), 163, 14, 5, 228, (press) -> {
						if (press instanceof HLButtonTextured button) {
							chapterTemplate.getPages().get(button.getId()).getPageScreen(button.getId(), book,
									chapterTemplate);
						}
					}));
		}

		for (HLButtonTextured pageButton : pageButtons) {
			this.addRenderableWidget(pageButton);
		}

		this.addRenderableWidget(arrowF = new HLButtonArrow(ArrowDirection.FORWARD, ARROWF,
				left + guiWidth - 18, top + guiHeight - 7,
				(press) -> chapterTemplate.getPages().get(0).getPageScreen(0, book, chapterTemplate)));

		this.addRenderableWidget(arrowB = new HLButtonArrow(ArrowDirection.BACKWARD, ARROWB,
				left, top + guiHeight - 7,
				(press) -> book.getTemplate().getPageScreen(0, book, null)));

		ResourceLocation tabTex = resolveTabTexture();
		this.addRenderableWidget(buttonTitle = new HLButtonTextured(tabTex, TITLEBUTTON,
				left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0,
				(press) -> book.getTemplate().getPageScreen(0, book, null)));

		this.addRenderableWidget(buttonCloseTab = new HLButtonTextured(tabTex, CLOSEBUTTON,
				left - guiWidth + 150, top + guiHeight - 192 - 16, 24, 16, 24, 32,
				(press) -> this.onClose()));
	}

	// -------------------------------------------------------------------------
	// Rendering
	// -------------------------------------------------------------------------

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderMenuBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		pageButtons.sort(Comparator.comparingInt(HLButtonTextured::getId));

		int accentColor = resolveAccentColor();

		// Determine UUID and knowledge to use – prefer explicitly-provided values,
		// otherwise fall back to the local player so the standard opening path works too.
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		UUID resolvedUuid = viewerUuid != null ? viewerUuid
				: (localPlayer != null ? localPlayer.getUUID() : null);
		IBookKnowledge resolvedKnowledge = knowledge != null ? knowledge
				: (localPlayer != null ? BookKnowledgeProvider.get(localPlayer) : null);

		for (int i = 0; i < pageButtons.size(); i++) {
			HLButtonTextured btn = pageButtons.get(i);
			btn.render(graphics, mouseX, mouseY, partialTicks);
			HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (i + 1)),
					btn.posX + 5, btn.posY + 2, 150, 0xffffff, true);
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(((PageTemplate) chapterTemplate.getPages().get(i)).getTitle()),
					btn.posX + 30, btn.posY + 2, 150, 0xffffff, true);

			// Unread dot indicator
			if (resolvedUuid != null) {
				ResourceLocation pageId = chapterTemplate.getPages().get(i).getId();
				boolean unread = pageId != null
						? !BookReadTracker.isAcknowledged(resolvedUuid, pageId)
						: resolvedKnowledge != null && buildPagePrefix(i) != null
								&& BookReadTracker.countUnread(resolvedUuid, resolvedKnowledge, buildPagePrefix(i)) > 0;
				if (unread) {
					graphics.fill(btn.posX + btn.getWidth() - 8, btn.posY + 3,
							btn.posX + btn.getWidth() - 3, btn.posY + 8, 0xFF000000 | accentColor);
				}
			}
		}

		arrowF.render(graphics, mouseX, mouseY, partialTicks);
		arrowB.render(graphics, mouseX, mouseY, partialTicks);
		buttonTitle.render(graphics, mouseX, mouseY, partialTicks);
		buttonCloseTab.render(graphics, mouseX, mouseY, partialTicks);

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

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		// TOC renders its own panel background; suppress Screen's default in-world blur path.
	}

	@Override
	protected void renderBlurredBackground(float partialTicks) {
		// Explicitly disable blur for this screen.
	}

	@Override
	protected void renderMenuBackground(GuiGraphics graphics) {
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		graphics.blit(chapterTemplate.getTextureLocation(), left, top, 0, 0, guiWidth, guiHeight);
	}

	// -------------------------------------------------------------------------
	// Theme helpers
	// -------------------------------------------------------------------------

	private ResourceLocation resolveTabTexture() {
		BookTheme theme = book.getTheme();
		if (theme != null && theme.tabTexture() != null) {
			return theme.tabTexture();
		}
		return HLLocHelper.guiPrefix("book_tabs.png");
	}

	private int resolveAccentColor() {
		BookTheme theme = book.getTheme();
		if (theme != null) {
			return theme.accentColor() != 0 ? theme.accentColor() : BookTheme.DEFAULT_ACCENT;
		}
		return BookTheme.DEFAULT_ACCENT;
	}

	/**
	 * Derives a {@link BookReadTracker} prefix string for page {@code index}.
	 * Uses the page's {@link net.minecraft.resources.ResourceLocation} ID if available,
	 * falling back to the book's entry prefix.
	 */
	@Nullable
	private String buildPagePrefix(int index) {
		var page = chapterTemplate.getPages().get(index);
		if (page.getId() != null) {
			return page.getId().getPath();
		}
		return null;
	}

	// -------------------------------------------------------------------------
	// Drag / misc
	// -------------------------------------------------------------------------

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown    -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

	// -------------------------------------------------------------------------
	// Static openers
	// -------------------------------------------------------------------------

	public static void openScreenViaItem(int pNum, BookCodeModel pBook, ChapterTemplate pChapterTemplate) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new HLGuiGuidePageTOC(pBook, pChapterTemplate));
	}

	/**
	 * Opens the TOC screen with unread-badge support.
	 *
	 * @param pBook             the book model
	 * @param pChapterTemplate  the chapter to show
	 * @param tracker           client-side read tracker
	 * @param viewerUuid        UUID of the viewing player
	 * @param knowledge         the player's book knowledge
	 */
	public static void openScreenViaItem(BookCodeModel pBook, ChapterTemplate pChapterTemplate,
			BookReadTracker tracker, UUID viewerUuid, IBookKnowledge knowledge) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new HLGuiGuidePageTOC(pBook, pChapterTemplate, tracker, viewerUuid, knowledge));
	}
}
