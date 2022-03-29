package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHLGuideBook extends ItemGuideBook {

	public ItemHLGuideBook(Properties prop, ResourceLocation texture) {
		super(prop, texture);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player p_41433_, InteractionHand p_41434_) {
		if (lvl.isClientSide) {
			HutosLib.proxy.openGuideGui();
		}
		return super.use(lvl, p_41433_, p_41434_);
	}

}
