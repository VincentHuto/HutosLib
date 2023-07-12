package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class DataTemplate {
	String processor;

	// So GSON.toJson doesnt like nonprimatives so imma split this like Im doing the
	// icon item thing
	public DataTemplate(String processor2) {
		this.processor = processor2;
	}

	public String getProcessor() {
		return processor;
	}

	public ResourceLocation getProcessorKey() {

		if (processor != null && processor.contains(":")) {
			String[] split = processor.split(":");
			ResourceLocation processorKey = new ResourceLocation(split[0], split[1]);
			if (processorKey != null) {
				return processorKey;
			}
		}
		return HutosLib.rloc(processor);
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public abstract void serializeToJson(FriendlyByteBuf buf);

	public abstract DataTemplate deserializeFromJson(FriendlyByteBuf buf);

	public abstract void renderInGui(GuiGraphics graphics, Font font, int left, int top, int guiWidth, int guiHeight,
			int mouseX, int mouseY, float partialTicks);

}
