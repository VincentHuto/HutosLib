package com.vincenthuto.hutoslib.client;

import org.lwjgl.glfw.GLFW;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = Bus.FORGE)
public class HLForgeEvents {

	public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

	public static void initKeybinds() {
		ClientRegistry.registerKeyBinding(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot",
				GLFW.GLFW_KEY_V, "key.armbanner.category"));
	}

	@SubscribeEvent
	public static void handleKeys(TickEvent.ClientTickEvent ev) {
		if (ev.phase != TickEvent.Phase.START)
			return;

		Minecraft mc = Minecraft.getInstance();
		while (OPEN_BANNER_SLOT_KEYBIND.consumeClick()) {
			if (mc.screen == null) {
				HLPacketHandler.MAINCHANNEL.sendToServer(new PacketOpenBanner());
			}
		}
	}

	@SubscribeEvent
	public static void skybox(RenderLevelLastEvent event) {

		BoltRenderer.onWorldRenderLast(event.getPartialTick(), event.getPoseStack());

	}

}
