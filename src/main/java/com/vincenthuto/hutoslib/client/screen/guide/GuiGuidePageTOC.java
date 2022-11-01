package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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


	public GuiGuidePageTOC(String catagoryIn, String subtitle) {
		super(0, catagoryIn, "Table of Contents", subtitle, ItemStack.EMPTY, "");
	}

	public GuiGuidePageTOC(String catagoryIn, ItemStack iconIn, String subtitle) {
		super(0, catagoryIn, "Table of Contents", subtitle, iconIn, "");
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		for (int i = 1; i < chapterButtons.size(); i++) {
			chapterButtons.get(i).renderButton(matrixStack, mouseX, mouseY, partialTicks);
			HLGuiUtils.drawMaxWidthString(font,  Component.literal("Pg." + i), (int) (chapterButtons.get(i).posX + 5),
					chapterButtons.get(i).posY + 2, 150, 0xffffff, true);
			HLGuiUtils.drawMaxWidthString(font,  Component.literal(getPages().get(i).title),
					(int) (chapterButtons.get(i).posX + 30), chapterButtons.get(i).posY + 2, 150, 0xffffff, true);
		}
	}

	@Override
	public void renderBackground(PoseStack p_96557_) {
		super.renderBackground(p_96557_);
	}

	@Override
	public void renderTooltip(PoseStack p_169389_, List<Component> p_169390_, Optional<TooltipComponent> p_169391_,
			int mouseX, int mouseY) {
		super.renderTooltip(p_169389_, p_169390_, p_169391_, mouseX, mouseY);
	}

	@Override
	public void renderComponentTooltip(PoseStack p_96598_, List<Component> p_96599_, int mouseX, int mouseY) {
		super.renderComponentTooltip(p_96598_, p_96599_, mouseX, mouseY);
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
