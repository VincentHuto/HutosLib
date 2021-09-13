package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.GuiUtils;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public abstract class GuiGuidePageTOC extends GuiGuidePage {
	GuiButtonTextured buttonTOC;
	public List<GuiButtonTextured> chapterButtons = new ArrayList<GuiButtonTextured>();

	public GuiGuidePageTOC(String catagoryIn) {
		this(catagoryIn, ItemStack.EMPTY);
	}

	public GuiGuidePageTOC(String catagoryIn, ItemStack iconIn) {
		super(0, catagoryIn, "Table of Contents", "", iconIn, "");
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		for (int i = 1; i < chapterButtons.size(); i++) {
			chapterButtons.get(i).renderButton(matrixStack, mouseX, mouseY, partialTicks);
			GuiUtils.drawMaxWidthString(font, new TextComponent("Pg." + i), (int) (chapterButtons.get(i).posX + 5),
					chapterButtons.get(i).posY + 2, 50, 0xffffff, true);
			GuiUtils.drawMaxWidthString(font, new TextComponent(getPages().get(i).title),
					(int) (chapterButtons.get(i).posX + 30), chapterButtons.get(i).posY + 2, 50, 0xffffff, true);
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
		for (int i = 0; i < getPages().size(); i++) {
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
		mc.setScreen(getPages().get(page));
	}

}
