package com.vincenthuto.hutoslib.client.screen.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.vincenthuto.hutoslib.client.HLClientUtils;
import com.vincenthuto.hutoslib.client.render.block.MultiblockPattern;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class GuiGuideMultiblockPage extends GuiGuidePage {

	double xDragPos = 0;
	double yDragPos = 0;
	private double dragLeftRight = 0;
	private double dragUpDown = 0;
	private MultiblockPattern pattern;

	public GuiGuideMultiblockPage(int pageNumIn, String catagoryIn, MultiblockPattern pattern) {
		super(pageNumIn, catagoryIn);
		this.pattern = pattern;
	}

	public GuiGuideMultiblockPage(int pageNumIn, String catagoryIn, String titleIn, MultiblockPattern pattern) {
		super(pageNumIn, catagoryIn, titleIn, "", "");
		this.pattern = pattern;

	}

	public GuiGuideMultiblockPage(int pageNumIn, String catagoryIn, String titleIn, String textIn,
			MultiblockPattern pattern) {
		super(pageNumIn, catagoryIn, titleIn, "", textIn);
		this.pattern = pattern;

	}

	public GuiGuideMultiblockPage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, String textIn,
			MultiblockPattern pattern) {
		super(pageNumIn, catagoryIn, titleIn, subtitleIn, textIn);
		this.pattern = pattern;

	}

	public GuiGuideMultiblockPage(int pageNumIn, String categoryIn, String titleIn, String subtitleIn, String textIn,
			ItemStack iconIn, MultiblockPattern pattern) {
		super(pageNumIn, categoryIn, titleIn, subtitleIn, iconIn, textIn);
		this.pattern = pattern;

	}

	public GuiGuideMultiblockPage(int pageNumIn, String categoryIn, String titleIn, ItemStack iconIn,
			MultiblockPattern pattern) {
		super(pageNumIn, categoryIn, titleIn, "", iconIn);
		this.pattern = pattern;

	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
		super.render(matrices, mouseX, mouseY, partialTicks);
		float left = width / 2 - guiWidth / 2;
		float top = height / 2 - guiHeight / 2;
		int line = 0;
		for (Block block : pattern.getBlockCount(false).keySet()) {
			HLGuiUtils.drawMaxWidthString(font,
					 new TranslatableComponent(
							I18n.get(block.getDescriptionId()) + ": " + pattern.getBlockCount(false).get(block)),
					(int) (left - guiWidth + 180), (int) (top + guiHeight - 140) - line * -10, 160, 0xffffff, true);
			line++;
			//System.out.println(I18n.get(block.getDescriptionId()) + ": " + pattern.getBlockCount(false).get(block));
		}

		matrices.mulPose(Vector3f.XN.rotationDegrees(-45 + (float) this.dragUpDown));
		matrices.mulPose(Vector3f.YP.rotationDegrees(45 + (float) this.dragLeftRight));
		float structScale = 2f;
		matrices.scale(structScale, structScale, structScale);
		HLGuiUtils.renderMultiBlock(matrices, pattern, HLClientUtils.getPartialTicks(), new ScreenBlockTintGetter(),
				left - guiWidth + 260, top + guiHeight - 65);

	}
	
	public MultiblockPattern getPattern() {
		return pattern;
	}

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragLeftRight, double dragUpDown) {
		xDragPos = xPos;
		yDragPos = yPos;
		this.dragLeftRight += dragLeftRight / 2;
		this.dragUpDown -= dragUpDown / 2;
		return super.mouseDragged(xPos, yPos, button, dragLeftRight, dragUpDown);
	}

}
