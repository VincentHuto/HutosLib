package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow;
import com.vincenthuto.hutoslib.client.screen.HLButtonArrow.ArrowDirection;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.IPageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class HLGuiGuidePage extends Screen {
	protected int left;
	protected int top;
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown = 0;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	public int pageNum, guiHeight = 228, guiWidth = 174;
	HLButtonArrow arrowF, arrowB;
	HLButtonTextured buttonTitle, buttonCloseTab;

	protected Minecraft mc = Minecraft.getInstance();
	BookDataTemplate pageTemplate;
	public BookCodeModel book;
	public ChapterTemplate chapter;

	public HLGuiGuidePage(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		super(Component.literal(""));
		this.pageNum = pageNum;
		this.book = book;
		this.chapter = chapter;
		this.pageTemplate = chapter.getPages().get(pageNum);

	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
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

		this.addRenderableWidget(buttonTitle = new HLButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0, (press) -> {
					mc.setScreen(new HLGuiGuideTitlePage(book));
				}));

		this.addRenderableWidget(buttonCloseTab = new HLButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 192 - 16, 24, 16, 24, 32, (press) -> {
					this.onClose();
				}));
		super.init();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		
		PoseStack matrixStack = graphics.pose();
		this.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ((IPageTemplate) pageTemplate).getTextureLocation());

		HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (pageNum + 1)), left + guiWidth - 26,
				top + guiHeight - 15, 50, 0xffffff, true);

		matrixStack.pushPose();
		graphics.renderFakeItem(((IPageTemplate) pageTemplate).getIconItem(), left + guiWidth - 32,
				top + guiHeight - 220);
		matrixStack.popPose();
		if (!((IPageTemplate) pageTemplate).getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((IPageTemplate) pageTemplate).getTitle())), left - guiWidth + 180,
					top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!((IPageTemplate) pageTemplate).getSubtitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((IPageTemplate) pageTemplate).getSubtitle())), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}

		if (!((IPageTemplate) pageTemplate).getText().isEmpty()
				&& ((IPageTemplate) pageTemplate).getSubtitle().isEmpty()
				&& ((IPageTemplate) pageTemplate).getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((IPageTemplate) pageTemplate).getText())), left - guiWidth + 180,
					top + guiHeight - 220, 160, 0xffffff, true);
		} else if (!((IPageTemplate) pageTemplate).getText().isEmpty()
				&& ((IPageTemplate) pageTemplate).getSubtitle().isEmpty()
				|| ((IPageTemplate) pageTemplate).getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((IPageTemplate) pageTemplate).getText())), left - guiWidth + 180,
					top + guiHeight - 200, 160, 0xffffff, true);
		} else if (!((IPageTemplate) pageTemplate).getText().isEmpty()
				&& !((IPageTemplate) pageTemplate).getSubtitle().isEmpty()
				&& !((IPageTemplate) pageTemplate).getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((IPageTemplate) pageTemplate).getText())), left - guiWidth + 180,
					top + guiHeight - 190, 160, 0xffffff, true);
		}

		if (pageNum != (chapter.getPages().size() - 1)) {
			arrowF.render(graphics, mouseX, mouseY, partialTicks);
		}

		if (pageNum >= 0) {
			arrowB.render(graphics, mouseX, mouseY, partialTicks);
		}

		buttonTitle.render(graphics, mouseX, mouseY, partialTicks);

		buttonCloseTab.render(graphics, mouseX, mouseY, partialTicks);

		if ((mouseX >= left + guiWidth - 32 && mouseX <= left + guiWidth - 10)) {
			if (mouseY >= top + guiHeight - 220 && mouseY <= top + guiHeight - 200) {
				List<Component> text = new ArrayList<>();
				if (!((IPageTemplate) pageTemplate).getIconItem().isEmpty()) {
					text.add(Component.literal(
							I18n.get(((IPageTemplate) pageTemplate).getIconItem().getHoverName().getString())));
					graphics.renderComponentTooltip(font, text, left + guiWidth - 32, top + guiHeight - 220);
				}
			}
		}
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
	}

	@Override
	public void renderBackground(GuiGraphics graphics) {
		super.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		graphics.blit(((IPageTemplate) pageTemplate).getTextureLocation(), centerX, centerY, 0, 0, this.guiWidth,
				this.guiHeight);
	}

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

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
