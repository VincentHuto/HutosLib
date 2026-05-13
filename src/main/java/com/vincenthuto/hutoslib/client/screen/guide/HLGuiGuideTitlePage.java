package com.vincenthuto.hutoslib.client.screen.guide;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLTextUtils;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;

public class HLGuiGuideTitlePage extends Screen {

	private static HLGuiGuideTitlePage screen;
	final Identifier texture;
	final Identifier overlay;
	int guiWidth = 186;
	int guiHeight = 240;
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown    = 0;
	int left, top;
	final int BUTTONCLOSE = 30;

	public ItemStack icon;
	HLButtonTextured buttonclose;
	public List<ChapterTemplate> chapters = new ArrayList<>();
	public List<HLButtonTextured> buttonList = new ArrayList<>();
	private BookCodeModel book;

	/** Optional tracker for displaying total unread count. */
	@Nullable
	private final BookReadTracker tracker;
	/** UUID of the viewing player (needed by {@link BookReadTracker}). */
	@Nullable
	private final UUID viewerUuid;
	/** Optional knowledge source for computing the unread badge count. */
	@Nullable
	private final IBookKnowledge knowledge;
	/**
	 * Optional supplier that, when invoked, returns a freshly-filtered
	 * {@link BookCodeModel} reflecting the player's current knowledge. Set by
	 * the opener (e.g. {@code BloodyBookItem.use}) so that
	 * {@link #refresh()} can rebuild the visible chapter list when the
	 * server pushes a knowledge sync while the book is open.
	 */
	@Nullable
	private java.util.function.Supplier<BookCodeModel> refresher;

	// -------------------------------------------------------------------------
	// Static openers
	// -------------------------------------------------------------------------

	public static void openScreenViaItem(BookCodeModel book) {
		openScreen(book, true);
	}

	public static void openScreen(BookCodeModel book, boolean ignoreNextMouseClick) {
		if (screen == null) {
			screen = new HLGuiGuideTitlePage(book);
		}
		screen = new HLGuiGuideTitlePage(book);
		Minecraft.getInstance().setScreen(screen);
	}

	/**
	 * Opens the title page with unread-count display support.
	 *
	 * @param book       the book model
	 * @param tracker    client-side read tracker
	 * @param viewerUuid UUID of the viewing player
	 * @param knowledge  the player's book knowledge
	 */
	public static void openScreen(BookCodeModel book, BookReadTracker tracker,
			UUID viewerUuid, IBookKnowledge knowledge) {
		Minecraft.getInstance().setScreen(
				new HLGuiGuideTitlePage(book, tracker, viewerUuid, knowledge));
	}

	/**
	 * Same as {@link #openScreen(BookCodeModel, BookReadTracker, UUID, IBookKnowledge)},
	 * but also installs a refresher supplier that
	 * {@link #refreshIfOpen()} can use to rebuild the visible chapters when
	 * the player's knowledge changes (e.g. on a server sync packet) while the
	 * book is open. The supplier should re-run the page-visibility filters
	 * against the freshest knowledge.
	 */
	public static void openScreen(BookCodeModel book, BookReadTracker tracker,
			UUID viewerUuid, IBookKnowledge knowledge,
			@Nullable java.util.function.Supplier<BookCodeModel> refresher) {
		HLGuiGuideTitlePage page = new HLGuiGuideTitlePage(book, tracker, viewerUuid, knowledge);
		page.refresher = refresher;
		Minecraft.getInstance().setScreen(page);
	}

	/**
	 * If the currently-open Minecraft screen is an {@link HLGuiGuideTitlePage}
	 * with an installed refresher, re-applies its filter and rebuilds the
	 * chapter buttons in place. Safe to call from any thread that has already
	 * been hopped to the client thread (e.g. inside
	 * {@code IPayloadContext#enqueueWork}).
	 */
	public static void refreshIfOpen() {
		Screen current = Minecraft.getInstance().screen;
		if (current instanceof HLGuiGuideTitlePage page) {
			page.refresh();
		}
	}

	/**
	 * Marks the supplied entries unread for {@code playerId}, then refreshes the
	 * open guide title page if one is present. This is useful for mods that sync
	 * newly-discovered book entries from the server and need existing stale
	 * client read-state to stop suppressing unread/new-entry badges.
	 */
	public static void markEntriesUnreadAndRefreshIfOpen(UUID playerId, Collection<Identifier> entryIds) {
		BookReadTracker.unacknowledge(playerId, entryIds);
		refreshIfOpen();
	}

