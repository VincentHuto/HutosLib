package com.hutoslib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class GuiButtonTextured extends Button {

	public final ResourceLocation texture;
	public int id, posX, posY, buttonWidth, buttonHeight, u, v, adjV, newV;
	boolean state;
	protected Button.OnTooltip onTooltip;
	public static Component text = new TextComponent("");
	public Button.OnPress action;

	/***
	 * 
	 * @param texIn          Texture Location
	 * @param idIn           Button Id
	 * @param posXIn         Screen X
	 * @param posYIn         Screen Y
	 * @param buttonWidthIn  Button Size Width
	 * @param buttonHeightIn Button Size Height
	 * @param uIn            Texture X Loc
	 * @param vIn            Texture Y Loc
	 * @param tooltip        Hover Tooltip
	 * @param actionIn       On Pressed Action
	 */

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Button.OnTooltip tooltip, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn, tooltip);
		this.texture = texIn;
		this.id = idIn;
		this.posX = posXIn;
		this.posY = posYIn;
		this.width = buttonWidthIn;
		this.height = buttonHeightIn;
		this.u = uIn;
		this.v = vIn;
		this.adjV = vIn + buttonHeightIn;
		this.newV = vIn;
		this.onTooltip = tooltip;
		this.action = actionIn;

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnTooltip tooltip, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn, tooltip);
		this.texture = texIn;
		this.id = idIn;
		this.posX = posXIn;
		this.posY = posYIn;
		this.width = buttonWidthIn;
		this.height = buttonHeightIn;
		this.u = uIn;
		this.v = vIn;
		this.adjV = vIn + buttonHeightIn;
		this.newV = vIn;
		this.action = actionIn;
		this.state = stateIn;

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn);
		this.texture = texIn;
		this.id = idIn;
		this.posX = posXIn;
		this.posY = posYIn;
		this.width = buttonWidthIn;
		this.height = buttonHeightIn;
		this.u = uIn;
		this.v = vIn;
		this.adjV = vIn + buttonHeightIn;
		this.newV = vIn;
		this.action = actionIn;

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn);
		this.texture = texIn;
		this.id = idIn;
		this.posX = posXIn;
		this.posY = posYIn;
		this.width = buttonWidthIn;
		this.height = buttonHeightIn;
		this.u = uIn;
		this.v = vIn;
		this.adjV = vIn + buttonHeightIn;
		this.newV = vIn;
		this.action = actionIn;
		this.state = stateIn;

	}

	@Override
	public void renderButton(PoseStack matrix, int mouseX, int mouseY, float particks) {
		if (visible) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, texture);
			if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
				this.isHovered = true;
				v = newV;
				this.blit(matrix, posX, posY, u, adjV, width, height);
			} else if (state == true) {
				v = newV;
				this.blit(matrix, posX, posY, u, adjV, width, height);
			} else {
				this.isHovered = false;
				newV = v;
				this.blit(matrix, posX, posY, u, v, width, height);
			}
		}

	}

	// Plays the clicking noise when the page turn button is pressed
	@Override
	public void playDownSound(SoundManager handler) {
		handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PUT, 1.0f, 1F));
	}

	public Button.OnPress getAction() {
		return action;
	}

	public void setAction(Button.OnPress action) {
		this.action = action;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
