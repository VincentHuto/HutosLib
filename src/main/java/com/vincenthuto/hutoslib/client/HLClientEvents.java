package com.vincenthuto.hutoslib.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.particle.TendrilRenderer;
import com.vincenthuto.hutoslib.client.render.item.RenderItemArmBanner;
import com.vincenthuto.hutoslib.client.render.item.RenderItemGuideBook;
import com.vincenthuto.hutoslib.client.render.layer.LayerArmBanner;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.effectsource.EffectSourceInference;
import com.vincenthuto.hutoslib.common.effectsource.EffectSourceRecord;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import com.vincenthuto.hutoslib.common.network.PacketOpenBanner;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class HLClientEvents {

	public static KeyMapping OPEN_BANNER_SLOT_KEYBIND;
	private static EffectLayout effectLayout;

	@SubscribeEvent
	public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
		if (event.getPlayer() != null) {
			ItemGuideBook.clearState(event.getPlayer().getUUID());
			BookReadTracker.flush();
		}
		BoltRenderer.INSTANCE.clear();
		TendrilRenderer.INSTANCE.clear();
		EffectSourceClientCache.clear();
	}

	@SubscribeEvent
	public static void addEffectSourceTooltip(GatherEffectScreenTooltipsEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if (player == null || (!player.getMainHandItem().is(HLItemInit.effect_source_lens.get())
				&& !player.getOffhandItem().is(HLItemInit.effect_source_lens.get()))) {
			return;
		}

		var effectHolder = event.getEffectInstance().getEffect();
		ResourceLocation effectId = effectHolder.unwrapKey().map(key -> key.location()).orElse(null);
		if (effectId == null) {
			return;
		}
		var tooltip = event.getTooltip();
		var effect = effectHolder.value();
		var modInfo = ModList.get().getModContainerById(effectId.getNamespace()).map(container -> container.getModInfo())
				.orElse(null);
		String modName = modInfo == null ? unknown() : modInfo.getDisplayName();
		String modVersion = modInfo == null ? unknown() : modInfo.getVersion().toString();
		String effectJar = EffectSourceInference.jarName(effect.getClass());

		tooltip.add(Component.translatable("tooltip.hutoslib.effect_source_lens.heading").withStyle(ChatFormatting.GOLD));
		tooltip.add(line("effect_id", effectId));
		tooltip.add(line("owner", modName, effectId.getNamespace(), modVersion));
		tooltip.add(line("implementation", effect.getClass().getName()));
		tooltip.add(line("effect_jar", effectJar));
		tooltip.add(Component.translatable("tooltip.hutoslib.effect_source_lens.latest_application")
				.withStyle(ChatFormatting.GOLD));

		EffectSourceRecord record = EffectSourceClientCache.get(effectId);
		if (record == null) {
			tooltip.add(line("source", unknown(), unknown()));
			tooltip.add(line("confidence", EffectSourceRecord.Confidence.UNKNOWN.name()));
			return;
		}
		tooltip.add(line("source", display(record.sourceEntityName()), display(record.sourceEntityType())));
		tooltip.add(line("source_uuid", display(record.sourceEntityUuid())));
		tooltip.add(line("items", display(record.sourceMainHandItem()), display(record.sourceOffhandItem()),
				display(record.targetUseItem())));
		tooltip.add(line("caller", display(record.callerClass()), display(record.callerMethod()),
				display(record.callerFile()), record.callerLine() < 0 ? unknown() : record.callerLine()));
		tooltip.add(line("caller_owner", display(record.callerModName()), display(record.callerModId()),
				display(record.callerModVersion())));
		tooltip.add(line("caller_jar", display(record.callerJar())));
		tooltip.add(line("confidence", record.confidence().name()));
	}

	@SubscribeEvent
	public static void beforeScreenRender(ScreenEvent.Render.Pre event) {
		effectLayout = null;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void captureEffectLayout(ScreenEvent.RenderInventoryMobEffects event) {
		if (event.getScreen() instanceof EffectRenderingInventoryScreen<?> screen) {
			effectLayout = new EffectLayout(screen, event.getHorizontalOffset(), event.isCompact());
		}
	}

	@SubscribeEvent
	public static void renderWideEffectTooltip(ScreenEvent.Render.Post event) {
		if (effectLayout == null || effectLayout.compact() || effectLayout.screen() != event.getScreen()
				|| !holdsEffectSourceLens()) {
			return;
		}
		int mouseX = event.getMouseX();
		if (mouseX < effectLayout.x() || mouseX > effectLayout.x() + 120) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		List<MobEffectInstance> effects = minecraft.player.getActiveEffects().stream()
				.filter(ClientHooks::shouldRenderEffect)
				.sorted()
				.toList();
		int index = hoveredEffectIndex(effects.size(), effectLayout.screen().getGuiTop(), event.getMouseY());
		if (index < 0) {
			return;
		}

		MobEffectInstance effect = effects.get(index);
		MutableComponent name = effect.getEffect().value().getDisplayName().copy();
		if (effect.getAmplifier() >= 1 && effect.getAmplifier() <= 9) {
			name.append(CommonComponents.SPACE)
					.append(Component.translatable("enchantment.level." + (effect.getAmplifier() + 1)));
		}
		List<Component> tooltip = new ArrayList<>(List.of(name,
				MobEffectUtil.formatDuration(effect, 1.0F, minecraft.level.tickRateManager().tickrate())));
		tooltip = ClientHooks.getEffectTooltip(effectLayout.screen(), effect, tooltip);
		event.getGuiGraphics().renderTooltip(minecraft.font, tooltip, Optional.empty(), mouseX, event.getMouseY());
	}

	static int hoveredEffectIndex(int effectCount, int top, int mouseY) {
		if (effectCount == 0 || mouseY < top) {
			return -1;
		}
		int spacing = effectCount > 5 ? 132 / (effectCount - 1) : 33;
		int hovered = -1;
		for (int index = 0, y = top; index < effectCount; index++, y += spacing) {
			if (mouseY >= y && mouseY <= y + spacing) {
				hovered = index;
			}
		}
		return hovered;
	}

	private static boolean holdsEffectSourceLens() {
		Player player = Minecraft.getInstance().player;
		return player != null && (player.getMainHandItem().is(HLItemInit.effect_source_lens.get())
				|| player.getOffhandItem().is(HLItemInit.effect_source_lens.get()));
	}

	private static Component line(String key, Object... values) {
		Object[] arguments = Arrays.stream(values)
				.map(value -> value instanceof Component || value instanceof Number || value instanceof Boolean
						|| value instanceof String ? value : String.valueOf(value))
				.toArray();
		return Component.translatable("tooltip.hutoslib.effect_source_lens." + key, arguments)
				.withStyle(ChatFormatting.GRAY);
	}

	private static String display(String value) {
		return value == null || value.isBlank() ? unknown() : value;
	}

	private static String unknown() {
		return Component.translatable("tooltip.hutoslib.effect_source_lens.unknown").getString();
	}

	private record EffectLayout(EffectRenderingInventoryScreen<?> screen, int x, boolean compact) {
	}

	@SubscribeEvent
	public static void skybox(RenderLevelStageEvent event) {
		if (!shouldRenderBolts(event.getStage())) {
			return;
		}
		BoltRenderer.onWorldRenderLast(event.getPartialTick().getGameTimeDeltaPartialTick(true), event.getPoseStack());
		TendrilRenderer.onWorldRenderLast(event.getPartialTick().getGameTimeDeltaPartialTick(true),
				event.getPoseStack());
	}

	static boolean shouldRenderBolts(RenderLevelStageEvent.Stage stage) {
		return stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES || shouldRenderBoltStage(stage.toString());
	}

	static boolean shouldRenderBoltStage(String stageName) {
		return "after_particles".equals(stageName) || "minecraft:after_particles".equals(stageName);
	}

	@SubscribeEvent
	public static void handleKeys(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		while (HLClientEvents.OPEN_BANNER_SLOT_KEYBIND.consumeClick()) {
			if (mc.screen == null) {
				PacketDistributor.sendToServer(new PacketOpenBanner());
			}
		}
	}

	@EventBusSubscriber(value = Dist.CLIENT, modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {

		@SubscribeEvent
		public static void initKeybinds(RegisterKeyMappingsEvent event) {
			event.register(OPEN_BANNER_SLOT_KEYBIND =
					new KeyMapping("key.banner_slot.slot", GLFW.GLFW_KEY_V, "key.armbanner.category"));
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

			Set<ResourceLocation> visiblePageIds = book.collectVisiblePageIds(player);
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
					return new RenderItemArmBanner(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
							Minecraft.getInstance().getEntityModels());
				}
			};

			event.registerItem(armBannerRenderer, HLItemInit.leather_arm_banner.get(), HLItemInit.iron_arm_banner.get(),
					HLItemInit.gold_arm_banner.get(), HLItemInit.diamond_arm_banner.get(),
					HLItemInit.obsidian_arm_banner.get(), HLItemInit.netherite_arm_banner.get());

			event.registerItem(new IClientItemExtensions() {
				@Override
				public BlockEntityWithoutLevelRenderer getCustomRenderer() {
					return new RenderItemGuideBook(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
							Minecraft.getInstance().getEntityModels());
				}
			}, HLItemInit.hl_guide_book.get());
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static <T extends LivingEntity, M extends HumanoidModel<T>, R extends LivingEntityRenderer<T, M>> void addLayerToEntity(
				EntityRenderersEvent.AddLayers event, EntityType<? extends T> entityType) {
			R renderer = event.getRenderer(entityType);
			if (renderer != null) {
				renderer.addLayer(new LayerArmBanner(renderer));
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinModel) {
			EntityRenderer<? extends Player> renderer = event.getSkin(skinModel);
			if (renderer instanceof LivingEntityRenderer livingRenderer) {
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
