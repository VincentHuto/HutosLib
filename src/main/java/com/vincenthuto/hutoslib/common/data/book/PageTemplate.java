package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.common.util.HLResourceUtils;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class PageTemplate extends BookDataTemplate {

	public static final Codec<PageTemplate> CODEC = RecordCodecBuilder
			.create(inst -> inst
					.group(Codec.INT.fieldOf("ordinality").forGetter(PageTemplate::getOrdinality),
							Codec.STRING.fieldOf("texture").forGetter(PageTemplate::getTexture),
							Codec.STRING.fieldOf("title").forGetter(PageTemplate::getTitle),
							Codec.STRING.fieldOf("subtitle").forGetter(PageTemplate::getSubtitle),
							Codec.STRING.fieldOf("text").forGetter(PageTemplate::getText),
							Codec.STRING.fieldOf("icon").forGetter(PageTemplate::getIcon),
							Codec.STRING.optionalFieldOf("requiresEntry", "").forGetter(PageTemplate::getRequiresEntry))
					.apply(inst, PageTemplate::new));
	public static final PSerializer<PageTemplate> SERIALIZER = PSerializer.fromCodec("page", CODEC);

	String title, chapter, subtitle, text, icon, texture;
	/** Entry ID that must be unlocked before this page is visible. Empty string = always visible. */
	String requiresEntry = "";

	public PageTemplate() {
		super(0);
	}

	public PageTemplate(int ordinality, String texture, String title, String subtitle, String text, String icon) {
		this(ordinality, texture, title, subtitle, text, icon, "");
	}

	public PageTemplate(int ordinality, String texture, String title, String subtitle, String text, String icon,
			String requiresEntry) {
		super(ordinality);
		this.texture = texture;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.icon = icon;
		this.requiresEntry = requiresEntry != null ? requiresEntry : "";
	}

	public ItemStack getIconItem() {
		if (icon != null && icon.contains(":")) {
			String[] split = icon.split(":", 2);
			if (split.length < 2) {
				return ItemStack.EMPTY;
			}
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(split[0], split[1]));
			if (item != null) {
				return new ItemStack(item);
			}
		}
		return ItemStack.EMPTY;
	}

	public Identifier getTextureLocation() {

		return HLResourceUtils.getBySplit(texture);

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

	public String getRequiresEntry() {
		return requiresEntry != null ? requiresEntry : "";
	}

	public void setRequiresEntry(String requiresEntry) {
		this.requiresEntry = requiresEntry != null ? requiresEntry : "";
	}

	@Override
	public String toString() {
		return "Page number: " + getOrdinality() + ", Title: " + title ;
	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
	}

	@Override
	public PSerializer<? extends BookDataTemplate> getSerializer() {
		return SERIALIZER;
	}
}
