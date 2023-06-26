package com.vincenthuto.hutoslib.common.event;

import java.util.Arrays;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public record BookPageTemplate(int page, String category, String title, String subtitle, String text, String icon) {

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
}