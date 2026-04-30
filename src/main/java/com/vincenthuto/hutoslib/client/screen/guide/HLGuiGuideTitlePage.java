package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HLGuiGuideTitlePage extends Screen {

	private static HLGuiGuideTitlePage screen;
	final ResourceLocation texture;
	final ResourceLocation overlay;
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

		ResourceLocation overlayTex = resolveOverlayTexture();
		this.addRenderableWidget(
				buttonclose = new HLButtonTextured(overlayTex, BUTTONCLOSE,
						(int) (centerX + (guiWidth * 0.05f)),
						(int) (centerY + (guiHeight * 0.78f)), 32, 32, 209, 32,
						(press) -> onClose()));

		chapters.sort(Comparator.comparingInt(ChapterTemplate::getOrdinality));

		for (int i = 0; i < chapters.size(); i++) {
			HLTomeCategoryTab tab = new HLTomeCategoryTab(chapters.get(i).getChapterRGB(),
					HLTextUtils.toProperCase(chapters.get(i).getTitle()), i,
					(int) (centerX + (guiWidth * 0.05f) + 167 + (rand.nextInt(6) - rand.nextInt(4))),
					centerY - (i * -25) + 18, 0, 192, (press) -> {
						if (press instanceof HLButtonTextured button) {
							chapters.get(button.id).getPageScreen(0, book, chapters.get(button.id));
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
		net.minecraft.world.entity.player.Player localPlayer = Minecraft.getInstance().player;
		UUID resolvedUuid = viewerUuid != null ? viewerUuid
				: (localPlayer != null ? localPlayer.getUUID() : null);
		IBookKnowledge resolvedKnowledge = knowledge != null ? knowledge
				: (localPlayer != null ? BookKnowledgeProvider.get(localPlayer) : null);

		// Unread count badge
		if (resolvedUuid != null && resolvedKnowledge != null) {
			int unread = BookReadTracker.countUnread(resolvedUuid, resolvedKnowledge, book.getEntryPrefix());
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
				if (resolvedUuid != null && resolvedKnowledge != null
						&& tab.id >= 0 && tab.id < chapters.size()) {
					String chapterPrefix = buildChapterPrefix(chapters.get(tab.id));
					if (chapterPrefix != null
							&& BookReadTracker.countUnread(resolvedUuid, resolvedKnowledge, chapterPrefix) > 0) {
						graphics.fill(tab.posX + tab.width - 7, tab.posY + 2,
								tab.posX + tab.width - 3, tab.posY + 6, 0xFF000000 | resolveAccentColor());
					}
				}
			} else {
				element.render(graphics, mouseX, mouseY, partialTicks);
			}

			if (element.isHoveredOrFocused()) {
				graphics.renderTooltip(font, element.text, element.getX(), element.getY());
			}
		}

		this.buttonclose.render(graphics, mouseX, mouseY, partialTicks);
		if (this.buttonclose.isHoveredOrFocused()) {
			graphics.renderTooltip(font, Component.literal("Close"),
					this.buttonclose.getX(), this.buttonclose.getY());
		}
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

	private ResourceLocation resolveOverlayTexture() {
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

	/**
	 * Derives a {@link BookReadTracker} prefix string for the given chapter.
	 *
	 * <p>Chapter resource IDs have the form {@code namespace:book/chapter/file}
	 * (three path segments). The logical chapter prefix used by knowledge entries
	 * is the first two segments — {@code book/chapter/} — NOT the full path. Using
	 * the full path ({@code book/chapter/file/}) would never match any knowledge
	 * entry because entries don't nest under the chapter JSON file name.
	 *
	 * <p>Returns {@code null} if the chapter has no assigned ID yet.
	 */
	@Nullable
	private String buildChapterPrefix(ChapterTemplate chapter) {
		if (chapter.getId() == null) {
			return null;
		}
		String path = chapter.getId().getPath();
		String[] parts = path.split("/");
		if (parts.length >= 2) {
			return parts[0] + "/" + parts[1] + "/";
		}
		return path + "/";
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
}
