package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.LocationHelper;
import com.vincenthuto.hutoslib.client.screen.GuiButtonTextured;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

public class TomeCategoryTab extends GuiButtonTextured {

	public TabColor color;
	public String category;
	int buttonWidth, buttonHeight;

	public TomeCategoryTab(TabColor color, String category, int idIn, int x, int y,
			Button.OnPress pressedAction) {
		super(LocationHelper.guiPrefix("book_tabs.png"), idIn, x, y, color.locX, color.locY, 24, 16, pressedAction);
		this.color = color;
		this.category = category;
	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}

	public String getCategory() {
		return category;
	}

	public TabColor getColor() {
		return color;
	}

	public static enum TabColor {
		RED(0, 0), GREEN(0, 32), BLUE(0, 64), YELLOW(0, 96), PURPLE(0, 128), CYAN(0, 160), WHITE(0, 192), BLACK(0, 224);

		int locX, locY;

		TabColor(int x, int y) {
			this.locX = x;
			this.locY = y;

		}
	}
}