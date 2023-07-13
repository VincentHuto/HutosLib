package com.vincenthuto.hutoslib.common.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class DataTemplate {
	int ordinality;
	String processor;

	// So GSON.toJson doesnt like nonprimatives so imma split this like Im doing the
	// icon item thing
	public DataTemplate(String processor, int ordinality) {
		this.processor = processor;
		this.ordinality = ordinality;
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

	public int getOrdinality() {
		return ordinality;
	}

	public void setOrdinality(int ordinality) {
		this.ordinality = ordinality;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public abstract void serializeToJson(FriendlyByteBuf buf);

	public abstract DataTemplate deserializeFromJson(FriendlyByteBuf buf);

	public abstract void renderInGui(GuiGraphics graphics, Font font, int left, int top, int guiWidth, int guiHeight,
			int mouseX, int mouseY, float partialTicks);

	public class PayloadJsonDeserializer implements JsonDeserializer<DataTemplate> {
		@Override
		public DataTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}
	}

	public abstract JsonDeserializer getTypeAdapter();

	public abstract void setChapter(String chapterName);
}
