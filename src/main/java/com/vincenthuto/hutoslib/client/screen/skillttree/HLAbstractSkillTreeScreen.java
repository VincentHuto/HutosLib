package com.vincenthuto.hutoslib.client.screen.skillttree;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HLAbstractSkillTreeScreen extends Screen {

	public float xOffset;
	public float yOffset;
	public float cachedXOffset;
	public float cachedYOffset;
	public boolean ignoreNextMouseInput;

//    public final List<BookObject> bookObjects = new ArrayList<>();

	public final int bookWidth;
	public final int bookHeight;
	public final int bookInsideWidth;
	public final int bookInsideHeight;

	public final int backgroundImageWidth;
	public final int backgroundImageHeight;

	protected HLAbstractSkillTreeScreen(int backgroundImageWidth, int backgroundImageHeight) {
		this(378, 250, 344, 218, backgroundImageWidth, backgroundImageHeight);
	}

	protected HLAbstractSkillTreeScreen(int bookWidth, int bookHeight, int bookInsideWidth, int bookInsideHeight,
			int backgroundImageWidth, int backgroundImageHeight) {
		super(Component.translatable("hutoslib.gui.book.title"));
		this.bookWidth = bookWidth;
		this.bookHeight = bookHeight;
		this.bookInsideWidth = bookInsideWidth;
		this.bookInsideHeight = bookInsideHeight;
		this.backgroundImageWidth = backgroundImageWidth;
		this.backgroundImageHeight = backgroundImageHeight;
		minecraft = Minecraft.getInstance();
	}

	public void renderBackground(ResourceLocation texture, GuiGraphics graphics, float xModifier, float yModifier) {
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		int insideLeft = guiLeft + 17;
		int insideTop = guiTop + 14;
		float uOffset = (backgroundImageWidth - xOffset) * xModifier;
		float vOffset = Math.min(backgroundImageHeight - bookInsideHeight,
				(backgroundImageHeight - bookInsideHeight - yOffset * yModifier));
		if (vOffset <= backgroundImageHeight / 2f) {
			vOffset = backgroundImageHeight / 2f;
		}
		if (uOffset <= 0) {
			uOffset = 0;
		}
		if (uOffset > (bookInsideWidth - 8) / 2f) {
			uOffset = (bookInsideWidth - 8) / 2f;
		}
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		graphics.blit(texture, insideLeft, insideTop, uOffset, vOffset, bookInsideWidth, bookInsideHeight,
				backgroundImageWidth / 2, backgroundImageHeight / 2);
		// renderTexture(texture, graphics.pose(), insideLeft, insideTop, uOffset,
		// vOffset, bookInsideWidth, bookInsideHeight, backgroundImageWidth / 2,
		// backgroundImageHeight / 2);
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		xOffset += dragX;
		yOffset += dragY;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		cachedXOffset = xOffset;
		cachedYOffset = yOffset;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (ignoreNextMouseInput) {
			ignoreNextMouseInput = false;
			return super.mouseReleased(mouseX, mouseY, button);
		}
		if (xOffset != cachedXOffset || yOffset != cachedYOffset) {
			return super.mouseReleased(mouseX, mouseY, button);
		}
//		for (BookObject object : bookObjects) {
//			if (object.isHovering(this, xOffset, yOffset, mouseX, mouseY)) {
//				object.click(xOffset, yOffset, mouseX, mouseY);
//				break;
//			}
//		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	public boolean isHovering(double mouseX, double mouseY, int posX, int posY, int width, int height) {
		if (!isInView(mouseX, mouseY)) {
			return false;
		} else {
			return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height;
		}
	}

	public boolean isInView(double mouseX, double mouseY) {
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		return !(mouseX < guiLeft + 17) && !(mouseY < guiTop + 14) && !(mouseX > guiLeft + (bookWidth - 17))
				&& !(mouseY > (guiTop + bookHeight - 14));
	}

	public void cut() {
		int scale = (int) getMinecraft().getWindow().getGuiScale();
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;
		int insideLeft = guiLeft + 17;
		int insideTop = guiTop + 18;
		GL11.glScissor(insideLeft * scale, insideTop * scale, bookInsideWidth * scale, (bookInsideHeight + 1) * scale); // do
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
