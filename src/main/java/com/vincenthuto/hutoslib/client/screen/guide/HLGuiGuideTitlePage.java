package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLTextUtils;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HLGuiGuideTitlePage extends Screen {

	private static HLGuiGuideTitlePage screen;
	final ResourceLocation texture;
	final ResourceLocation overlay;
	Minecraft mc = Minecraft.getInstance();
	int guiWidth = 186;
	int guiHeight = 240;
	double xDragPos = 0;
	double yDragPos = 0;
	public double dragLeftRight = 0;
	public double dragUpDown = 0;
	int left, top;
	final int BUTTONCLOSE = 30;
	Component titleComponent;
	Component subtitleComponent;

	public ItemStack icon;
	HLButtonTextured buttonclose;
	public List<ChapterTemplate> chapters = new ArrayList<>();
	public List<HLButtonTextured> buttonList = new ArrayList<>();
	private BookCodeModel book;

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

	public HLGuiGuideTitlePage(BookCodeModel book) {
		super(Component.translatable(""));
		this.book = book;
		this.icon = book.getTemplate().getIconItem();
		this.titleComponent = Component.literal(book.getTemplate().getTitle());
		this.chapters = book.getChapters();
		this.texture = book.getTemplate().getCoverImage();
		this.overlay = book.getTemplate().getOverlayImage();

	}

	public void setBook(BookCodeModel book) {
		this.book = book;
	}

	public BookCodeModel getBook() {
		return book;
	}

	@Override
	public void init() {
		Random rand = new Random();
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.buttonList.clear();
		this.clearWidgets();
		this.addRenderableWidget(
				buttonclose = new HLButtonTextured(overlay, BUTTONCLOSE, (int) (centerX + (guiWidth * 0.05f)),
						(int) (centerY + (guiHeight * 0.78f)), 32, 32, 209, 32, (press) -> {
							onClose();
						}));

		Collections.sort(chapters, (obj1, obj2) -> Integer.compare(obj1.getOrdinality(), obj2.getOrdinality()));

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

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		this.renderBackground(graphics);
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		Collections.sort(chapters, (obj1, obj2) -> Integer.compare(obj1.getOrdinality(), obj2.getOrdinality()));

		graphics.blit(texture, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
		graphics.blit(overlay, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
		title.getContents();
		if (title.getContents() != ComponentContents.EMPTY) {
			HLGuiUtils.drawMaxWidthString(font, title, centerX + 10, centerY + 10, 165, 0xffffff, true);
		}

		matrixStack.pushPose();
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		graphics.renderFakeItem(icon, left + guiWidth - 48, top + guiHeight - 230);
		matrixStack.popPose();

		for (HLButtonTextured element : buttonList) {
			if (element instanceof HLTomeCategoryTab tab) {
				RenderSystem.setShaderColor(tab.color.getRed() / 255, tab.color.getGreen() / 255,
						tab.color.getBlue() / 255, 1.0F);
				element.render(graphics, mouseX, mouseY, partialTicks);
				RenderSystem.setShaderColor(1, 1, 1, 1.0F);

			} else {
				element.render(graphics, mouseX, mouseY, partialTicks);

			}

			if (element.isHoveredOrFocused()) {
				graphics.renderTooltip(font, element.text, element.getX(), element.getY());
			}

		}

		this.buttonclose.render(graphics, mouseX, mouseY, partialTicks);
		if (this.buttonclose.isHoveredOrFocused()) {
			graphics.renderTooltip(font, Component.translatable("Close"), this.buttonclose.getX(),
					this.buttonclose.getY());
		}
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
		mc.setScreen(new HLGuiGuideTitlePage(pBook));
	}
}
