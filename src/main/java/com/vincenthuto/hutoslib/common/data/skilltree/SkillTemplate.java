package com.vincenthuto.hutoslib.common.data.skilltree;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SkillTemplate extends TreeDataTemplate {

	public static final Codec<SkillTemplate> CODEC = RecordCodecBuilder.create(inst -> inst
			.group(Codec.INT.fieldOf("ordinality").forGetter(SkillTemplate::getOrdinality),
					Codec.STRING.fieldOf("texture").forGetter(SkillTemplate::getTexture),
					Codec.STRING.fieldOf("title").forGetter(SkillTemplate::getTitle),
					Codec.STRING.fieldOf("subtitle").forGetter(SkillTemplate::getSubtitle),
					Codec.STRING.fieldOf("text").forGetter(SkillTemplate::getText),
					Codec.STRING.fieldOf("icon").forGetter(SkillTemplate::getIcon),
					Codec.INT.listOf().fieldOf("parents").forGetter(SkillTemplate::getParentIds),
					Codec.INT.listOf().fieldOf("siblings").forGetter(SkillTemplate::getSiblingIds),
					Codec.INT.listOf().fieldOf("children").forGetter(SkillTemplate::getChildrenIds))
			.apply(inst, SkillTemplate::new));
	public static final PSerializer<SkillTemplate> SERIALIZER = PSerializer.fromCodec("skill", CODEC);

	String title, branch, subtitle, text, icon, texture;
	List<Integer> parentIds, siblingIds, childrenIds;

	public SkillTemplate() {
		super(0);
	}

	public SkillTemplate(int ordinality, String texture, String title, String subtitle, String text, String icon,
			List<Integer> parentIds, List<Integer> siblingIds, List<Integer> childrenIds) {
		super(ordinality);
		this.texture = texture;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.icon = icon;
		this.parentIds = parentIds;
		this.siblingIds = siblingIds;
		this.childrenIds = childrenIds;

	}

	public List<Integer> getChildrenIds() {
		return childrenIds;
	}

	public void setChildrenIds(List<Integer> childrenIds) {
		this.childrenIds = childrenIds;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
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
		return "Skill number: " + getOrdinality() + ", Title: " + title;
	}

	@Override
	public void getSkillScreen(int skillNum, TreeCodeModel tree, BranchTemplate branch) {
		// HLGuiGuideSkill.openScreenViaItem(skillNum, tree, branch);
	}

	@Override
	public PSerializer<? extends TreeDataTemplate> getSerializer() {
		return SERIALIZER;
	}

	public List<Integer> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<Integer> parentIds) {
		this.parentIds = parentIds;
	}

	public List<Integer> getSiblingIds() {
		return siblingIds;
	}

	public void setSiblingIds(List<Integer> siblingIds) {
		this.siblingIds = siblingIds;
	}

}