package com.vincenthuto.hutoslib.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class HLButtonTextured extends Button {
	public final Identifier texture;
	public int id, posX, posY, buttonWidth, buttonHeight, u, v, adjV, newV;
	boolean state;
	public Button.OnPress action;
	public Component text;

	public HLButtonTextured(Identifier texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnPress actionIn) {
		this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, stateIn,
				Component.empty(), actionIn);
	}

	public HLButtonTextured(Identifier texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Component text, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonWidthIn, buttonHeightIn, text, actionIn, DEFAULT_NARRATION);
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

	public HLButtonTextured(Identifier texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Button.OnPress actionIn) {
		this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, false, actionIn);
	}

	public HLButtonTextured(Identifier texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Component text, Button.OnPress actionIn) {
		this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, false, text, actionIn);
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
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		this.posX = this.getX();
		this.posY = this.getY();
		int drawV = (this.isHoveredOrFocused() || this.state) ? this.adjV : this.v;
		graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.u, drawV,
				this.width, this.height, 256, 256);
		if (!this.text.getString().isEmpty()) {
			graphics.centeredText(net.minecraft.client.Minecraft.getInstance().font, this.text,
					this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0x404040);
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