	/**
	 * Re-runs the installed refresher (if any), swaps in the new
	 * {@link BookCodeModel}, and rebuilds widgets via {@link #init()}. No-op
	 * when no refresher was provided.
	 */
	public void refresh() {
		if (refresher == null) {
			return;
		}
		BookCodeModel refreshed = refresher.get();
		if (refreshed == null) {
			return;
		}
		this.book = refreshed;
		this.chapters = refreshed.getChapters();
		// Rebuild buttons against the new chapter list.
		this.init(Minecraft.getInstance(), this.width, this.height);
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public HLGuiGuideTitlePage(BookCodeModel book) {
		this(book, null, null, null);
	}

	public HLGuiGuideTitlePage(BookCodeModel book, @Nullable BookReadTracker tracker,
			@Nullable UUID viewerUuid, @Nullable IBookKnowledge knowledge) {
		super(Component.literal(book.getTemplate().getTitle()));
		this.book       = book;
		this.icon       = book.getTemplate().getIconItem();
		this.chapters   = book.getChapters();
		this.texture    = book.getTemplate().getCoverImage();
		this.overlay    = book.getTemplate().getOverlayImage();
		this.tracker    = tracker;
		this.viewerUuid = viewerUuid;
		this.knowledge  = knowledge;
	}

	public void setBook(BookCodeModel book) {
		this.book = book;
	}

	public BookCodeModel getBook() {
		return book;
	}

	// -------------------------------------------------------------------------
	// Screen lifecycle
	// -------------------------------------------------------------------------

	@Override
	public void init() {
		Random rand = new Random();
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.buttonList.clear();
		this.clearWidgets();

		Identifier overlayTex = resolveOverlayTexture();
//		this.addRenderableWidget(
//				buttonclose = new HLButtonTextured(overlayTex, BUTTONCLOSE,
//						(int) (centerX + (guiWidth * 0.05f)),
//						(int) (centerY + (guiHeight * 0.78f)), 32, 32, 209, 32,
//						(press) -> onClose()));

		chapters.sort(Comparator.comparingInt(ChapterTemplate::getOrdinality));

		for (int i = 0; i < chapters.size(); i++) {
			HLTomeCategoryTab tab = new HLTomeCategoryTab(chapters.get(i).getChapterRGB(),
					HLTextUtils.toProperCase(chapters.get(i).getTitle()), i,
					(int) (centerX + (guiWidth * 0.05f) + 167 + (rand.nextInt(6) - rand.nextInt(4))),
					centerY - (i * -25) + 18, 0, 192, (press) -> {
						if (press instanceof HLButtonTextured button) {
							Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(book, chapters.get(button.id), tracker,
									resolveViewerUuid(), resolveKnowledge()));
						}
					});
			buttonList.add(tab);
			this.addRenderableWidget(buttonList.get(i));
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
		if (p_96552_ == GLFW.GLFW_KEY_E || p_96552_ == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
			this.onClose();
		}
		return true;
	}

	// -------------------------------------------------------------------------
	// Rendering
	// -------------------------------------------------------------------------

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		this.renderMenuBackground(graphics);
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		chapters.sort(Comparator.comparingInt(ChapterTemplate::getOrdinality));

		int titleColor = resolveAccentColor();

		if (!this.title.getString().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, this.title, centerX + 10, centerY + 10, 165, titleColor, true);
		}

		// Determine UUID and knowledge to use – prefer explicitly-provided values,
		// otherwise fall back to the local player so the standard opening path works too.
		UUID resolvedUuid = resolveViewerUuid();
		IBookKnowledge resolvedKnowledge = resolveKnowledge();

		// Unread count badge
		if (resolvedUuid != null) {
			int unread = countUnreadForBook(resolvedUuid, resolvedKnowledge);
			if (unread > 0) {
				Component badge = Component.literal(unread + " new");
				HLGuiUtils.drawMaxWidthString(font, badge, centerX + 10, centerY + 20, 100, titleColor, true);
			}
		}

		matrixStack.pushPose();
		left = width / 2 - guiWidth / 2;
		top  = height / 2 - guiHeight / 2;
		graphics.renderFakeItem(icon, left + guiWidth - 48, top + guiHeight - 230);
		matrixStack.popPose();

