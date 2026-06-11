package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.screen.template.EffectTemplateScreen;
import com.vincenthuto.hutoslib.common.template.EffectTemplateType;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemTendrilTemplate extends Item {
	public ItemTendrilTemplate(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (level.isClientSide) {
			EffectTemplateScreen.open(usedHand, EffectTemplateType.TENDRIL, stack);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
