package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HLCommonEvents {

	@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

	}

}
