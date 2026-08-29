package com.vincenthuto.hutoslib.common.effectsource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public final class EffectSourceLedger implements INBTSerializable<CompoundTag> {

	public static final int MAX_RECORDS = 256;

	private final Map<ResourceLocation, EffectSourceRecord> records = new LinkedHashMap<>();

	public Map<ResourceLocation, EffectSourceRecord> records() {
		return Map.copyOf(records);
	}

	public void replaceWith(Map<ResourceLocation, EffectSourceRecord> replacement) {
		records.clear();
		replacement.entrySet().stream().limit(MAX_RECORDS).forEach(entry -> records.put(entry.getKey(), entry.getValue()));
	}

	public void put(ResourceLocation effectId, EffectSourceRecord record) {
		if (!records.containsKey(effectId) && records.size() >= MAX_RECORDS) {
			records.remove(records.keySet().iterator().next());
		}
		records.put(effectId, record);
	}

	public boolean remove(ResourceLocation effectId) {
		return records.remove(effectId) != null;
	}

	public boolean retainEffects(Set<ResourceLocation> activeEffects) {
		return records.keySet().removeIf(id -> !activeEffects.contains(id));
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		var entries = new ListTag();
		records.forEach((effectId, record) -> {
			var entry = new CompoundTag();
			entry.putString("Effect", effectId.toString());
			entry.put("Source", record.toTag());
			entries.add(entry);
		});
		var root = new CompoundTag();
		root.put("Records", entries);
		return root;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		records.clear();
		var entries = tag.getList("Records", Tag.TAG_COMPOUND);
		for (int i = 0; i < entries.size() && records.size() < MAX_RECORDS; i++) {
			var entry = entries.getCompound(i);
			ResourceLocation effectId = ResourceLocation.tryParse(entry.getString("Effect"));
			if (effectId != null && entry.contains("Source", Tag.TAG_COMPOUND)) {
				records.put(effectId, EffectSourceRecord.fromTag(entry.getCompound("Source")));
			}
		}
	}
}