		for (HLButtonTextured element : buttonList) {
			if (element instanceof HLTomeCategoryTab tab) {
				RenderSystem.setShaderColor(tab.color.getRed() / 255, tab.color.getGreen() / 255,
						tab.color.getBlue() / 255, 1.0F);
				element.render(graphics, mouseX, mouseY, partialTicks);
				RenderSystem.setShaderColor(1, 1, 1, 1.0F);

				// Unread dot indicator on chapter tassel
				if (resolvedUuid != null && tab.id >= 0 && tab.id < chapters.size()) {
					if (countUnreadForChapter(resolvedUuid, chapters.get(tab.id), resolvedKnowledge) > 0) {
						graphics.fill(tab.posX + tab.getWidth() - 7, tab.posY + 2,
								tab.posX + tab.getWidth() - 3, tab.posY + 6, 0xFF000000 | resolveAccentColor());
					}
				}
			} else {
				element.render(graphics, mouseX, mouseY, partialTicks);
			}

			if (element.isHoveredOrFocused()) {
				graphics.renderTooltip(font, element.text, element.getX(), element.getY());
			}
		}

//		this.buttonclose.render(graphics, mouseX, mouseY, partialTicks);
//		if (this.buttonclose.isHoveredOrFocused()) {
//			graphics.renderTooltip(font, Component.literal("Close"),
//					this.buttonclose.getX(), this.buttonclose.getY());
//		}
	}

	@Override
	protected void renderMenuBackground(GuiGraphics graphics) {
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		graphics.blit(texture,             centerX, centerY, 0, 0, guiWidth, guiHeight);
		graphics.blit(resolveOverlayTexture(), centerX, centerY, 0, 0, guiWidth, guiHeight);
	}

	// -------------------------------------------------------------------------
	// Theme helpers
	// -------------------------------------------------------------------------

	private Identifier resolveOverlayTexture() {
		BookTheme theme = book.getTheme();
		if (theme != null && theme.backgroundTexture() != null) {
			return theme.backgroundTexture();
		}
		return overlay;
	}

	private int resolveAccentColor() {
		BookTheme theme = book.getTheme();
		if (theme != null) {
			return theme.accentColor() != 0 ? theme.accentColor() : BookTheme.DEFAULT_ACCENT;
		}
		return BookTheme.DEFAULT_ACCENT;
	}

	@Nullable
	private UUID resolveViewerUuid() {
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		return viewerUuid != null ? viewerUuid : (localPlayer != null ? localPlayer.getUUID() : null);
	}

	@Nullable
	private IBookKnowledge resolveKnowledge() {
		if (knowledge != null) {
			return knowledge;
		}
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		return localPlayer != null ? BookKnowledgeProvider.get(localPlayer) : null;
	}

	/**
	 * Derives a {@link BookReadTracker} prefix string for the given chapter.
	 *
	 * <p>Chapter resource IDs have the form {@code namespace:book/chapter/file}
	 * (three path segments). The logical chapter prefix used by knowledge entries
	 * is the first two segments — {@code book/chapter/} — NOT the full path. Using
	 * the full path ({@code book/chapter/file/}) would never match any knowledge
	 * entry because entries don't nest under the chapter JSON file name.
	 *
	 * <p>Returns {@code null} if the chapter has no assigned ID or if the ID
	 * contains fewer than three path segments (malformed).
	 */
	@Nullable
	private String buildChapterPrefix(ChapterTemplate chapter) {
		if (chapter.getId() == null) {
			return null;
		}
		String path = chapter.getId().getPath();
		int first = path.indexOf('/');
		if (first < 0) {
			return null; // malformed – single-segment ID has no chapter component
		}
		int second = path.indexOf('/', first + 1);
		if (second < 0) {
			return null; // malformed – only two segments, no file component
		}
		// Return book/chapter/ (the first two path segments), which scopes to all
		// knowledge entries that belong to this chapter.
		return path.substring(0, second + 1);
	}

	// -------------------------------------------------------------------------
	// Drag / misc
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
	// Legacy/static openers
	// -------------------------------------------------------------------------

	public static void openScreenViaItem(int pNum, BookCodeModel pBook, ChapterTemplate pChapterTemplate) {
		Minecraft mc = Minecraft.getInstance();
		// Apply page filter before showing the book
		net.minecraft.world.entity.player.Player player = mc.player;
		BookCodeModel filtered = player != null
				? pBook.getPageFilter().filter(pBook, player)
				: pBook;
		mc.setScreen(new HLGuiGuideTitlePage(filtered));
	}

	private int countUnreadForBook(UUID playerId, @Nullable IBookKnowledge knowledge) {
		Set<Identifier> pageIds = collectPageIds(chapters);
		int unreadByPages = pageIds.isEmpty() ? 0 : BookReadTracker.countUnread(playerId, pageIds);
		int unreadByKnowledge = knowledge != null
				? BookReadTracker.countUnread(playerId, knowledge, book.getEntryPrefix())
				: 0;
		return Math.max(unreadByPages, unreadByKnowledge);
	}

	private int countUnreadForChapter(UUID playerId, ChapterTemplate chapter, @Nullable IBookKnowledge knowledge) {
		Set<Identifier> pageIds = collectPageIds(List.of(chapter));
		int unreadByPages = pageIds.isEmpty() ? 0 : BookReadTracker.countUnread(playerId, pageIds);
		String chapterPrefix = buildChapterPrefix(chapter);
		int unreadByKnowledge = (chapterPrefix == null || knowledge == null)
				? 0
				: BookReadTracker.countUnread(playerId, knowledge, chapterPrefix);
		return Math.max(unreadByPages, unreadByKnowledge);
	}

	private static Set<Identifier> collectPageIds(List<ChapterTemplate> sourceChapters) {
		Set<Identifier> ids = new HashSet<>();
		for (ChapterTemplate chapter : sourceChapters) {
			for (var page : chapter.getPages()) {
				if (page.getId() != null) {
					ids.add(page.getId());
				}
			}
		}
		return ids;
	}
}
