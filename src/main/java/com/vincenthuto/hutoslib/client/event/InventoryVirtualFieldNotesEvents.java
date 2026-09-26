package com.vincenthuto.hutoslib.client.event;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.FieldNotes;
import net.minecraft.client.Minecraft;
import com.vincenthuto.hutoslib.client.screen.inventory.VirtualFieldNotesButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
public final class InventoryVirtualFieldNotesEvents {
	private InventoryVirtualFieldNotesEvents() {
	}

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof InventoryScreen screen && !FieldNotes.available(Minecraft.getInstance().player).isEmpty()) {
			event.addListener(new VirtualFieldNotesButton(screen));
		}
	}
}
