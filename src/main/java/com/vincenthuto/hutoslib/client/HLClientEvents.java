package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.render.layer.LayerArmBanner;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
public class HLClientEvents {

    public static final KeyMapping.Category ARMBANNER_CATEGORY =
        new KeyMapping.Category(Identifier.fromNamespaceAndPath("hutoslib", "armbanner"));

    @SubscribeEvent
    public static void skybox(RenderLevelStageEvent event) {
        BoltRenderer.onWorldRenderLast(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), event.getPoseStack());
    }

    public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

    @SubscribeEvent
    public static void handleKeys(ClientTickEvent.Pre ev) {
        Minecraft mc = Minecraft.getInstance();
        while (HLClientEvents.OPEN_BANNER_SLOT_KEYBIND.consumeClick()) {
            if (mc.screen == null) {
                ClientPacketDistributor.sendToServer(new PacketOpenBanner());
            }
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void initKeybinds(RegisterKeyMappingsEvent ev) {
            ev.registerCategory(ARMBANNER_CATEGORY);
            ev.register(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot", GLFW.GLFW_KEY_V,
                ARMBANNER_CATEGORY));
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void addLayerToEntity(EntityRenderersEvent.AddLayers event, EntityType<?> entityType) {
            var renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new LayerArmBanner<>(livingRenderer));
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerModelType skinModel) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skinModel);
            if (renderer != null) {
                renderer.addLayer(new LayerArmBanner<>(renderer));
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
            addLayerToPlayerSkin(event, PlayerModelType.WIDE);
            addLayerToPlayerSkin(event, PlayerModelType.SLIM);
        }
    }
}
