package com.vincenthuto.hutoslib.common.data.book;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.guide.GuiButtonBookArrow;
import com.vincenthuto.hutoslib.client.screen.guide.GuiButtonBookArrow.ArrowDirection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TestGuiGuidePage extends Screen {
	protected final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");
	protected int left;
	protected int top;
	final int ARROWF = 0, ARROWB = 1, TITLEBUTTON = 2, CLOSEBUTTON = 3;
	public int pageNum, guiHeight = 228, guiWidth = 174;
	GuiButtonBookArrow arrowF, arrowB;
	GuiButtonTextured buttonTitle, buttonCloseTab;
	
	EditBox textBox;
	protected Minecraft mc = Minecraft.getInstance();
	BookPageTemplate pageTemplate;

	public TestGuiGuidePage(BookPageTemplate pageTemplate) {
		super(Component.literal(pageTemplate.title));
		this.pageTemplate = pageTemplate;
	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		this.clearWidgets();
//		if (pageNum != (getPages().size() - 1)) {
//			this.addRenderableWidget(arrowF = new GuiButtonBookArrow(ArrowDirection.FORWARD, ARROWF,
//					left + guiWidth - 18, top + guiHeight - 7, (press) -> {
//						if (pageNum != (getPages().size() - 1)) {
//							mc.setScreen(getPages().get((pageNum + 1)));
//						} else {
//							mc.setScreen(getPages().get((pageNum)));
//						}
//					}));
//		}
//		this.addRenderableWidget(
//				arrowB = new GuiButtonBookArrow(ArrowDirection.BACKWARD, ARROWB, left, top + guiHeight - 7, (press) -> {
//					if (pageNum > 0) {
//						mc.setScreen(getPages().get((pageNum - 1)));
//					} else {
//						mc.setScreen(getOwnerTome().getTitle());
//					}
//				}));

		this.addRenderableWidget(buttonTitle = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				TITLEBUTTON, left - guiWidth + 150, top + guiHeight - 210, 24, 16, 24, 0, (press) -> {
					this.onClose();
				}));
		
		this.addRenderableWidget(buttonCloseTab = new GuiButtonTextured(HLLocHelper.guiPrefix("book_tabs.png"),
				CLOSEBUTTON, left - guiWidth + 150, top + guiHeight - 192, 24, 16, 24, 32, (press) -> {
					this.onClose();
				}));
		textBox = new EditBox(font, left - guiWidth + 155, top + guiHeight - 227, 14, 14, Component.literal(""));
		super.init();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		this.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		if (pageNum != 0) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + pageNum), left + guiWidth - 26,
					top + guiHeight - 15, 50, 0xffffff, true);
		}
		matrixStack.pushPose();
		graphics.renderFakeItem(pageTemplate.getIconItem(), left + guiWidth - 32, top + guiHeight - 220);
		matrixStack.popPose();
		if (!pageTemplate.title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(pageTemplate.title)), left - guiWidth + 180,
					top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!pageTemplate.subtitle.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(pageTemplate.subtitle)), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}

		if (!pageTemplate.text.isEmpty() && pageTemplate.subtitle.isEmpty() && pageTemplate.title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(pageTemplate.text)), left - guiWidth + 180,
					top + guiHeight - 220, 160, 0xffffff, true);
		} else if (!pageTemplate.text.isEmpty() && pageTemplate.subtitle.isEmpty() || pageTemplate.title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(pageTemplate.text)), left - guiWidth + 180,
					top + guiHeight - 200, 160, 0xffffff, true);
		} else if (!pageTemplate.text.isEmpty() && !pageTemplate.subtitle.isEmpty() && !pageTemplate.title.isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(pageTemplate.text)), left - guiWidth + 180,
					top + guiHeight - 190, 160, 0xffffff, true);
		}

//		if (pageNum != (getPages().size() - 1)) {
//			arrowF.render(graphics, mouseX, mouseY, partialTicks);
//		}
//
//		if (pageNum >= 0) {
//			arrowB.render(graphics, mouseX, mouseY, partialTicks);
//		}

		buttonTitle.render(graphics, mouseX, mouseY, partialTicks);

		buttonCloseTab.render(graphics, mouseX, mouseY, partialTicks);

		textBox.render(graphics, mouseX, mouseY, partialTicks);
		if ((mouseX >= left + guiWidth - 32 && mouseX <= left + guiWidth - 10)) {
			if (mouseY >= top + guiHeight - 220 && mouseY <= top + guiHeight - 200) {
				List<Component> text = new ArrayList<>();
				if (!pageTemplate.getIconItem().isEmpty()) {
					text.add(Component.literal(I18n.get(pageTemplate.getIconItem().getHoverName().getString())));
					graphics.renderComponentTooltip(font, text, left + guiWidth - 32, top + guiHeight - 220);
				}
			}
		}
		List<Component> titlePage = new ArrayList<Component>();
		titlePage.add( Component.literal(I18n.get("Title")));
		titlePage.add( Component.literal(I18n.get("Return to Catagories")));
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
		graphics.blit(texture, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
	}

	public void updateTextBoxes() {
//		if (!textBox.getValue().isEmpty()) {
//			if (NumberUtils.isCreatable(textBox.getValue())) {
//				int searchNum = (Integer.parseInt(textBox.getValue()));
//				if (searchNum < getPages().size()) {
//					mc.setScreen(getPages().get(searchNum));
//				} else if (searchNum >= getPages().size()) {
//					mc.setScreen(getPages().get(getPages().size() - 1));
//				}
//			}
//		}
	}
}
