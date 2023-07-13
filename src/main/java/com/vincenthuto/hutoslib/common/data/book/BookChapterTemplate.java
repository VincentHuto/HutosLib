package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import com.google.gson.JsonDeserializer;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookChapterTemplate extends DataTemplate {

	int chapterOrder;
	String color, title, subtitle, icon;
	List<DataTemplate> pages;

	public BookChapterTemplate() {
		super("chapter");
	}

	public BookChapterTemplate(int chapterOrder, String color, String title, String subtitle, String icon,
			List<DataTemplate> pages) {
		super("chapter");
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

	public List<DataTemplate> getPages() {
		return pages;
	}

	public void setPages(List<DataTemplate> pages2) {
		this.pages = pages2;
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
			for (DataTemplate page : getPages()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "Chapter Title: " + title + ", Has " + this.pages != null ? this.getPageCount() +"" : "0" + " pages.";
	}

	@Override
	public void serializeToJson(FriendlyByteBuf buf) {
		buf.writeInt(getChapterOrder());
		buf.writeUtf(getColor());
		buf.writeUtf(getTitle());
		buf.writeUtf(getSubtitle());
		buf.writeUtf(getIcon());
	}

	@Override
	public BookChapterTemplate deserializeFromJson(FriendlyByteBuf buf) {
		int chapterNum = buf.readInt();
		String chapterColor = buf.readUtf();
		String chapterTitle = buf.readUtf();
		String chapterSubtitle = buf.readUtf();
		String chapterIcon = buf.readUtf();


		BookChapterTemplate chapterTemp = new BookChapterTemplate(chapterNum, chapterColor, chapterTitle,
				chapterSubtitle, chapterIcon, null);
		return chapterTemp;
	}

	@Override
	public void renderInGui(GuiGraphics graphics, Font font, int left, int top, int guiWidth, int guiHeight, int mouseX,
			int mouseY, float partialTicks) {
		
	}

	@Override
	public JsonDeserializer getTypeAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChapter(String chapterName) {
		
	}

}