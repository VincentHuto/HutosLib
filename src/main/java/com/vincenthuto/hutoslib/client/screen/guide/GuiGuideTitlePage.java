package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.HLTextUtils;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuideTitlePage extends Screen {

	final ResourceLocation texture = HLLocHelper.guiPrefix("title.png");
	final ResourceLocation overlay;
	Minecraft mc = Minecraft.getInstance();
	int guiWidth = 186;
	int guiHeight = 240;
	int left, top;
	final int BUTTONCLOSE = 30;
	Component titleComponent;
	public ItemStack icon;
	GuiButtonTextured buttonclose;
	public List<TomeChapter> categories = new ArrayList<>();
	public List<GuiButtonTextured> buttonList = new ArrayList<>();

	public GuiGuideTitlePage() {
		super(Component.translatable(""));
		this.icon = ItemStack.EMPTY;
		this.overlay = HLLocHelper.guiPrefix("blank.png");
	}

	public GuiGuideTitlePage(Component title, ItemStack stack, List<TomeChapter> chapters) {
		super(title);
		this.titleComponent = title;
		this.icon = stack;
		this.categories = chapters;
		this.overlay = HLLocHelper.guiPrefix("blank.png");
	}

	public GuiGuideTitlePage(Component title, ItemStack stack, List<TomeChapter> chapters, ResourceLocation overlay) {
		super(title);
		this.titleComponent = title;
		this.icon = stack;
		this.categories = chapters;
		this.overlay = overlay;
	}

	public GuiGuideTitlePage(Component title, List<TomeChapter> chapters) {
		super(title);
		this.titleComponent = title;
		this.icon = ItemStack.EMPTY;
		this.categories = chapters;
		this.overlay = HLLocHelper.guiPrefix("blank.png");
	}

	public GuiGuideTitlePage(Component title, List<TomeChapter> chapters, ResourceLocation overlay) {
		super(title);
		this.titleComponent = title;
		this.icon = ItemStack.EMPTY;
		this.categories = chapters;
		this.overlay = overlay;
	}

	public abstract TomeLib getOwnerTome();

	@Override
	public void init() {
		Random rand = new Random();
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.buttonList.clear();
		this.clearWidgets();
		this.addRenderableWidget(
				buttonclose = new GuiButtonTextured(overlay, BUTTONCLOSE, (int) (centerX + (guiWidth * 0.05f)),
						(int) (centerY + (guiHeight * 0.78f)), 32, 32, 209, 32, (press) -> {
							onClose();
						}));

		for (int i = 0; i < categories.size(); i++) {
			TomeCategoryTab tab = new TomeCategoryTab(categories.get(i).color,
					HLTextUtils.toProperCase(categories.get(i).category), i,
					(int) (centerX + (guiWidth * 0.05f) + 167 + (rand.nextInt(6) - rand.nextInt(4))),
					centerY - (i * -25) + 18, (press) -> {
						if (press instanceof GuiButtonTextured button) {
							mc.setScreen(getOwnerTome().getMatchingChapters(categories.get(button.id).category).TOC);
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
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.blit(matrixStack, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);
		RenderSystem.setShaderTexture(0, this.overlay);
		this.blit(matrixStack, centerX, centerY, 0, 0, this.guiWidth, this.guiHeight);

		title.getContents();
		if (title.getContents() != ComponentContents.EMPTY) {
			HLGuiUtils.drawMaxWidthString(font, title, centerX + 10, centerY + 10, 165, 0xffffff, true);
		}

		matrixStack.pushPose();
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, left + guiWidth - 48,
				top + guiHeight - 230);
		matrixStack.popPose();

		for (GuiButtonTextured element : buttonList) {
			element.render(matrixStack, mouseX, mouseY, partialTicks);
			if (element.isHoveredOrFocused()) {
				renderTooltip(matrixStack, element.text, element.x, element.y);
			}
		}

		this.buttonclose.render(matrixStack, mouseX, mouseY, partialTicks);
		if (this.buttonclose.isHoveredOrFocused()) {
			renderTooltip(matrixStack, Component.translatable("Close"), this.buttonclose.x, this.buttonclose.y);
		}
	}
}
