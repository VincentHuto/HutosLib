package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.LocationHelper;
import com.vincenthuto.hutoslib.client.TextUtils;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuideTitlePage extends Screen {

	final ResourceLocation texture = LocationHelper.guiPrefix("title.png");
	final ResourceLocation overlay;
	Minecraft mc = Minecraft.getInstance();
	int guiWidth = 186;
	int guiHeight = 240;
	int left, top;
	final int BUTTONCLOSE = 30;
	TextComponent titleComponent;
	public ItemStack icon;
	GuiButtonTextured buttonclose;
	public List<TomeChapter> categories = new ArrayList<TomeChapter>();
	public List<GuiButtonTextured> buttonList = new ArrayList<GuiButtonTextured>();

	public GuiGuideTitlePage() {
		super(new TextComponent(""));
		this.icon = ItemStack.EMPTY;
		this.overlay = LocationHelper.guiPrefix("blank.png");
	}

	public GuiGuideTitlePage(TextComponent title, ItemStack stack, List<TomeChapter> chapters,
			ResourceLocation overlay) {
		super(title);
		this.titleComponent = title;
		this.icon = stack;
		this.categories = chapters;
		this.overlay = overlay;
	}

	public GuiGuideTitlePage(TextComponent title, ItemStack stack, List<TomeChapter> chapters) {
		super(title);
		this.titleComponent = title;
		this.icon = stack;
		this.categories = chapters;
		this.overlay = LocationHelper.guiPrefix("blank.png");
	}

	public GuiGuideTitlePage(TextComponent title, List<TomeChapter> chapters, ResourceLocation overlay) {
		super(title);
		this.titleComponent = title;
		this.icon = ItemStack.EMPTY;
		this.categories = chapters;
		this.overlay = overlay;
	}

	public GuiGuideTitlePage(TextComponent title, List<TomeChapter> chapters) {
		super(title);
		this.titleComponent = title;
		this.icon = ItemStack.EMPTY;
		this.categories = chapters;
		this.overlay = LocationHelper.guiPrefix("blank.png");
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

		if (!title.getContents().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, title, centerX + 10, centerY + 10, 165, 0xffffff, true);
		}

		matrixStack.pushPose();
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, left + guiWidth - 48,
				top + guiHeight - 230);
		matrixStack.popPose();

		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
			if (buttonList.get(i).isHoveredOrFocused()) {
				renderTooltip(matrixStack, buttonList.get(i).text, buttonList.get(i).x, buttonList.get(i).y);
			}
		}

		this.buttonclose.render(matrixStack, mouseX, mouseY, partialTicks);
		if (this.buttonclose.isHoveredOrFocused()) {
			renderTooltip(matrixStack, new TextComponent("Close"), this.buttonclose.x, this.buttonclose.y);
		}
	}

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
					TextUtils.toProperCase(categories.get(i).category), i,
					(int) (centerX + (guiWidth * 0.05f) + 167 + (rand.nextInt(6) - rand.nextInt(4))),
					(int) (centerY - (i * -25) + 18), (press) -> {
						if (press instanceof GuiButtonTextured button) {
							mc.setScreen(getOwnerTome().getMatchingChapters(categories.get(button.id).category).TOC);
						}
					});
			buttonList.add(tab);
			this.addRenderableWidget(buttonList.get(i));
		}
	}

	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
		if (p_96552_ == GLFW.GLFW_KEY_E || p_96552_ == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
			this.onClose();
		}
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public abstract TomeLib getOwnerTome();
}
