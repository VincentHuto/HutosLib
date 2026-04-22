package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class HlClientTickHandler {

public static int ticksWithLexicaOpen = 0;
public static int pageFlipTicks = 0;
public static int ticksInGame = 0;
public static float partialTicks = 0;
public static float delta = 0;
public static float total = 0;

private static void calcDelta() {
float oldTotal = total;
total = ticksInGame + partialTicks;
delta = total - oldTotal;
}

@SubscribeEvent
public static void clientTickEnd(ClientTickEvent.Post event) {
Screen gui = Minecraft.getInstance().screen;
if (gui == null || !gui.isPauseScreen()) {
ticksInGame++;
partialTicks = 0;

Player player = Minecraft.getInstance().player;
if (player != null) {
}
}
calcDelta();
}

@SubscribeEvent
public static void renderFramePre(RenderFrameEvent.Pre event) {
partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
}

@SubscribeEvent
public static void renderFramePost(RenderFrameEvent.Post event) {
calcDelta();
}

private HlClientTickHandler() {
}
}
