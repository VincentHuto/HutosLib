package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.LocationHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class GuiButtonBookArrow extends GuiButtonTextured {

	int buttonWidth, buttonHeight;
	int id, u, v;
	public ArrowDirection direction;

	public GuiButtonBookArrow(ArrowDirection direction, int idIn, int x, int y, Button.OnPress pressedAction) {
		super(direction.texture, idIn, x, y, 24, 16, 0, 0, pressedAction);
		this.direction = direction;

	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}

	public static enum ArrowDirection {
		FORWARD(LocationHelper.guiPrefix("arrow_forward.png")), BACKWARD(LocationHelper.guiPrefix("arrow_back.png"));

		ResourceLocation texture;

		ArrowDirection(ResourceLocation texture) {
			this.texture = texture;
		}
	}
}