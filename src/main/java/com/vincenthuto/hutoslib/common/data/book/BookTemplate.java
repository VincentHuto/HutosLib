package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.common.util.HLResourceUtils;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class BookTemplate extends BookDataTemplate {

	public static final Codec<BookTemplate> CODEC = RecordCodecBuilder
			.create(inst -> inst
					.group(Codec.STRING.fieldOf("coverLoc").forGetter(BookTemplate::getCoverLoc),
							Codec.STRING.fieldOf("overlayLoc").forGetter(BookTemplate::getOverlayLoc),
							Codec.STRING.fieldOf("title").forGetter(BookTemplate::getTitle),
							Codec.STRING.fieldOf("subtitle").forGetter(BookTemplate::getSubtitle),
							Codec.STRING.fieldOf("text").forGetter(BookTemplate::getText),
							Codec.STRING.fieldOf("icon").forGetter(BookTemplate::getIcon))
					.apply(inst, BookTemplate::new));
	public static final PSerializer<BookTemplate> SERIALIZER = PSerializer.fromCodec("book", CODEC);

	String title, subtitle, coverLoc, overlayLoc, text, icon;

	public BookTemplate(String coverLoc, String overlayLoc, String title, String subtitle, String text, String icon) {
		super(0);
		this.coverLoc = coverLoc;
		this.overlayLoc = overlayLoc;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.icon = icon;
	}

	public String getOverlayLoc() {
		return overlayLoc;
	}

	public void setOverlayLoc(String overlayLoc) {
		this.overlayLoc = overlayLoc;
	}

	public void setCoverLoc(String coverLoc) {
		this.coverLoc = coverLoc;
	}

	public String getCoverLoc() {
		return coverLoc;
	}

	public Identifier getOverlayImage() {
		return HLResourceUtils.getBySplit(overlayLoc);
	}

	public Identifier getCoverImage() {
		return HLResourceUtils.getBySplit(coverLoc);

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
	public void setChapter(String chapterName) {
	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
	}

	@Override
	public PSerializer<? extends BookDataTemplate> getSerializer() {
		return SERIALIZER;
	}

}
