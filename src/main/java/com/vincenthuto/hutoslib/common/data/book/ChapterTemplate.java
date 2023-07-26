package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuidePageTOC;
import com.vincenthuto.hutoslib.common.data.DataTemplate;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ChapterTemplate extends DataTemplate {

	public static final Codec<ChapterTemplate> CODEC = RecordCodecBuilder.create(inst -> inst
			.group(Codec.INT.fieldOf("ordinality").forGetter(ChapterTemplate::getOrdinality),
					Codec.STRING.fieldOf("texture").forGetter(ChapterTemplate::getTexture),
					Codec.STRING.fieldOf("color").forGetter(ChapterTemplate::getColor),
					Codec.STRING.fieldOf("title").forGetter(ChapterTemplate::getTitle),
					Codec.STRING.fieldOf("subtitle").forGetter(ChapterTemplate::getSubtitle),
					Codec.STRING.fieldOf("icon").forGetter(ChapterTemplate::getIcon))
			.apply(inst, ChapterTemplate::new));
	public static final PSerializer<ChapterTemplate> SERIALIZER = PSerializer.fromCodec("chapter", CODEC);

	String color, title, subtitle, icon, texture;

	List<DataTemplate> pages;

	public ChapterTemplate(int ordinality, String texture, String color, String title, String subtitle, String icon) {
		super(ordinality);
		this.texture = texture;
		this.color = color;
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
	}

	public List<DataTemplate> getPages() {
		return pages;
	}

	public void setPages(List<DataTemplate> pages) {
		this.pages = pages;
	}

	public ItemStack getIconItem() {
		if (color != null && icon.contains(",")) {
			String[] split = icon.split(",");
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
			if (item != null) {
				return new ItemStack(item);
			}

		}
		return ItemStack.EMPTY;
	}

	public ParticleColor getChapterRGB() {
		if (color != null && color.contains(",")) {
			String[] split = color.split(",");
			ParticleColor pc = new ParticleColor(255 * Float.parseFloat(split[0]), 255 * Float.parseFloat(split[1]),
					255 * Float.parseFloat(split[2]));

			if (pc != null) {
				return pc;
			}

		}
		return new ParticleColor(1, 1, 1);
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public ResourceLocation getTextureLocation() {

		if (texture != null && texture.contains(":")) {
			String[] split = texture.split(":");
			ResourceLocation rl = new ResourceLocation(split[0], split[1]);
			if (rl != null) {
				return rl;
			}
		}
		return HutosLib.rloc(texture);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


	@SuppressWarnings("unused")
	public int getPageCount() {
		int count = 0;
		if (getPages() != null) {
			for (DataTemplate page : getPages()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "Chapter Title: " + title + ", Has " + this.pages != null ? this.getPageCount() + "" : "0" + " pages.";
	}

	@Override
	public void setChapter(String chapterName) {

	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		HLGuiGuidePageTOC.openScreenViaItem(pageNum, book, chapter);
	}

	@Override
	public PSerializer<? extends DataTemplate> getSerializer() {
		return SERIALIZER;
	}

}