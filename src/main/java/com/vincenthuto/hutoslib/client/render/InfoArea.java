/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */

package com.vincenthuto.hutoslib.client.render;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public abstract class InfoArea implements Renderable {
	protected final Rect2i area;

	protected InfoArea(Rect2i area) {
		this.area = area;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= area.getX() && mouseY >= area.getY() && mouseX < area.getX() + area.getWidth()
				&& mouseY < area.getY() + area.getHeight();
	}

	public final void fillTooltip(int mouseX, int mouseY, List<Component> tooltip) {
		if (area.contains(mouseX, mouseY))
			fillTooltipOverArea(mouseX, mouseY, tooltip);
	}

	protected abstract void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip);

}