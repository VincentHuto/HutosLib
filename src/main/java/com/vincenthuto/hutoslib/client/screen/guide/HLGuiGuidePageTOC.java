package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow.ArrowDirection;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HLGuiGuidePageTOC extends Screen {
	HLButtonTextured buttonTOC;
	protected final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown = 0;
	protected int left;
	protected int top;
	public int guiHeight = 228, guiWidth = 174;
	HLButtonTextured buttonTitle, buttonCloseTab;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	HLButtonArrow arrowF, arrowB;
	public List<HLButtonTextured> pageButtons = new ArrayList<>();
	private ChapterTemplate chapterTemplate;
	protected Minecraft mc = Minecraft.getInstance();
	private BookCodeModel book;

	public HLGuiGuidePageTOC(BookCodeModel book, ChapterTemplate chapterTemplate) {
		super(Component.literal(chapterTemplate.getTitle()));
		this.chapterTemplate = chapterTemplate;
		this.book = book;

	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		int sideLoc = left + guiWidth;
		int verticalLoc = top + guiHeight;
		this.clearWidgets();
		pageButtons.clear();
		super.init();
		Collections.sort(pageButtons, (obj1, obj2) -> Integer.compare(obj1.getId(), obj2.getId()));

		for (int i = 0; i < chapterTemplate.getPages().size(); i++) {
			pageButtons.add(new HLButtonTextured(chapterTemplate.getTextureLocation(), i, sideLoc - (guiWidth - 5),
					(verticalLoc - 210) + ((i) * 15), 163, 14, 5, 228, (press) -> {
						if (press instanceof HLButtonTextured button) {
							chapterTemplate.getPages().get(button.getId()).getPageScreen(button.getId(), book,
									chapterTemplate);
						}
					}));
		}

		for (HLButtonTextured pageButton : pageButtons) {
			this.addRenderableWidget(pageButton);
		}
		this.addRenderableWidget(arrowF = new HLButtonArrow(ArrowDirection.FORWARD, ARROWF, left + guiWidth - 18,
				top + guiHeight - 7, (press) -> {
					chapterTemplate.getPages().get(0).getPageScreen(0, book, chapterTemplate);
				}));

		this.addRenderableWidget(
				arrowB = new HLButtonArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {
					book.getTemplate().getPageScreen(0, book, null);
				}));

		this.addRenderableWidget(buttonTitle = new HLButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"), TITLEBUTTON,
				left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0, (press) -> {
					book.getTemplate().getPageScreen(0, book, null);
				}));

		this.addRenderableWidget(buttonCloseTab = new HLButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 192 - 16, 24, 16, 24, 32, (press) -> {
					this.onClose();
				}));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, chapterTemplate.getTextureLocation());
		Collections.sort(pageButtons, (obj1, obj2) -> Integer.compare(obj1.getId(), obj2.getId()));

		for (int i = 0; i < pageButtons.size(); i++) {
			pageButtons.get(i).render(graphics, mouseX, mouseY, partialTicks);
			HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (i + 1)), pageButtons.get(i).posX + 5,
					pageButtons.get(i).posY + 2, 150, 0xffffff, true);
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(((PageTemplate) chapterTemplate.getPages().get(i)).getTitle()),
					pageButtons.get(i).posX + 30, pageButtons.get(i).posY + 2, 150, 0xffffff, true);
		}
		buttonTitle.render(graphics, mouseX, mouseY, partialTicks);

		buttonCloseTab.render(graphics, mouseX, mouseY, partialTicks);
		List<Component> titlePage = new ArrayList<Component>();
		titlePage.add(Component.literal(I18n.get("Title")));
		titlePage.add(Component.literal(I18n.get("Return to Catagories")));
		if (buttonTitle.isHovered()) {
			graphics.renderComponentTooltip(font, titlePage, mouseX, mouseY);
		}
		List<Component> ClosePage = new ArrayList<>();
		ClosePage.add(Component.literal(I18n.get("Close Book")));
		if (buttonCloseTab.isHoveredOrFocused()) {
			graphics.renderComponentTooltip(font, ClosePage, mouseX, mouseY);
		}
		arrowF.render(graphics, mouseX, mouseY, partialTicks);
		arrowB.render(graphics, mouseX, mouseY, partialTicks);

	}

	@Override
	public void renderBackground(GuiGraphics graphics) {
		super.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		graphics.blit(chapterTemplate.getTextureLocation(), centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

	public static void openScreenViaItem(int pNum, BookCodeModel pBook, ChapterTemplate pChapterTemplate) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new HLGuiGuidePageTOC(pBook, pChapterTemplate));
	}
}
