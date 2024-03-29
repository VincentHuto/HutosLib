package com.vincenthuto.hutoslib.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HLButtonTextured extends Button {

	public final ResourceLocation texture;
	public int id, posX, posY, buttonWidth, buttonHeight, u, v, adjV, newV;
	boolean state;
	public Button.OnPress action;
	public Component text;

	public HLButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, Component.literal(""), actionIn, DEFAULT_NARRATION);
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
		this.text = Component.literal("");

	}

	public HLButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Component text, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn, DEFAULT_NARRATION);
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
		this.text = text;

	}

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

	public HLButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Button.OnPress actionIn) {
		this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, false, actionIn);

	}

	public HLButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Component text, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, text, actionIn, DEFAULT_NARRATION);
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
		this.state = false;
		this.text = text;

	}

	public Button.OnPress getAction() {
		return action;
	}

	public int getId() {
		return id;
	}

	public boolean getState() {
		return state;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float particks) {
		if (visible) {
			if (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width
					&& mouseY < this.getY() + this.height) {
				this.isHovered = true;
				v = newV;
				graphics.blit(texture, posX, posY, u, adjV, width, height);
			} else if (state) {
				v = newV;
				graphics.blit(texture, posX, posY, u, adjV, width, height);
			} else {
				this.isHovered = false;
				newV = v;
				graphics.blit(texture, posX, posY, u, v, width, height);
			}
		}


	}

	public void setAction(Button.OnPress action) {
		this.action = action;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
