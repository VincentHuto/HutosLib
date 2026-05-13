package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.common.util.HLResourceUtils;
import com.vincenthuto.hutoslib.common.util.ParticleColor;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class HLTomeCategoryTab extends HLButtonTextured {

	private static final int TAB_U = 0;

	public ParticleColor color;

	public HLTomeCategoryTab(ParticleColor color, String category, int idIn, int x, int y, int locX, int locY,
			Button.OnPress pressedAction) {
		super(HLResourceUtils.guiPrefix("book_tabs.png"), idIn, x, y, 24, 16, TAB_U, tabV(color), Component.empty(),
				pressedAction);
		this.color = color;
		this.setTooltip(Tooltip.create(Component.literal(category)));
	}

	public ParticleColor getColor() {
		return color;
	}

	private static int tabV(ParticleColor color) {
		int r = Math.round(color.getRed());
		int g = Math.round(color.getGreen());
		int b = Math.round(color.getBlue());
		if (r > 220 && g > 220 && b > 220) {
			return 192;
		}
		if (r > 220 && g > 220) {
			return 96;
		}
		if (g > 220 && b > 220) {
			return 160;
		}
		if (r > 220 && b > 220) {
			return 128;
		}
		if (g > 220) {
			return 32;
		}
		if (b > 220) {
			return 64;
		}
		if (r > 220) {
			return 0;
		}
		return 208;
	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}
}
