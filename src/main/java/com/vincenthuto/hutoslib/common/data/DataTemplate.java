package com.vincenthuto.hutoslib.common.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.common.data.book.BookChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
		return HLLocHelper.getBySplit(processor);
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

	public class PayloadJsonDeserializer implements JsonDeserializer<DataTemplate> {
		@Override
		public DataTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return null;
		}
	}

	public abstract JsonDeserializer getTypeAdapter();

	public abstract void setChapter(String chapterName);
	
	public abstract Screen getPageScreen(int pageNum, BookCodeModel book, BookChapterTemplate chapter);
}
