package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.guide.GuiButtonBookArrow.ArrowDirection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuidePage extends Screen {
	final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");
	int left, top;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	public int pageNum, guiHeight = 228, guiWidth = 174;
	static Component titleComponent =  Component.literal("");
	String title, subtitle, text;
	ItemStack icon;
	GuiButtonBookArrow arrowF, arrowB;
	GuiButtonTextured buttonTitle, buttonCloseTab;
	EditBox textBox;
	String catagory;
	Minecraft mc = Minecraft.getInstance();

	public GuiGuidePage(String catagoryIn) {
		this(0, catagoryIn, "", "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(String catagoryIn, String titleIn) {
		this(0, catagoryIn, titleIn, "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(String catagoryIn, String titleIn, String subtitleIn) {
		this(0, catagoryIn, titleIn, subtitleIn, ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn) {
		this(pageNumIn, catagoryIn, "", "", ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String textIn) {
		this(pageNumIn, catagoryIn, "", "", ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String titleIn, ItemStack iconIn) {
		this(pageNumIn, catagoryIn, titleIn, "", iconIn, "");
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn) {
		this(pageNumIn, catagoryIn, titleIn, subtitleIn, ItemStack.EMPTY, "");
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, ItemStack iconIn) {
		this(pageNumIn, catagoryIn, titleIn, subtitleIn, iconIn, "");
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, String textIn) {
		this(pageNumIn, catagoryIn, titleIn, subtitleIn, ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(String catagoryIn, String titleIn, String subtitleIn, String textIn) {
		this(0, catagoryIn, titleIn, subtitleIn, ItemStack.EMPTY, textIn);
	}

	public GuiGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, ItemStack iconIn,
			String textIn) {
		super(titleComponent);
		this.title = titleIn;
		this.subtitle = subtitleIn;
		this.icon = iconIn;
		this.text = textIn;
		this.pageNum = pageNumIn;
		this.catagory = catagoryIn;
	}

	@Override
	public void renderBackground(PoseStack matrixStack, int p_96560_) {
		super.renderBackground(matrixStack, p_96560_);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.blit(matrixStack, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		if (pageNum != 0) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal("Pg." + pageNum), left + guiWidth - 26,
					top + guiHeight - 15, 50, 0xffffff, true);
		}
		matrixStack.pushPose();
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, left + guiWidth - 32,
				top + guiHeight - 220);
		matrixStack.popPose();
		if (!title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(I18n.get(title)), left - guiWidth + 180,
					top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!subtitle.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(I18n.get(subtitle)), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}

		if (!text.isEmpty() && subtitle.isEmpty() && title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(I18n.get(text)), left - guiWidth + 180,
					top + guiHeight - 220, 160, 0xffffff, true);
		} else if (!text.isEmpty() && subtitle.isEmpty() || title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(I18n.get(text)), left - guiWidth + 180,
					top + guiHeight - 200, 160, 0xffffff, true);
		} else if (!text.isEmpty() && !subtitle.isEmpty() && !title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(I18n.get(text)), left - guiWidth + 180,
					top + guiHeight - 190, 160, 0xffffff, true);
		}

		if (pageNum != (getPages().size() - 1)) {
			arrowF.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}

		if (pageNum >= 0) {
			arrowB.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}

		// buttonTitle.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		buttonCloseTab.renderButton(matrixStack, mouseX, mouseY, partialTicks);

		textBox.render(matrixStack, mouseX, mouseY, partialTicks);
		if ((mouseX >= left + guiWidth - 32 && mouseX <= left + guiWidth - 10)) {
			if (mouseY >= top + guiHeight - 220 && mouseY <= top + guiHeight - 200) {
				List<Component> text = new ArrayList<Component>();
				if (!icon.isEmpty()) {
					text.add( Component.literal(I18n.get(icon.getHoverName().getString())));
					renderComponentTooltip(matrixStack, text, left + guiWidth - 32, top + guiHeight - 220);
				}
			}
		}
//		List<Component> titlePage = new ArrayList<Component>();
//		titlePage.add( Component.literal(I18n.get("Title")));
//		titlePage.add( Component.literal(I18n.get("Return to Catagories")));
//		if (buttonTitle.isHovered()) {
//			renderComponentTooltip(matrixStack, titlePage, mouseX, mouseY);
//		}
		List<Component> ClosePage = new ArrayList<Component>();
		ClosePage.add( Component.literal(I18n.get("Close Book")));
		if (buttonCloseTab.isHoveredOrFocused()) {
			renderComponentTooltip(matrixStack, ClosePage, mouseX, mouseY);
		}
	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		this.clearWidgets();
		if (pageNum != (getPages().size() - 1)) {
			this.addRenderableWidget(arrowF = new GuiButtonBookArrow(ArrowDirection.FORWARD, ARROWF,
					left + guiWidth - 18, top + guiHeight - 7, (press) -> {
						if (pageNum != (getPages().size() - 1)) {
							mc.setScreen(getPages().get((pageNum + 1)));
						} else {
							mc.setScreen(getPages().get((pageNum)));
						}
					}));
		}
		this.addRenderableWidget(
				arrowB = new GuiButtonBookArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {
					if (pageNum > 0) {
						mc.setScreen(getPages().get((pageNum - 1)));
					} else {
						mc.setScreen(getOwnerTome().getTitle());
					}
				}));

//		this.addRenderableWidget(buttonTitle = new GuiButtonTextured(LocationHelper.guiPrefix("book_tabs.png"),
//				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210, 24, 16, 24, 0, (press) -> {
//					mc.setScreen(getOwnerTome().getTitle());
//
//				}));
		this.addRenderableWidget(buttonCloseTab = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 210, 24, 16, 24, 32, (press) -> {
					this.onClose();
				}));
		textBox = new EditBox(font, left - guiWidth + 155, top + guiHeight - 227, 14, 14,  Component.literal(""));
		super.init();
	}

	public void updateTextBoxes() {
		if (!textBox.getValue().isEmpty()) {
			if (NumberUtils.isCreatable(textBox.getValue())) {
				int searchNum = (Integer.parseInt(textBox.getValue()));
				if (searchNum < getPages().size()) {
					mc.setScreen(getPages().get(searchNum));
				} else if (searchNum >= getPages().size()) {
					mc.setScreen(getPages().get(getPages().size() - 1));
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

	public String getCatagory() {
		return catagory;
	}

	public abstract TomeLib getOwnerTome();

	public List<GuiGuidePage> getPages() {
		return getOwnerTome().getMatchingChapters(getCatagory()).pages;

	}
}
