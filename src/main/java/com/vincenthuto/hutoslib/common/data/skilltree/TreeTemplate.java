package com.vincenthuto.hutoslib.common.data.skilltree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class TreeTemplate extends TreeDataTemplate {

	public static final Codec<TreeTemplate> CODEC = RecordCodecBuilder
			.create(inst -> inst
					.group(Codec.STRING.fieldOf("coverLoc").forGetter(TreeTemplate::getCoverLoc),
							Codec.STRING.fieldOf("overlayLoc").forGetter(TreeTemplate::getOverlayLoc),
							Codec.STRING.fieldOf("title").forGetter(TreeTemplate::getTitle),
							Codec.STRING.fieldOf("subtitle").forGetter(TreeTemplate::getSubtitle),
							Codec.STRING.fieldOf("text").forGetter(TreeTemplate::getText),
							Codec.STRING.fieldOf("icon").forGetter(TreeTemplate::getIcon))
					.apply(inst, TreeTemplate::new));
	public static final PSerializer<TreeTemplate> SERIALIZER = PSerializer.fromCodec("skilltree", CODEC);

	String title, subtitle, coverLoc, overlayLoc, text, icon;

	public TreeTemplate(String coverLoc, String overlayLoc, String title, String subtitle, String text, String icon) {
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
	public void setBranch(String branchName) {
	}

	@Override
	public void getSkillScreen(int skillNum, TreeCodeModel book, BranchTemplate branch) {
		//HLGuiGuideTitleSkill.openScreenViaItem(skillNum, book, branch);
	}

	@Override
	public PSerializer<? extends TreeDataTemplate> getSerializer() {
		return SERIALIZER;
	}

}