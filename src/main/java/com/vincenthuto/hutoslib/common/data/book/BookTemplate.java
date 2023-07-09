package com.vincenthuto.hutoslib.common.data.book;

import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookTemplate implements DataTemplate {
	String title, subtitle, coverLoc, text, icon;

	public BookTemplate(String title, String subtitle, String coverLoc, String text, String icon) {
		this.title = title;
		this.subtitle = subtitle;
		this.coverLoc = coverLoc;
		this.text = text;
		this.icon = icon;
	}
	
	public void setCoverLoc(String coverLoc) {
		this.coverLoc = coverLoc;
	}
	
	public String getCoverLoc() {
		return coverLoc;
	}

	public ResourceLocation getCoverImage() {
		if (coverLoc != null && coverLoc.contains(":")) {
			String[] split = coverLoc.split(":");
			ResourceLocation cover = new ResourceLocation(split[0], split[1]);
			if (cover != null) {
				return cover;
			}
		}
		return null;
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

}