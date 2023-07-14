package com.vincenthuto.hutoslib.common.data.book;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.common.data.DataTemplate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookPageTemplate extends DataTemplate {
	String title, chapter, subtitle, text, icon, texture;

	public BookPageTemplate(String processor) {
		super(processor, 0);
	}

	public BookPageTemplate(String processor, int ordinality, String texture, String title, String subtitle,
			String text, String icon) {
		super(processor, ordinality);
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
		return "Page number: " + getOrdinality() + ", Title: " + title + ", Processor: " + getProcessor();
	}

	@Override
	public void serializeToJson(FriendlyByteBuf buf) {
		buf.writeUtf(getProcessor());
		buf.writeInt(getOrdinality());
		buf.writeUtf(getTexture());
		buf.writeUtf(getTitle());
		buf.writeUtf(getSubtitle());
		buf.writeUtf(getText());
		buf.writeUtf(getIcon());
	}

	@Override
	public BookPageTemplate deserializeFromJson(FriendlyByteBuf buf) {
		String pagePK = buf.readUtf();
		int pageNum = buf.readInt();
		String pageTexture = buf.readUtf();
		String pageTitle = buf.readUtf();
		String pageSubtitle = buf.readUtf();
		String pageText = buf.readUtf();
		String pageIcon = buf.readUtf();
		BookPageTemplate pageTemp = new BookPageTemplate(pagePK, pageNum, pageTexture, pageTitle, pageSubtitle,
				pageText, pageIcon);
		return pageTemp;
	}

	@Override
	public void renderInGui(GuiGraphics graphics, Font font, int left, int top, int guiWidth, int guiHeight, int mouseX,
			int mouseY, double dragUpDown, double dragLeftRight, float partialTicks) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		graphics.renderFakeItem(getIconItem(), left + guiWidth - 32, top + guiHeight - 220);
		matrixStack.popPose();
		if (!getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(getTitle())), left - guiWidth + 180,
					top + guiHeight - 220, 165, 0xffffff, true);
		}
		if (!getSubtitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(getSubtitle())), left - guiWidth + 180,
					top + guiHeight - 210, 165, 0xffffff, true);
		}

		if (!getText().isEmpty() && getSubtitle().isEmpty() && getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(getText())), left - guiWidth + 180,
					top + guiHeight - 220, 160, 0xffffff, true);
		} else if (!getText().isEmpty() && getSubtitle().isEmpty() || getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(getText())), left - guiWidth + 180,
					top + guiHeight - 200, 160, 0xffffff, true);
		} else if (!getText().isEmpty() && !getSubtitle().isEmpty() && !getTitle().isEmpty()) {
			HLGuiUtils.drawMaxWidthString(font, Component.literal(I18n.get(getText())), left - guiWidth + 180,
					top + guiHeight - 190, 160, 0xffffff, true);
		}
	}

	@Override
	public JsonDeserializer getTypeAdapter() {
		return new BookPageTemplateDeserializer();
	}

	class BookPageTemplateDeserializer implements JsonDeserializer<BookPageTemplate> {
		@Override
		public BookPageTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Gson gson = new Gson();
			HashMap<String, Object> map = gson.fromJson(json, HashMap.class);
			// System.out.println("JSON" +map);
			String processor = (String) map.get("processor");
			double ordinalityd = (double) map.get("ordinality");
			int ordinality = (int) ordinalityd;
			String tex = (String) map.get("texture");
			String title = (String) map.get("title");
			String subtitle = (String) map.get("subtitle");
			String text = (String) map.get("text");
			String icon = (String) map.get("icon");

			return new BookPageTemplate(processor, ordinality, tex, title, subtitle, text, icon);
		}
	}
}