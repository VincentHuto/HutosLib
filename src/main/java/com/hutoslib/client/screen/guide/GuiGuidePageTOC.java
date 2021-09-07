package com.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.hutoslib.client.screen.GuiButtonTextured;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;

public abstract class GuiGuidePageTOC extends GuiGuidePage {
	GuiButtonTextured buttonTOC;
	public List<GuiGuidePage> chapterPages;
	public List<GuiButtonTextured> chapterButtons = new ArrayList<GuiButtonTextured>();

	public GuiGuidePageTOC(ITomeCatagory catagoryIn, List<GuiGuidePage> chapterPages, ItemStack iconIn) {
		super(0, catagoryIn, chapterPages, "Table of Contents", "", iconIn, "");
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		for (int i = 1; i < chapterButtons.size(); i++) {
			chapterButtons.get(i).renderButton(matrixStack, mouseX, mouseY, partialTicks);
			drawString(matrixStack, font, "Pg." + i, (chapterButtons.get(i).posX + 2), chapterButtons.get(i).posY + 2,
					8060954);
			drawString(matrixStack, font, getMatchingChapter().get(i).title, (int) (chapterButtons.get(i).posX + 30),
					chapterButtons.get(i).posY + 2, 8060954);
		}
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
		for (int i = 0; i < chapterPages.size(); i++) {
			chapterButtons.add(new GuiButtonTextured(texture, i, sideLoc - (guiWidth - 5),
					(verticalLoc - 210) + (i * 15), 163, 14, 5, 228, (press) -> {
						if (press instanceof GuiButtonTextured button) {
							tableButtonCheck((button.getId()));
						}
					}));
		}
		for (int i = 0; i < chapterButtons.size(); i++) {
			this.addRenderableWidget(chapterButtons.get(i));
		}
	}

	public void tableButtonCheck(int page) {
		mc.setScreen(this.getMatchingChapter().get(page));
	}

	public abstract List<GuiGuidePage> getMatchingChapter();

}
