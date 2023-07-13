package com.vincenthuto.hutoslib.common.data.book;

import com.google.gson.JsonDeserializer;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookTemplate extends DataTemplate {
	String title, subtitle, coverLoc, text, icon;

	public BookTemplate() {
		super("book",0);
	}

	public BookTemplate(String title, String subtitle, String coverLoc, String text, String icon) {
		super("book",0);
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

	@Override
	public void serializeToJson(FriendlyByteBuf buf) {

		// Write book json
		buf.writeUtf(getTitle());
		buf.writeUtf(getSubtitle());
		buf.writeUtf(getCoverLoc());
		buf.writeUtf(getText());
		buf.writeUtf(getIcon());
	}

	@Override
	public BookTemplate deserializeFromJson(FriendlyByteBuf buf) {
		String bookTitle = buf.readUtf();
		String bookSubtitle = buf.readUtf();
		String bookCoverLoc = buf.readUtf();
		String bookText = buf.readUtf();
		String bookIcon = buf.readUtf();

		BookTemplate bookTemp = new BookTemplate(bookTitle, bookSubtitle, bookCoverLoc, bookText, bookIcon);

		return bookTemp;
	}

	@Override
	public void renderInGui(GuiGraphics graphics, Font font, int left, int top, int guiWidth, int guiHeight, int mouseX,
			int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JsonDeserializer getTypeAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChapter(String chapterName) {
		// TODO Auto-generated method stub
		
	}

}