package com.vincenthuto.hutoslib.client.screen.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.client.HLClientUtils;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.ScreenBlockTintGetter;
import com.vincenthuto.hutoslib.common.data.book.BookChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class HLGuiGuideMultiblockPage extends HLGuiGuidePage {

	double xDragPos = 0;
	double yDragPos = 0;
	private double dragLeftRight = 0;
	private double dragUpDown = 0;
	private MultiblockPattern pattern;

	public HLGuiGuideMultiblockPage(int pageNum, BookCodeModel book, BookChapterTemplate chapter,
			MultiblockPattern pattern) {
		super(pageNum, book, chapter);
		this.pattern = pattern;

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

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		float left = width / 2 - guiWidth / 2;
		float top = height / 2 - guiHeight / 2;
		int line = 0;
		for (Block block : pattern.getBlockCount(false).keySet()) {
			HLGuiUtils.drawMaxWidthString(font,
					Component.literal(
							I18n.get(block.getDescriptionId()) + ": " + pattern.getBlockCount(false).get(block)),
					(int) (left - guiWidth + 180), (int) (top + guiHeight - 140) - line * -10, 160, 0xffffff, true);
			line++;
		}

		PoseStack matrices = graphics.pose();
		matrices.mulPose(Vector3.XN.rotationDegrees(-45 + (float) this.dragUpDown).toMoj());
		matrices.mulPose(Vector3.YP.rotationDegrees(45 + (float) this.dragLeftRight).toMoj());
		float structScale = 2f;
		matrices.scale(structScale, structScale, structScale);
		HLGuiUtils.renderMultiBlock(matrices, pattern, HLClientUtils.getPartialTicks(), new ScreenBlockTintGetter(),
				left - guiWidth + 260, top + guiHeight - 65);

	}

}
