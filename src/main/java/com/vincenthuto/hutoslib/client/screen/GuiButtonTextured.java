package com.vincenthuto.hutoslib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiButtonTextured extends Button {

	public final ResourceLocation texture;
	public int id, posX, posY, buttonWidth, buttonHeight, u, v, adjV, newV;
	boolean state;
	protected Button.OnTooltip onTooltip;
	public Button.OnPress action;
	public Component text;

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
			int buttonHeightIn, int uIn, int vIn, Button.OnPress actionIn) {
		this(texIn, idIn, posXIn, posYIn, buttonWidthIn, buttonHeightIn, uIn, vIn, false, actionIn);

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Component text, Button.OnPress actionIn) {
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
		this.text = text;

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, Component text, Button.OnPress actionIn) {
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
		this.state = false;
		this.text = text;

	}

	public GuiButtonTextured(ResourceLocation texIn, int idIn, int posXIn, int posYIn, int buttonWidthIn,
			int buttonHeightIn, int uIn, int vIn, boolean stateIn, Button.OnPress actionIn) {
		super(posXIn, posYIn, buttonHeightIn, buttonWidthIn, Component.translatable(""), actionIn);
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
		this.text =  Component.translatable("");

	}

	@Override
	public void renderButton(PoseStack matrix, int mouseX, int mouseY, float particks) {
		if (visible) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, texture);
			
			
			if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
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
