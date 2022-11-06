package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class TomeCategoryTab extends GuiButtonTextured {

	public static enum TabColor {
		RED(0, 0), GREEN(0, 32), BLUE(0, 64), YELLOW(0, 96), ORANGE(24, 64), PURPLE(0, 128), CYAN(0, 160),
		WHITE(0, 192), BLACK(0, 224), RAINBOW(24, 96);

		int locX, locY;

		TabColor(int x, int y) {
			this.locX = x;
			this.locY = y;

		}
	}

	public TabColor color;

	public TomeCategoryTab(TabColor color, String category, int idIn, int x, int y, Button.OnPress pressedAction) {
		super(HLLocHelper.guiPrefix("book_tabs.png"), idIn, x, y, 24, 16, color.locX, color.locY,
				Component.literal(category), pressedAction);
		this.color = color;
	}

	public TabColor getColor() {
		return color;
	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}
}