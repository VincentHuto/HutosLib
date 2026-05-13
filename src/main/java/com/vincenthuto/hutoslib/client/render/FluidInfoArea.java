package com.vincenthuto.hutoslib.client.render;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.IFluidTank;

import java.util.List;

public class FluidInfoArea extends InfoArea {
	private final IFluidTank tank;

	public FluidInfoArea(IFluidTank tank, Rect2i area, int overlayUMin, int overlayVMin, int overlayWidth,
			int overlayHeight) {
		super(area);
		this.tank = tank;
	}

	@Override
	protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip) {
		if (this.tank != null && !this.tank.getFluid().isEmpty()) {
			tooltip.add(this.tank.getFluid().getHoverName());
		}
	}
}
