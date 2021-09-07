package com.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

import com.hutoslib.client.LocationHelper;
import com.hutoslib.client.screen.GuiButtonTextured;
import com.hutoslib.client.screen.GuiUtils;
import com.hutoslib.client.screen.guide.GuiButtonBookArrow.ArrowDirection;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuidePage extends Screen {
	final ResourceLocation texture = LocationHelper.guiPrefix("page.png");
	int left, top;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	int pageNum, guiHeight = 228, guiWidth = 174;
	static TextComponent titleComponent = new TextComponent("");
	String title, subtitle, text;
	ItemStack icon;
	GuiButtonBookArrow arrowF, arrowB;
	GuiButtonTextured buttonTitle, buttonCloseTab;
	EditBox textBox;
	ITomeCatagory catagory;
	Minecraft mc = Minecraft.getInstance();
	List<GuiGuidePage> chapterPages;

	public GuiGuidePage(ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages) {
		this(0, catagoryIn, chapterPages, "", "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn) {
		this(0, catagoryIn, chapterPages, titleIn, "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn, String subtitleIn) {
		this(0, catagoryIn, chapterPages, titleIn, subtitleIn, ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages) {
		this(pageNumIn, catagoryIn, chapterPages, "", "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String textIn) {
		this(pageNumIn, catagoryIn, chapterPages, "", "", ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, ItemStack iconIn,
			String titleIn) {
		this(pageNumIn, catagoryIn, chapterPages, titleIn, "", iconIn, "");
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn,
			String subtitleIn) {
		this(pageNumIn, catagoryIn, chapterPages, titleIn, subtitleIn, ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn,
			String subtitleIn, String textIn) {
		this(pageNumIn, catagoryIn, chapterPages, titleIn, subtitleIn, ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn, String subtitleIn,
			String textIn) {
		this(0, catagoryIn, chapterPages, titleIn, subtitleIn, ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(int pageNumIn, ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, String titleIn,
			String subtitleIn, ItemStack iconIn, String textIn) {
		super(titleComponent);
		this.title = titleIn;
		this.subtitle = subtitleIn;
		this.icon = iconIn;
		this.text = textIn;
		this.pageNum = pageNumIn;
		this.catagory = catagoryIn;
		this.chapterPages = chapterPages;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.blit(matrixStack, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);

		if (pageNum != 0) {
			drawString(matrixStack, font, "Pg." + pageNum, 90, 0, 0000000);
		}
		matrixStack.pushPose();
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, left + guiWidth - 32,
				top + guiHeight - 220);
		matrixStack.popPose();
		if (!title.isEmpty()) {
			GuiUtils.drawMaxWidthString(font, new TextComponent(I18n.get(title)), left - guiWidth + 180,
					top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!subtitle.isEmpty()) {
			GuiUtils.drawMaxWidthString(font, new TextComponent(I18n.get(subtitle)), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}
		if (!text.isEmpty()) {
			GuiUtils.drawMaxWidthString(font, new TextComponent(I18n.get(text)), left - guiWidth + 180,
					top + guiHeight - 190, 165, 0xffffff, true);

		}
		if (pageNum != (chapterPages.size() - 1)) {
			arrowF.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}

		if (pageNum > 0) {
			arrowB.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}

		buttonTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		buttonCloseTab.renderButton(matrixStack, mouseX, mouseY, partialTicks);

		textBox.render(matrixStack, mouseX, mouseY, partialTicks);
		if ((mouseX >= left + guiWidth - 32 && mouseX <= left + guiWidth - 10)) {
			if (mouseY >= top + guiHeight - 220 && mouseY <= top + guiHeight - 200) {
				List<Component> text = new ArrayList<Component>();
				if (!icon.isEmpty()) {
					text.add(new TextComponent(I18n.get(icon.getHoverName().getString())));
					renderComponentTooltip(matrixStack, text, left + guiWidth - 32, top + guiHeight - 220);
				}
			}
		}
		List<Component> titlePage = new ArrayList<Component>();
		titlePage.add(new TextComponent(I18n.get("Title")));
		titlePage.add(new TextComponent(I18n.get("Return to Catagories")));
		if (buttonTitle.isHovered()) {
			renderComponentTooltip(matrixStack, titlePage, mouseX, mouseY);
		}
		List<Component> ClosePage = new ArrayList<Component>();
		ClosePage.add(new TextComponent(I18n.get("Close Book")));
		if (buttonCloseTab.isHovered()) {
			renderComponentTooltip(matrixStack, ClosePage, mouseX, mouseY);
		}
	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		this.clearWidgets();

		if (pageNum != (chapterPages.size() - 1)) {
			this.addRenderableWidget(arrowF = new GuiButtonBookArrow(ArrowDirection.FORWARD, ARROWF,
					left + guiWidth - 18, top + guiHeight - 10, (press) -> {
						if (pageNum != (chapterPages.size() - 1)) {
							mc.setScreen(chapterPages.get((pageNum + 1)));
						} else {
							mc.setScreen(chapterPages.get((pageNum)));

						}
					}));
		}
		if (pageNum != 0) {
			this.addRenderableWidget(arrowB = new GuiButtonBookArrow(ArrowDirection.BACKWARD, ARROWB, left,
					top + guiHeight - 10, (press) -> {
						if (pageNum > 0) {
							mc.setScreen(chapterPages.get((pageNum - 1)));
						} else {
							mc.setScreen(chapterPages.get((0)));

						}
					}));
		}
		this.addRenderableWidget(buttonTitle = new GuiButtonTextured(LocationHelper.guiPrefix("title_tab.png"),
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210, 24, 16, 0, 0, (press) -> {
					mc.setScreen(this.getTitlePage());

				}));
		this.addRenderableWidget(buttonCloseTab = new GuiButtonTextured(LocationHelper.guiPrefix("close_tab.png"),
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 193, 24, 16, 0, 0, (press) -> {
					this.onClose();
				}));
		textBox = new EditBox(font, left - guiWidth + 155, top + guiHeight - 227, 14, 14, new TextComponent(""));
		super.init();
	}

	public void updateTextBoxes() {
		if (!textBox.getValue().isEmpty()) {
			if (NumberUtils.isCreatable(textBox.getValue())) {
				int searchNum = (Integer.parseInt(textBox.getValue()));
				if (searchNum < chapterPages.size()) {
					mc.setScreen(chapterPages.get(searchNum));
				} else if (searchNum >= chapterPages.size()) {
					mc.setScreen(chapterPages.get(chapterPages.size() - 1));
				}
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		textBox.setValue(GLFW.glfwGetKeyName(keyCode, scanCode));
		updateTextBoxes();
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		textBox.mouseClicked(mouseX, mouseY, mouseButton);
		updateTextBoxes();
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public abstract GuiGuideTitlePage getTitlePage();

}
