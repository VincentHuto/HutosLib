package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class HLTomeCategoryTab extends HLButtonTextured {

	public ParticleColor color;

	public HLTomeCategoryTab(ParticleColor color, String category, int idIn, int x, int y, int locX, int locY,
			Button.OnPress pressedAction) {
		super(HLLocHelper.guiPrefix("book_tabs.png"), idIn, x, y, 24, 16, locX, locY, Component.literal(category),
				pressedAction);
		this.color = color;
	}

	public ParticleColor getColor() {
		return color;
	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
	}
}