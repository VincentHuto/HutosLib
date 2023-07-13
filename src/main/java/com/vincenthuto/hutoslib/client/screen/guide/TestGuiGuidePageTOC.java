package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.guide.GuiButtonBookArrow.ArrowDirection;
import com.vincenthuto.hutoslib.common.data.book.BookChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookPageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TestGuiGuidePageTOC extends Screen {
	GuiButtonTextured buttonTOC;
	protected final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");

	protected int left;
	protected int top;
	public int guiHeight = 228, guiWidth = 174;
	GuiButtonTextured buttonTitle, buttonCloseTab;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	GuiButtonBookArrow arrowF, arrowB;
	public List<GuiButtonTextured> pageButtons = new ArrayList<>();
	private BookChapterTemplate chapterTemplate;
	protected Minecraft mc = Minecraft.getInstance();
	private BookCodeModel book;

	public TestGuiGuidePageTOC(BookCodeModel book, BookChapterTemplate chapterTemplate) {
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
			pageButtons.add(new GuiButtonTextured(chapterTemplate.getTextureLocation(), i, sideLoc - (guiWidth - 5),
					(verticalLoc - 210) + ((i) * 15), 163, 14, 5, 228, (press) -> {
						if (press instanceof GuiButtonTextured button) {
							mc.setScreen(new TestGuiGuidePage(button.getId(), book, chapterTemplate));
						}
					}));
		}

		for (GuiButtonTextured pageButton : pageButtons) {
			this.addRenderableWidget(pageButton);
		}
		this.addRenderableWidget(arrowF = new GuiButtonBookArrow(ArrowDirection.FORWARD, ARROWF, left + guiWidth - 18,
				top + guiHeight - 7, (press) -> {
					mc.setScreen(new TestGuiGuidePage(0, book, chapterTemplate));
				}));

		this.addRenderableWidget(
				arrowB = new GuiButtonBookArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {
					mc.setScreen(new TestGuiGuideTitlePage(book));
				}));

		this.addRenderableWidget(buttonTitle = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0, (press) -> {
					mc.setScreen(new TestGuiGuideTitlePage(book));
				}));

		this.addRenderableWidget(buttonCloseTab = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
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
			HLGuiUtils.drawMaxWidthString(font, Component.literal(((BookPageTemplate)chapterTemplate.getPages().get(i)).getTitle()),
					pageButtons.get(i).posX + 30, pageButtons.get(i).posY + 2, 150, 0xffffff, true);
		}
//		for (BookPageTemplate p : chapterTemplate.getPages()) {
//			System.out.println(p.getClass());
//
//		}
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

}
