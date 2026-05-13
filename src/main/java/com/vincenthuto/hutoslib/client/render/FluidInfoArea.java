/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
package com.vincenthuto.hutoslib.client.render;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

import java.util.List;
import java.util.function.Consumer;


public class FluidInfoArea extends InfoArea {
	private final IFluidTank tank;
	private final Rect2i area;
	private final int overlayUMin;
	private final int overlayVMin;
	private final int overlayWidth;
	private final int overlayHeight;
	private final Identifier overlayTexture;

	public FluidInfoArea(IFluidTank tank, Rect2i area, int overlayUMin, int overlayVMin, int overlayWidth,
			int overlayHeight, Identifier overlayTexture) {
		super(area);
		this.tank = tank;
		this.area = area;
		this.overlayUMin = overlayUMin;
		this.overlayVMin = overlayVMin;
		this.overlayWidth = overlayWidth;
		this.overlayHeight = overlayHeight;
		this.overlayTexture = overlayTexture;
	}

	@Override
	public void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip) {
		fillTooltip(tank.getFluid(), tank.getCapacity(), tooltip::add);
	}

	public static void fillTooltip(FluidStack fluid, int tankCapacity, Consumer<Component> tooltip)
	{

		if(!fluid.isEmpty())
			tooltip.accept(fluid.getHoverName());
		else
			tooltip.accept(Component.literal("Empty"));

		if(Minecraft.getInstance().options.advancedItemTooltips&&!fluid.isEmpty())
		{
				tooltip.accept(applyFormat(Component.literal("Fluid Registry: "+BuiltInRegistries.FLUID.getKey(fluid.getFluid())), ChatFormatting.DARK_GRAY));
				tooltip.accept(applyFormat(Component.literal("Density: "+fluid.getFluid().getFluidType().getDensity(fluid)), ChatFormatting.DARK_GRAY));
				tooltip.accept(applyFormat(Component.literal("Temperature: "+fluid.getFluid().getFluidType().getTemperature(fluid)), ChatFormatting.DARK_GRAY));
				tooltip.accept(applyFormat(Component.literal("Viscosity: "+fluid.getFluid().getFluidType().getViscosity(fluid)), ChatFormatting.DARK_GRAY));
		}

		if(tankCapacity > 0)
			tooltip.accept(applyFormat(Component.literal(fluid.getAmount()+"/"+tankCapacity+"mB"), ChatFormatting.GRAY));
		else if(tankCapacity==0)
			tooltip.accept(applyFormat(Component.literal(fluid.getAmount()+"mB"), ChatFormatting.GRAY));
		//don't display amount for tankCapacity < 0, i.e. for ghost fluid stacks
	}
	public static MutableComponent applyFormat(Component component, ChatFormatting... color)
	{
		Style style = component.getStyle();
		for(ChatFormatting format : color)
			style = style.applyFormat(format);
		return component.copy().setStyle(style);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float parTick) {
		FluidStack fluid = tank.getFluid();
		int capacity = tank.getCapacity();
		graphics.pose().pushPose();
		if (!fluid.isEmpty()) {
			int fluidHeight = 0;
			if (capacity > 0) {
				float ratio = (float) fluid.getAmount() / (float) capacity;
				fluidHeight = Math.round(area.getHeight() * ratio);
				fluidHeight = Math.max(1, Math.min(area.getHeight(), fluidHeight));
			}
			if (fluidHeight > 0) {
				MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
				HLRenderHelper.drawRepeatedFluidSpriteGui(buffer, graphics.pose(), fluid, area.getX(),
						area.getY() + area.getHeight() - fluidHeight, area.getWidth(), fluidHeight);
				buffer.endBatch();
			}
		}
		graphics.blit(overlayTexture, area.getX(), area.getY(), overlayUMin, overlayVMin, overlayWidth, overlayHeight);
		graphics.pose().popPose();
	}

}