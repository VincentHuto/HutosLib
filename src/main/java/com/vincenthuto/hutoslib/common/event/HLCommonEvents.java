package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HLCommonEvents {

	@SubscribeEvent
	public static void registerResourceListener(AddReloadListenerEvent e) {
		e.addListener(new ResourceReloadHandler());

	}

	@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

	}

}
