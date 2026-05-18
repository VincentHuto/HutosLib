package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.HLTextUtils;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.book.BookTheme;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class HLGuiGuideTitlePage extends Screen {
	private static final Identifier FALLBACK_BACKGROUND = HutosLib.rloc("textures/gui/guide/title.png");
	private static final int COVER_WIDTH = 186;
	private static final int COVER_HEIGHT = 240;
	private static final int TEXT_LEFT = 10;
	private static final int TITLE_TOP = 10;
	private static final int UNREAD_TOP = 20;
	private static final int TEXT_WIDTH = 165;
	private final BookCodeModel book;
	@Nullable
	private final BookReadTracker tracker;
	@Nullable
	private final UUID viewerUuid;
	@Nullable
	private final IBookKnowledge knowledge;
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
		this.tracker = tracker;
		this.viewerUuid = viewerUuid;
		this.knowledge = knowledge;
		this.refresher = refresher;
	}

	@Override
	protected void init() {
		this.left = (this.width - COVER_WIDTH) / 2;
		this.top = (this.height - COVER_HEIGHT) / 2;
		this.clearWidgets();
		List<ChapterTemplate> chapters = sortedChapters();
		for (int i = 0; i < chapters.size(); i++) {
			ChapterTemplate chapter = chapters.get(i);
			int x = this.left + 176 + (i % 2 == 0 ? 0 : 3);
			int y = this.top + 18 + i * 25;
			HLTomeCategoryTab tab = new HLTomeCategoryTab(chapter.getChapterRGB(),
					HLTextUtils.toProperCase(safe(chapter.getTitle())), i, x, y, 0, 192,
					button -> Minecraft.getInstance().setScreen(new HLGuiGuidePageTOC(this.book, chapter,
							this.tracker, resolveViewerUuid(), resolveKnowledge())));
			this.addRenderableWidget(tab);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundOf(this.book), this.left, this.top, 0, 0,
				COVER_WIDTH, COVER_HEIGHT, 256, 256);
		BookTemplate template = this.book.getTemplate();
		if (template != null && template.getOverlayLoc() != null && !template.getOverlayLoc().isBlank()) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, overlayOf(this.book), this.left, this.top, 0, 0,
					COVER_WIDTH, COVER_HEIGHT, 256, 256);
		}
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		int accentColor = 0xFF000000 | resolveAccentColor();
		if (!this.title.getString().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(graphics, this.font, this.title, this.left + TEXT_LEFT,
					this.top + TITLE_TOP, TEXT_WIDTH, accentColor, true);
		}
		UUID playerId = resolveViewerUuid();
		IBookKnowledge currentKnowledge = resolveKnowledge();
		if (playerId != null) {
			int unread = countUnreadForBook(playerId, currentKnowledge);
			if (unread > 0) {
				HLGuiUtils.drawMaxWidthString(graphics, this.font, Component.literal(unread + " new"),
						this.left + TEXT_LEFT, this.top + UNREAD_TOP, TEXT_WIDTH, accentColor, true);
			}
		}
	}

	public void refresh() {
		if (this.refresher != null) {
			Minecraft.getInstance().setScreen(new HLGuiGuideTitlePage(this.refresher.get(), this.tracker,
					this.viewerUuid, this.knowledge, this.refresher));
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

	private static Identifier overlayOf(BookCodeModel book) {
		if (book != null && book.getTheme() != null && book.getTheme().backgroundTexture() != null) {
			return book.getTheme().backgroundTexture();
		}
		return book != null && book.getTemplate() != null ? book.getTemplate().getOverlayImage() : FALLBACK_BACKGROUND;
	}

	private static String safe(String value) {
		return value == null || value.isBlank() ? "Untitled" : value;
	}

	private List<ChapterTemplate> sortedChapters() {
		List<ChapterTemplate> chapters = this.book != null && this.book.getChapters() != null
				? new ArrayList<>(this.book.getChapters())
				: new ArrayList<>();
		chapters.sort(Comparator.comparingInt(ChapterTemplate::getOrdinality));
		return chapters;
	}

	private int resolveAccentColor() {
		BookTheme theme = this.book != null ? this.book.getTheme() : null;
		if (theme != null && theme.accentColor() != 0) {
			return theme.accentColor();
		}
		return BookTheme.DEFAULT_ACCENT;
	}

	@Nullable
	private UUID resolveViewerUuid() {
		if (this.viewerUuid != null) {
			return this.viewerUuid;
		}
		return Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null;
	}

	@Nullable
	private IBookKnowledge resolveKnowledge() {
		if (this.knowledge != null) {
			return this.knowledge;
		}
		return Minecraft.getInstance().player != null ? BookKnowledgeProvider.get(Minecraft.getInstance().player) : null;
	}

	private int countUnreadForBook(UUID playerId, @Nullable IBookKnowledge currentKnowledge) {
		List<ChapterTemplate> chapters = sortedChapters();
		Set<Identifier> pageIds = collectPageIds(chapters);
		int unreadByPages = pageIds.isEmpty() ? 0 : BookReadTracker.countUnread(playerId, pageIds);
		int unreadByKnowledge = currentKnowledge != null && this.book != null
				? BookReadTracker.countUnread(playerId, currentKnowledge, this.book.getEntryPrefix())
				: 0;
		return Math.max(unreadByPages, unreadByKnowledge);
	}

	@Nullable
	private String buildChapterPrefix(ChapterTemplate chapter) {
		if (chapter.getId() == null) {
			return null;
		}
		String path = chapter.getId().getPath();
		int first = path.indexOf('/');
		if (first < 0) {
			return null;
		}
		int second = path.indexOf('/', first + 1);
		if (second < 0) {
			return null;
		}
		return path.substring(0, second + 1);
	}

	private int countUnreadForChapter(UUID playerId, ChapterTemplate chapter, @Nullable IBookKnowledge currentKnowledge) {
		Set<Identifier> pageIds = collectPageIds(List.of(chapter));
		int unreadByPages = pageIds.isEmpty() ? 0 : BookReadTracker.countUnread(playerId, pageIds);
		String chapterPrefix = buildChapterPrefix(chapter);
		int unreadByKnowledge = chapterPrefix == null || currentKnowledge == null
				? 0
				: BookReadTracker.countUnread(playerId, currentKnowledge, chapterPrefix);
		return Math.max(unreadByPages, unreadByKnowledge);
	}

	private static Set<Identifier> collectPageIds(List<ChapterTemplate> chapters) {
		Set<Identifier> ids = new HashSet<>();
		for (ChapterTemplate chapter : chapters) {
			if (chapter.getPages() == null) {
				continue;
			}
			for (BookDataTemplate page : chapter.getPages()) {
				if (page.getId() != null) {
					ids.add(page.getId());
				}
			}
		}
		return ids;
	}
}
