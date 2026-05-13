package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;


public class CraftingRecipeTemplate extends PageTemplate {
	public static final Codec<CraftingRecipeTemplate> CODEC = RecordCodecBuilder.create(inst -> inst
			.group(Codec.INT.fieldOf("ordinality").forGetter(PageTemplate::getOrdinality),
					Codec.STRING.fieldOf("texture").forGetter(PageTemplate::getTexture),
					Codec.STRING.fieldOf("title").forGetter(PageTemplate::getTitle),
					Codec.STRING.fieldOf("subtitle").forGetter(PageTemplate::getSubtitle),
					Codec.STRING.fieldOf("text").forGetter(PageTemplate::getText),
					Codec.STRING.fieldOf("icon").forGetter(PageTemplate::getIcon))
			.apply(inst, CraftingRecipeTemplate::new));
	public static final PSerializer<CraftingRecipeTemplate> SERIALIZER = PSerializer.fromCodec("craftingpage", CODEC);

	public CraftingRecipeTemplate(int ordinality, String texture, String title, String subtitle, String text,
			String icon) {
		super(ordinality, texture, title, subtitle, text, icon);
	}

	public static int[][] getCoordinates(int position) {
		// Check if the position is within the valid range
		if (position < 0 || position > 8) {
			System.out.println("Invalid input. Please provide a number between 0 and 8.");
			return null;
		}

		int i = position / 3; // Row index
		int j = position % 3; // Column index

		return new int[][] { { i, j } };
	}

	private static int getCraftingIndex(int i, int width, int height) {
		int index;
		if (width == 1) {
			if (height == 3) {
				index = (i * 3) + 1;
			} else if (height == 2) {
				index = (i * 3) + 1;
			} else {
				index = 4;
			}
		} else if (height == 1) {
			index = i + 3;
		} else if (width == 2) {
			index = i;
			if (i > 1) {
				index++;
				if (i > 3) {
					index++;
				}
			}
		} else if (height == 2) {
			index = i + 3;
		} else {
			index = i;
		}
		return index;
	}


	@Override
	public String toString() {
		return "Page number: " + getOrdinality() + ", Title: " + title;
	}

	@Override
	public PSerializer<? extends BookDataTemplate> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
	}

}
