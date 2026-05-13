package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.render.item.RenderItemArmBanner;
import com.vincenthuto.hutoslib.client.render.item.RenderItemGuideBook;
import com.vincenthuto.hutoslib.client.render.layer.LayerArmBanner;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
public class HLClientEvents {

    public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getPlayer() != null) {
            ItemGuideBook.clearState(event.getPlayer().getUUID());
            BookReadTracker.flush();
        }
    }

    @SubscribeEvent
    public static void skybox(RenderLevelStageEvent event) {
        BoltRenderer.onWorldRenderLast(event.getPartialTick().getGameTimeDeltaPartialTick(true), event.getPoseStack());
    }

    @SubscribeEvent
    public static void handleKeys(ClientTickEvent.Pre ev) {
        Minecraft mc = Minecraft.getInstance();
        while (HLClientEvents.OPEN_BANNER_SLOT_KEYBIND.consumeClick()) {
            if (mc.screen == null) {
                PacketDistributor.sendToServer(new PacketOpenBanner());
            }
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void initKeybinds(RegisterKeyMappingsEvent ev) {
            ev.register(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot", GLFW.GLFW_KEY_V, "key.armbanner.category"));
        }


        @SubscribeEvent
        public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof ItemGuideBook book) {
                    String prefix = book.getBookPrefix();
                    if (prefix == null || prefix.isEmpty()) {
                        continue;
                    }
                    var provider = book.getKnowledgeProvider();
                    IItemDecorator decorator = (graphics, font, stack, itemX, itemY) -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.player == null) {
                            return false;
                        }

                        IBookKnowledge knowledge = provider.apply(mc.player).orElse(null);
                        if (!hasUnreadForDecorator(mc.player, book, knowledge)) {
                            return false;
                        }

                        // Draw a 3x4 gold badge at the top-right corner of the item icon,
                        // with corner pixels removed so it looks like a small circle.
                        int dotX = itemX + 12;
                        int dotY = itemY;
                        int color = 0xFFFFD700;
                        graphics.fill(dotX + 1, dotY, dotX + 3, dotY + 1, color);
                        graphics.fill(dotX, dotY + 1, dotX + 4, dotY + 3, color);
                        graphics.fill(dotX + 1, dotY + 3, dotX + 3, dotY + 4, color);
                        return true;
                    };

                    event.register(book, decorator);
                }

            }
        }

        private static boolean hasUnreadForDecorator(Player player, ItemGuideBook book, IBookKnowledge knowledge) {
            String prefix = book.getBookPrefix();
            if (prefix == null || prefix.isEmpty()) {
                return false;
            }

            // Prefer page-id based unread state, but keep a knowledge-prefix fallback
            // for books whose unlock IDs don't map 1:1 with page IDs.
            Set<Identifier> visiblePageIds = book.collectVisiblePageIds(player);
            int unreadByPages = visiblePageIds.isEmpty()
                    ? 0
                    : BookReadTracker.countUnread(player.getUUID(), visiblePageIds);
            int unreadByKnowledge = knowledge != null
                    ? BookReadTracker.countUnread(player.getUUID(), knowledge, prefix)
                    : 0;

            return Math.max(unreadByPages, unreadByKnowledge) > 0;
        }


        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            IClientItemExtensions armBannerRenderer = new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return new RenderItemArmBanner(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
            };

            event.registerItem(armBannerRenderer, HLItemInit.leather_arm_banner.get(), HLItemInit.iron_arm_banner.get(), HLItemInit.gold_arm_banner.get(), HLItemInit.diamond_arm_banner.get(), HLItemInit.obsidian_arm_banner.get(), HLItemInit.netherite_arm_banner.get());

            event.registerItem(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return new RenderItemGuideBook(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
            }, HLItemInit.hl_guide_book.get());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static <T extends LivingEntity, M extends HumanoidModel<T>, R extends LivingEntityRenderer<T, M>> void addLayerToEntity(EntityRenderersEvent.AddLayers event, EntityType<? extends T> entityType) {
            R renderer = event.getRenderer(entityType);
            if (renderer != null) {
                renderer.addLayer(new LayerArmBanner(renderer));
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinModel) {
            EntityRenderer<? extends Player> render = event.getSkin(skinModel);
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
            addLayerToPlayerSkin(event, PlayerSkin.Model.WIDE);
            addLayerToPlayerSkin(event, PlayerSkin.Model.SLIM);
        }
    }
}
