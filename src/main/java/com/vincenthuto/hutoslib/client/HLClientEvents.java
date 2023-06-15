package com.vincenthuto.hutoslib.client;

import org.lwjgl.glfw.GLFW;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.render.layer.LayerArmBanner;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HLClientEvents {
	
	@SubscribeEvent
	public static void skybox(RenderLevelStageEvent event) {

		BoltRenderer.onWorldRenderLast(event.getPartialTick(), event.getPoseStack());

	}
	
	public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

	@SubscribeEvent
	public static void handleKeys(TickEvent.ClientTickEvent ev) {
		if (ev.phase != TickEvent.Phase.START)
			return;

		Minecraft mc = Minecraft.getInstance();
		while (HLClientEvents.OPEN_BANNER_SLOT_KEYBIND.consumeClick()) {
			if (mc.screen == null) {
				HLPacketHandler.MAINCHANNEL.sendToServer(new PacketOpenBanner());
			}
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

		@SubscribeEvent
		public static void initKeybinds(RegisterKeyMappingsEvent ev) {
			ev.register(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot", GLFW.GLFW_KEY_V,
					"key.armbanner.category"));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static <T extends LivingEntity, M extends HumanoidModel<T>, R extends LivingEntityRenderer<T, M>> void addLayerToEntity(
				EntityRenderersEvent.AddLayers event, EntityType<? extends T> entityType) {
			R renderer = event.getRenderer(entityType);
			if (renderer != null)
				renderer.addLayer(new LayerArmBanner(renderer));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName) {
			EntityRenderer<? extends Player> render = event.getSkin(skinName);
			if (render instanceof LivingEntityRenderer livingRenderer) {
				livingRenderer.addLayer(new LayerArmBanner<>(livingRenderer));
			}
		}

		@SubscribeEvent
		public static void constructLayers(EntityRenderersEvent.AddLayers event) {

			addLayerToEntity(event, EntityType.ARMOR_STAND);
			addLayerToEntity(event, EntityType.ZOMBIE);
			addLayerToEntity(event, EntityType.SKELETON);
			addLayerToEntity(event, EntityType.HUSK);
			addLayerToEntity(event, EntityType.DROWNED);
			addLayerToEntity(event, EntityType.STRAY);
			addLayerToPlayerSkin(event, "default");
			addLayerToPlayerSkin(event, "slim");

		}

	}

}
