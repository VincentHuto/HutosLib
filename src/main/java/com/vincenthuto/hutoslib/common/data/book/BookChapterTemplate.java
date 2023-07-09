package com.vincenthuto.hutoslib.common.data.book;

import java.util.Arrays;
import java.util.List;

import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookChapterTemplate implements DataTemplate {

	int chapterOrder;
	String color, title, subtitle, icon;
	List<BookPageTemplate> pages;

	public BookChapterTemplate(int chapterOrder, String color, String title, String subtitle, String icon,
			List<BookPageTemplate> pages) {
		this.chapterOrder = chapterOrder;
		this.color = color;
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
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

	public int getChapterOrder() {
		return chapterOrder;
	}

	public void setChapterOrder(int chapterOrder) {
		this.chapterOrder = chapterOrder;
	}

	public List<BookPageTemplate> getPages() {
		return pages;
	}

	public void setPages(List<BookPageTemplate> pages) {
		this.pages = pages;
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

	public int getPageCount() {
		int count = 0;
		if (getPages() != null) {
			for (BookPageTemplate page : getPages()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "Chapter Title: " + title + ", Has " + pages.size() + " pages.";
	}

}