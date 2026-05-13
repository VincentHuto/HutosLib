package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.HLGuideBookClient;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemHLGuideBook extends ItemGuideBook {
	public ItemHLGuideBook(Properties prop, Identifier texture) {
		super(prop, texture);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			HLGuideBookClient.open(this, player);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
