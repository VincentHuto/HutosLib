package com.vincenthuto.hutoslib.common.effectsource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

class EffectSourceLedgerTest {

	private static final ResourceLocation EFFECT_ID = ResourceLocation.parse("example:moon_sight");

	@Test
	void roundTripsApplicationRecordsThroughNbt() {
		var ledger = new EffectSourceLedger();
		var record = new EffectSourceRecord("Witch", "00000000-0000-0000-0000-000000000001",
				"minecraft:witch", "minecraft:splash_potion", "", "", "example.magic.MoonEffects",
				"applyMoonSight", "MoonEffects.java", 81, "example", "Example Magic", "2.0", "example.jar",
				EffectSourceRecord.Confidence.EXPLICIT);
		ledger.put(EFFECT_ID, record);
		var registries = HolderLookup.Provider.create(Stream.empty());

		var copy = new EffectSourceLedger();
		copy.deserializeNBT(registries, ledger.serializeNBT(registries));

		assertEquals(record, copy.records().get(EFFECT_ID));
	}

	@Test
	void pruningDropsRecordsForEffectsNoLongerActive() {
		var ledger = new EffectSourceLedger();
		ledger.put(EFFECT_ID, EffectSourceRecord.unknown());

		ledger.retainEffects(Set.of(ResourceLocation.parse("minecraft:speed")));

		assertFalse(ledger.records().containsKey(EFFECT_ID));
	}

	@Test
	void recordsClampModSuppliedTextToPacketLimit() {
		var record = new EffectSourceRecord("x".repeat(2000), "", "", "", "", "", "", "", "", -1, "", "", "", "",
				EffectSourceRecord.Confidence.EXPLICIT);

		assertEquals(1024, record.sourceEntityName().length());
	}
}
