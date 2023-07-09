package com.vincenthuto.hutoslib.common.data.book;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TestGuiGuidePageTOC extends Screen {
	GuiButtonTextured buttonTOC;
	protected final ResourceLocation texture = HLLocHelper.guiPrefix("page.png");

	protected int left;
	protected int top;
	public int guiHeight = 228, guiWidth = 174;

	public List<GuiButtonTextured> chapterButtons = new ArrayList<>();
	private BookChapterTemplate chapterTemplate;
	protected Minecraft mc = Minecraft.getInstance();

	public TestGuiGuidePageTOC(BookChapterTemplate chapterTemplate) {
		super(Component.literal(chapterTemplate.title));
		this.chapterTemplate = chapterTemplate;
	}

	@Override
	protected void init() {
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		int sideLoc = left + guiWidth;
		int verticalLoc = top + guiHeight;
		this.clearWidgets();
		chapterButtons.clear();
		super.init();
		for (int i = 0; i < chapterTemplate.getPages().size(); i++) {
			chapterButtons.add(new GuiButtonTextured(texture, i, sideLoc - (guiWidth - 5),
					(verticalLoc - 210) + (i * 15), 163, 14, 5, 228, (press) -> {
						if (press instanceof GuiButtonTextured button) {
							mc.setScreen(new TestGuiGuidePage(chapterTemplate.getPages().get(button.getId())));
						}
					}));
		}
		for (GuiButtonTextured chapterButton : chapterButtons) {
			this.addRenderableWidget(chapterButton);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		for (int i = 1; i < chapterButtons.size(); i++) {
			chapterButtons.get(i).render(graphics, mouseX, mouseY, partialTicks);
			HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + i), chapterButtons.get(i).posX + 5,
					chapterButtons.get(i).posY + 2, 150, 0xffffff, true);
			HLGuiUtils.drawMaxWidthString(font, Component.literal(chapterTemplate.getPages().get(i).title),
					chapterButtons.get(i).posX + 30, chapterButtons.get(i).posY + 2, 150, 0xffffff, true);
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

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}
