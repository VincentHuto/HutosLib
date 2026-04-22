package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.CraftingRecipeTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class HLGuiGuideCraftingPage extends HLGuiGuidePage {


	public HLGuiGuideCraftingPage(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		super(pageNum, book, chapter);

	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderMenuBackground(graphics);
		left = width / 2 - guiWidth / 2;
		top = height / 2 - guiHeight / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ((PageTemplate) pageTemplate).getTextureLocation());

		HLGuiUtils.drawMaxWidthString(font, Component.literal("Pg." + (pageNum + 1)), left + guiWidth - 26,
				top + guiHeight - 15, 50, 0xffffff, true);

		graphics.renderFakeItem(((PageTemplate) pageTemplate).getIconItem(), left + guiWidth - 32,
				top + guiHeight - 220);
		if (!((PageTemplate) pageTemplate).getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(((PageTemplate) pageTemplate).getTitle())),
					left - guiWidth + 180, top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!((PageTemplate) pageTemplate).getSubtitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(I18n.get(((PageTemplate) pageTemplate).getSubtitle())), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}

		renderGhost(((CraftingRecipeTemplate) getPageTemplate()).getItemRecipe(), graphics, minecraft,
				left - guiWidth + 180, top + guiHeight - 210, true, partialTicks);

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
				if (!((PageTemplate) pageTemplate).getIconItem().isEmpty()) {
					text.add(Component
							.literal(I18n.get(((PageTemplate) pageTemplate).getIconItem().getHoverName().getString())));
					graphics.renderComponentTooltip(font, text, left + guiWidth - 32, top + guiHeight - 220);
				}
			}
		}
		List<Component> titlePage = new ArrayList<>();
		titlePage.add(Component.literal("Title"));
		titlePage.add(Component.literal("Return to Categories"));
		if (buttonTitle.isHovered()) {
			graphics.renderComponentTooltip(font, titlePage, mouseX, mouseY);
		}
		List<Component> ClosePage = new ArrayList<>();
		ClosePage.add(Component.literal("Close Book"));
		if (buttonCloseTab.isHoveredOrFocused()) {
			graphics.renderComponentTooltip(font, ClosePage, mouseX, mouseY);
		}
	}
	
	

	private void renderGhost(GhostRecipe itemRecipe, GuiGraphics pGuiGraphics, Minecraft pMinecraft, int pLeftPos,
			int pTopPos, boolean p_282174_, float pPartialTick) {
		itemRecipe.render(pGuiGraphics, pMinecraft, pLeftPos, pTopPos, p_282174_, pPartialTick);
	}

	public static void openScreenViaItem(int pNum, BookCodeModel pBook, ChapterTemplate pChapterTemplate) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new HLGuiGuideCraftingPage(pNum, pBook, pChapterTemplate));
	}

}
