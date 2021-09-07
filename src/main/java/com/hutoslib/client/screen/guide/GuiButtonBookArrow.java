package com.hutoslib.client.screen.guide;

import com.hutoslib.client.LocationHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class GuiButtonBookArrow extends Button {

	int buttonWidth;
	int buttonHeight;
	int u = 0;
	int v = 0;
	public int id;
	public ArrowDirection direction;

	public GuiButtonBookArrow(ArrowDirection direction, int idIn, int x, int y, Button.OnPress pressedAction) {
		super(x, y, 24, 16, new TextComponent(""), pressedAction);
		this.id = idIn;
		this.u = 0;
		this.v = 0;
		this.direction = direction;

	}

	@Override
	public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (visible) {

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, direction.texture);

			if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
				this.isHovered = true;
			} else {
				this.isHovered = false;
			}
			if (this.isHovered) {
				v = 18;
			} else {
				v = 1;
			}
			this.blit(matrix, x, y, u, v, width, height);
		}
	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}

	public int getId() {
		return id;
	}

	public void setid() {
	}

	public static enum ArrowDirection {
		FORWARD(LocationHelper.guiPrefix("arrow_forward.png")),
		BACKWARD(LocationHelper.guiPrefix("arrow_back.png"));

		ResourceLocation texture;

		ArrowDirection(ResourceLocation texture) {
			this.texture = texture;
		}
	}
}