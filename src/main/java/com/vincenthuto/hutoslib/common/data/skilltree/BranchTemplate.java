package com.vincenthuto.hutoslib.common.data.skilltree;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BranchTemplate extends TreeDataTemplate {

	public static final Codec<BranchTemplate> CODEC = RecordCodecBuilder.create(inst -> inst
			.group(Codec.INT.fieldOf("ordinality").forGetter(BranchTemplate::getOrdinality),
					Codec.STRING.fieldOf("texture").forGetter(BranchTemplate::getTexture),
					Codec.STRING.fieldOf("color").forGetter(BranchTemplate::getColor),
					Codec.STRING.fieldOf("title").forGetter(BranchTemplate::getTitle),
					Codec.STRING.fieldOf("subtitle").forGetter(BranchTemplate::getSubtitle),
					Codec.STRING.fieldOf("icon").forGetter(BranchTemplate::getIcon))
			.apply(inst, BranchTemplate::new));
	public static final PSerializer<BranchTemplate> SERIALIZER = PSerializer.fromCodec("branch", CODEC);

	String color, title, subtitle, icon, texture;

	List<TreeDataTemplate> skills;

	public BranchTemplate(int ordinality, String texture, String color, String title, String subtitle, String icon) {
		super(ordinality);
		this.texture = texture;
		this.color = color;
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
	}

	public List<TreeDataTemplate> getSkills() {
		return skills;
	}

	public void setSkills(List<TreeDataTemplate> skills) {
		this.skills = skills;
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

	public ParticleColor getBranchRGB() {
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

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public ResourceLocation getTextureLocation() {

		if (texture != null && texture.contains(":")) {
			String[] split = texture.split(":");
			ResourceLocation rl = new ResourceLocation(split[0], split[1]);
			if (rl != null) {
				return rl;
			}
		}
		return HutosLib.rloc(texture);
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


	@SuppressWarnings("unused")
	public int getSkillCount() {
		int count = 0;
		if (getSkills() != null) {
			for (TreeDataTemplate skill : getSkills()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "Branch Title: " + title + ", Has " + this.skills != null ? this.getSkillCount() + "" : "0" + " skills.";
	}

	@Override
	public void setBranch(String branchName) {

	}

	@Override
	public void getSkillScreen(int skillNum, TreeCodeModel book, BranchTemplate branch) {
		//HLGuiGuideSkillTOC.openScreenViaItem(skillNum, book, branch);
	}

	@Override
	public PSerializer<? extends TreeDataTemplate> getSerializer() {
		return SERIALIZER;
	}

}