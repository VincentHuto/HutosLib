package com.vincenthuto.hutoslib.client;

import org.lwjgl.glfw.GLFW;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

	public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

	public static void initKeybinds() {
		ClientRegistry.registerKeyBinding(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot",
				GLFW.GLFW_KEY_V, "key.toolbanner.category"));
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

}
