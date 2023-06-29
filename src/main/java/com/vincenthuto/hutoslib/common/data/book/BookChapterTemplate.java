package com.vincenthuto.hutoslib.common.data.book;

import java.util.Arrays;

import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookChapterTemplate implements DataTemplate {

	String color, chapter, title, subtitle, icon;

	public BookChapterTemplate(String color, String chapter, String title, String subtitle, String icon) {
		this.color = color;
		this.chapter = chapter;
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
	}

	public ItemStack getIconItem() {
		if (icon != null && icon.contains(":")) {
			String[] split = icon.split(":");
			System.out.println(Arrays.toString(split));
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
			if (item != null) {
				return new ItemStack(item);
			}

		}
		return ItemStack.EMPTY;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	
}