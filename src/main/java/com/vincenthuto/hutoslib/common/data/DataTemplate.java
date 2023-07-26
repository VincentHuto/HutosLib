package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.shadow.TypeKeyed.TypeKeyedBase;

import net.minecraft.resources.ResourceLocation;

public abstract class DataTemplate extends TypeKeyedBase<DataTemplate> {
	ResourceLocation location;
	int ordinality;

	// So GSON.toJson doesnt like nonprimatives so imma split this like Im doing the
	// icon item thing
	public DataTemplate(int ordinality) {
		this.ordinality = ordinality;
	}

	public int getOrdinality() {
		return ordinality;
	}

	public void setOrdinality(int ordinality) {
		this.ordinality = ordinality;
	}

	public abstract void setChapter(String chapterName);

	public abstract void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter);

}
