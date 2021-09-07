package com.hutoslib.client.screen.guide;

import com.hutoslib.HutosLib;
import com.hutoslib.client.screen.GuiButtonTextured;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuideTitlePage extends Screen {

	final ResourceLocation texture = new ResourceLocation(HutosLib.MOD_ID, "textures/gui/title.png");
	Minecraft mc = Minecraft.getInstance();
	int guiWidth = 186;
	int guiHeight = 240;
	int left, top;
	final int BUTTONCLOSE = 0;
	static String title = " Table of Contents";
	static TextComponent titleComponent = new TextComponent(title);
	public ItemStack icon;
	GuiButtonTextured buttonclose;

	public GuiGuideTitlePage() {
		super(titleComponent);
		this.icon = ItemStack.EMPTY;
	}

	public GuiGuideTitlePage(ItemStack stack) {
		super(titleComponent);
		this.icon = stack;
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
		this.buttonclose.render(matrixStack, mouseX, mouseY, partialTicks);

		if (this.buttonclose.isHovered()) {
			renderTooltip(matrixStack, new TextComponent("Close"), this.buttonclose.x, this.buttonclose.y);
		}
	}

	@Override
	public void init() {
		int centerX = (width / 2) - guiWidth / 2;
		int centerY = (height / 2) - guiHeight / 2;
		this.clearWidgets();
		this.addRenderableWidget(
				buttonclose = new GuiButtonTextured(texture, BUTTONCLOSE, (int) (centerX + (guiWidth * 0.05f)),
						(int) (centerY + (guiHeight * 0.78f)), 32, 32, 209, 32, (press) -> {
							onClose();
						}));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
