package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.particle.factory.DarkGlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.EmberParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.GlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.render.item.ArmBannerSpecialRenderer;
import com.vincenthuto.hutoslib.client.render.item.GuideBookSpecialRenderer;
import com.vincenthuto.hutoslib.client.render.layer.LayerArmBanner;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID)
public class HLClientEvents {

    public static final KeyMapping.Category HUTOSLIB_KEY_CATEGORY = KeyMapping.Category.register(HutosLib.rloc("hutoslib"));
    public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getPlayer() != null) {
            ItemGuideBook.clearState(event.getPlayer().getUUID());
            BookReadTracker.flush();
        }
    }

    @SubscribeEvent
    public static void skybox(RenderLevelStageEvent.AfterTranslucentParticles event) {
        BoltRenderer.onWorldRenderLast(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), event.getPoseStack());
    }

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
            ev.register(OPEN_BANNER_SLOT_KEYBIND = new KeyMapping("key.banner_slot.slot", GLFW.GLFW_KEY_V, HUTOSLIB_KEY_CATEGORY));
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
        public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
            event.register(ArmBannerSpecialRenderer.TYPE, ArmBannerSpecialRenderer.Unbaked.MAP_CODEC);
            event.register(GuideBookSpecialRenderer.TYPE, GuideBookSpecialRenderer.Unbaked.MAP_CODEC);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerModelType skinModel) {
            EntityRenderer render = event.getPlayerRenderer(skinModel);
            if (render instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new LayerArmBanner(livingRenderer));
            }
        }


        @SubscribeEvent
        public static void constructLayers(EntityRenderersEvent.AddLayers event) {
            addLayerToPlayerSkin(event, PlayerModelType.WIDE);
            addLayerToPlayerSkin(event, PlayerModelType.SLIM);
        }

        @SubscribeEvent
        public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
            event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
                @Override
                public <T extends Avatar & ClientAvatarEntity> void accept(T player, AvatarRenderState state) {
                    ItemStack banner = BannerFinder.findBanner(player, true).map(getter -> getter.getBanner().copy()).orElse(ItemStack.EMPTY);
                    ((IRenderStateExtension) state).setRenderData(LayerArmBanner.ARM_BANNER_STACK, banner);
                }
            });
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(HlContainerInit.banner_slot_container.get(), com.vincenthuto.hutoslib.client.screen.BannerSlotScreen::new);
        }

        @SubscribeEvent
        public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(HLBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(HLParticleInit.glow.get(), GlowParticleFactory::new);
            event.registerSpriteSet(HLParticleInit.dark_glow.get(), DarkGlowParticleFactory::new);
            event.registerSpriteSet(HLParticleInit.lightning_bolt.get(), LightningParticleFactory::new);
            event.registerSpriteSet(HLParticleInit.ember.get(), EmberParticleFactory::new);
        }
    }
}
