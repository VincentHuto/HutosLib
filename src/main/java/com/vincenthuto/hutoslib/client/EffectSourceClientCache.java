package com.vincenthuto.hutoslib.client;

import java.util.Map;

import com.vincenthuto.hutoslib.common.effectsource.EffectSourceRecord;

import net.minecraft.resources.ResourceLocation;

public final class EffectSourceClientCache {

	private static Map<ResourceLocation, EffectSourceRecord> records = Map.of();

	public static EffectSourceRecord get(ResourceLocation effectId) {
		return records.get(effectId);
	}

	public static void replaceWith(Map<ResourceLocation, EffectSourceRecord> replacement) {
		records = Map.copyOf(replacement);
	}

	public static void clear() {
		records = Map.of();
	}

	private EffectSourceClientCache() {
	}
}
