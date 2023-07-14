package com.vincenthuto.hutoslib.common.data.book;

import com.google.gson.JsonDeserializer;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.guide.TestGuiGuideTitlePage;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookTemplate extends DataTemplate {
	String title, subtitle, coverLoc, overlayLoc, text, icon;

	public BookTemplate() {
		super("book", 0);
	}

	public BookTemplate( String coverLoc, String overlayLoc,String title, String subtitle, String text, String icon) {
		super("book", 0);
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

	public ResourceLocation getOverlayImage() {
		return HLLocHelper.getBySplit(overlayLoc);
	}

	public ResourceLocation getCoverImage() {
		return HLLocHelper.getBySplit(coverLoc);

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
		buf.writeUtf(getCoverLoc());
		buf.writeUtf(getOverlayLoc());
		buf.writeUtf(getTitle());
		buf.writeUtf(getSubtitle());
		buf.writeUtf(getText());
		buf.writeUtf(getIcon());
	}

	@Override
	public BookTemplate deserializeFromJson(FriendlyByteBuf buf) {
		String bookCoverLoc = buf.readUtf();
		String bookOverlayLoc = buf.readUtf();
		String bookTitle = buf.readUtf();
		String bookSubtitle = buf.readUtf();
		String bookText = buf.readUtf();
		String bookIcon = buf.readUtf();

		BookTemplate bookTemp = new BookTemplate(bookCoverLoc,
				bookOverlayLoc,bookTitle, bookSubtitle, bookText, bookIcon);

		return bookTemp;
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

	@Override
	public Screen getPageScreen(int pageNum, BookCodeModel book, BookChapterTemplate chapter) {
		return new TestGuiGuideTitlePage(book);
	}

}