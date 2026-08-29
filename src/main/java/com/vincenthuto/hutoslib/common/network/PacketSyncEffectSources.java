package com.vincenthuto.hutoslib.common.network;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vincenthuto.hutoslib.client.EffectSourceClientCache;
import com.vincenthuto.hutoslib.common.effectsource.EffectSourceRecord;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSyncEffectSources(Map<ResourceLocation, EffectSourceRecord> records) implements CustomPacketPayload {

	public static final int MAX_RECORDS = 256;
	public static final Type<PacketSyncEffectSources> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath("hutoslib", "sync_effect_sources"));
	public static final StreamCodec<FriendlyByteBuf, PacketSyncEffectSources> CODEC = StreamCodec.of(
			PacketSyncEffectSources::encode, PacketSyncEffectSources::decode);

	public PacketSyncEffectSources {
		records = Map.copyOf(records);
	}

	private static void encode(FriendlyByteBuf buffer, PacketSyncEffectSources packet) {
		if (packet.records.size() > MAX_RECORDS) {
			throw new IllegalArgumentException("Too many effect source records: " + packet.records.size());
		}
		buffer.writeVarInt(packet.records.size());
		packet.records.forEach((effectId, record) -> {
			buffer.writeResourceLocation(effectId);
			record.write(buffer);
		});
	}

	private static PacketSyncEffectSources decode(FriendlyByteBuf buffer) {
		int count = buffer.readVarInt();
		if (count < 0 || count > MAX_RECORDS) {
			throw new IllegalArgumentException("Invalid effect source record count: " + count);
		}
		var records = new LinkedHashMap<ResourceLocation, EffectSourceRecord>();
		for (int i = 0; i < count; i++) {
			records.put(buffer.readResourceLocation(), EffectSourceRecord.read(buffer));
		}
		return new PacketSyncEffectSources(records);
	}

	public static void handle(PacketSyncEffectSources packet, IPayloadContext context) {
		context.enqueueWork(() -> EffectSourceClientCache.replaceWith(packet.records));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
