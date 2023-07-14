package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.guide.GuiButtonBookArrow.ArrowDirection;
import com.vincenthuto.hutoslib.common.data.DataTemplate;
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

public class TestGuiGuidePage extends Screen {
	protected final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");
	protected int left;
	protected int top;
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown = 0;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	public int pageNum, guiHeight = 228, guiWidth = 174;
	GuiButtonBookArrow arrowF, arrowB;
	GuiButtonTextured buttonTitle, buttonCloseTab;

	protected Minecraft mc = Minecraft.getInstance();
	DataTemplate pageTemplate;
	private BookCodeModel book;
	private BookChapterTemplate chapter;

	public TestGuiGuidePage(int pageNum, BookCodeModel book, BookChapterTemplate chapter) {
		super(Component.literal(((BookPageTemplate) chapter.getPages().get(pageNum)).getTitle()));
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
			this.addRenderableWidget(arrowF = new GuiButtonBookArrow(ArrowDirection.FORWARD, ARROWF,
					left + guiWidth - 18, top + guiHeight - 7, (press) -> {
						if (pageNum != (chapter.getPages().size() - 1)) {

							mc.setScreen(new TestGuiGuidePage(pageNum + 1, book, chapter));

						} else {
							mc.setScreen(new TestGuiGuidePage(pageNum, book, chapter));

						}
					}));
		}
		this.addRenderableWidget(
				arrowB = new GuiButtonBookArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {

					if (pageNum > 0) {
						mc.setScreen(new TestGuiGuidePage(pageNum - 1, book, chapter));
					} else {
						mc.setScreen(new TestGuiGuidePageTOC(book, chapter));
					}
				}));

		this.addRenderableWidget(buttonTitle = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210 - 16, 24, 16, 24, 0, (press) -> {
					mc.setScreen(new TestGuiGuideTitlePage(book));
				}));

		this.addRenderableWidget(buttonCloseTab = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
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
		RenderSystem.setShaderTexture(0, texture);

		HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (pageNum + 1)), left + guiWidth - 26,
				top + guiHeight - 15, 50, 0xffffff, true);
		
		pageTemplate.renderInGui(graphics, font, left, top, guiWidth, guiHeight, mouseX, mouseY, dragUpDown, dragLeftRight, partialTicks);
		//System.out.println(pageTemplate.getClass());
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
				if (!((BookPageTemplate)pageTemplate).getIconItem().isEmpty()) {
					text.add(Component.literal(I18n.get(((BookPageTemplate)pageTemplate).getIconItem().getHoverName().getString())));
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
		graphics.blit(((BookPageTemplate)pageTemplate).getTextureLocation(), centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
	}
	
	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

}
