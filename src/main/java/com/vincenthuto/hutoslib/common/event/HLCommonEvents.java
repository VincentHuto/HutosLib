package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.recipe.ArmBannerCraftRecipe;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = HutosLib.MOD_ID)
public final class HLCommonEvents {

	private HLCommonEvents() {
		// Utility class
	}

	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		ArmBannerCraftRecipe.copyBannerComponentsFromCraftingContainer(event.getInventory(), event.getCrafting());
	}
}
