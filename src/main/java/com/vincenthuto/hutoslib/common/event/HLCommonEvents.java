package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;

import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.NEOFORGE)
public class HLCommonEvents {

	@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

	}

}
