package com.vincenthuto.hutoslib.common.data.book;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public interface IPageTemplate {

	public void setChapter(String chapter);

	public String getTitle();
	public String getText();

	public ResourceLocation getTextureLocation();

	public String getSubtitle();
	
	public String getIcon();

	public default ItemStack getIconItem() {
		if (getIcon() != null && getIcon().contains(":")) {
			String[] split = getIcon().split(":");
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
			if (item != null) {
				return new ItemStack(item);
			}
		}
		return ItemStack.EMPTY;
	}

}
