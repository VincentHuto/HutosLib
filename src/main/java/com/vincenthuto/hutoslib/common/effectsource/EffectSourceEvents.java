package com.vincenthuto.hutoslib.common.effectsource;

import java.util.Set;
import java.util.stream.Collectors;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketSyncEffectSources;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class EffectSourceEvents {

	@SubscribeEvent
	public static void onEffectAdded(MobEffectEvent.Added event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		ResourceLocation effectId = effectId(event.getEffectInstance().getEffect());
		if (effectId == null) {
			return;
		}

		Entity source = event.getEffectSource();
		var caller = EffectSourceInference.capture();
		var frame = caller.frame();
		var origin = caller.origin();
		var sourceLiving = source instanceof LivingEntity living ? living : null;
		var confidence = source != null
				? EffectSourceRecord.Confidence.EXPLICIT
				: frame.className().isEmpty() ? EffectSourceRecord.Confidence.UNKNOWN : EffectSourceRecord.Confidence.INFERRED;
		var record = new EffectSourceRecord(source == null ? "" : source.getDisplayName().getString(),
				source == null ? "" : source.getUUID().toString(),
				source == null ? "" : BuiltInRegistries.ENTITY_TYPE.getKey(source.getType()).toString(),
				sourceLiving == null ? "" : itemId(sourceLiving.getMainHandItem()),
				sourceLiving == null ? "" : itemId(sourceLiving.getOffhandItem()), itemId(player.getUseItem()),
				frame.className(), frame.methodName(), frame.fileName(), frame.lineNumber(), origin.modId(),
				origin.modName(), origin.modVersion(), origin.jar(), confidence);

		EffectSourceLedger ledger = player.getData(HLAttachmentTypes.EFFECT_SOURCE_RECORDS.get());
		ledger.put(effectId, record);
		sync(player, ledger);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEffectRemoved(MobEffectEvent.Remove event) {
		remove(event.getEntity(), effectId(event.getEffect()));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEffectExpired(MobEffectEvent.Expired event) {
		remove(event.getEntity(), effectId(event.getEffectInstance().getEffect()));
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			reconcileAndSync(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			reconcileAndSync(player);
		}
	}

	private static void remove(LivingEntity entity, ResourceLocation effectId) {
		if (!(entity instanceof ServerPlayer player) || effectId == null) {
			return;
		}
		EffectSourceLedger ledger = player.getData(HLAttachmentTypes.EFFECT_SOURCE_RECORDS.get());
		if (ledger.remove(effectId)) {
			sync(player, ledger);
		}
	}

	private static void reconcileAndSync(ServerPlayer player) {
		EffectSourceLedger ledger = player.getData(HLAttachmentTypes.EFFECT_SOURCE_RECORDS.get());
		Set<ResourceLocation> activeEffects = player.getActiveEffects().stream()
				.map(instance -> effectId(instance.getEffect()))
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toSet());
		ledger.retainEffects(activeEffects);
		sync(player, ledger);
	}

	private static void sync(ServerPlayer player, EffectSourceLedger ledger) {
		PacketDistributor.sendToPlayer(player, new PacketSyncEffectSources(ledger.records()));
	}

	private static ResourceLocation effectId(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
		return effect.unwrapKey().map(key -> key.location()).orElse(null);
	}

	private static String itemId(ItemStack stack) {
		return stack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
	}

	private EffectSourceEvents() {
	}
}
