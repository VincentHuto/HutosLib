package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuidePage;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PageTemplate extends BookDataTemplate {

	public static final Codec<PageTemplate> CODEC = RecordCodecBuilder
			.create(inst -> inst
					.group(Codec.INT.fieldOf("ordinality").forGetter(PageTemplate::getOrdinality),
							Codec.STRING.fieldOf("texture").forGetter(PageTemplate::getTexture),
							Codec.STRING.fieldOf("title").forGetter(PageTemplate::getTitle),
							Codec.STRING.fieldOf("subtitle").forGetter(PageTemplate::getSubtitle),
							Codec.STRING.fieldOf("text").forGetter(PageTemplate::getText),
							Codec.STRING.fieldOf("icon").forGetter(PageTemplate::getIcon))
					.apply(inst, PageTemplate::new));
	public static final PSerializer<PageTemplate> SERIALIZER = PSerializer.fromCodec("page", CODEC);

	String title, chapter, subtitle, text, icon, texture;

	public PageTemplate() {
		super(0);
	}

	public PageTemplate(int ordinality, String texture, String title, String subtitle, String text, String icon) {
		super(ordinality);
		this.texture = texture;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.icon = icon;

	}

	public ItemStack getIconItem() {
		if (icon != null && icon.contains(":")) {
			String[] split = icon.split(":");
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
			if (item != null) {
				return new ItemStack(item);
			}
		}
		return ItemStack.EMPTY;
	}

	public ResourceLocation getTextureLocation() {

		return HLLocHelper.getBySplit(texture);

	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "Page number: " + getOrdinality() + ", Title: " + title ;
	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		HLGuiGuidePage.openScreenViaItem(pageNum, book, chapter);
	}

	@Override
	public PSerializer<? extends BookDataTemplate> getSerializer() {
		return SERIALIZER;
	}
}