package com.vincenthuto.hutoslib.common.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.common.effectsource.EffectSourceRecord;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

class PacketSyncEffectSourcesTest {

	@Test
	void roundTripsFullEffectSourceMap() {
		var expected = Map.of(ResourceLocation.parse("example:moon_sight"), EffectSourceRecord.unknown());
		var buffer = new FriendlyByteBuf(Unpooled.buffer());

		PacketSyncEffectSources.CODEC.encode(buffer, new PacketSyncEffectSources(expected));

		assertEquals(expected, PacketSyncEffectSources.CODEC.decode(buffer).records());
	}

	@Test
	void rejectsMoreThanMaximumRecords() {
		var records = new LinkedHashMap<ResourceLocation, EffectSourceRecord>();
		for (int i = 0; i <= PacketSyncEffectSources.MAX_RECORDS; i++) {
			records.put(ResourceLocation.fromNamespaceAndPath("example", "effect_" + i), EffectSourceRecord.unknown());
		}

		assertThrows(IllegalArgumentException.class,
				() -> PacketSyncEffectSources.CODEC.encode(new FriendlyByteBuf(Unpooled.buffer()), new PacketSyncEffectSources(records)));
	}

	@Test
	void rejectsOversizedRecordCountBeforeReadingEntries() {
		var buffer = new FriendlyByteBuf(Unpooled.buffer());
		buffer.writeVarInt(PacketSyncEffectSources.MAX_RECORDS + 1);

		var exception = assertThrows(IllegalArgumentException.class,
				() -> PacketSyncEffectSources.CODEC.decode(buffer));

		assertEquals("Invalid effect source record count: 257", exception.getMessage());
	}
}
