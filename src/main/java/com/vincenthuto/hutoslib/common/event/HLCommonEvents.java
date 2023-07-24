package com.vincenthuto.hutoslib.common.event;

import java.util.Collections;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.ResourceReloadHandler;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketSyncBookData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

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
