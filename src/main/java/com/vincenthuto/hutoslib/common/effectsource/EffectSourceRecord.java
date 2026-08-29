package com.vincenthuto.hutoslib.common.effectsource;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record EffectSourceRecord(String sourceEntityName, String sourceEntityUuid, String sourceEntityType,
		String sourceMainHandItem, String sourceOffhandItem, String targetUseItem, String callerClass,
		String callerMethod, String callerFile, int callerLine, String callerModId, String callerModName,
		String callerModVersion, String callerJar, Confidence confidence) {

	private static final int MAX_TEXT_LENGTH = 1024;

	public EffectSourceRecord {
		sourceEntityName = clean(sourceEntityName);
		sourceEntityUuid = clean(sourceEntityUuid);
		sourceEntityType = clean(sourceEntityType);
		sourceMainHandItem = clean(sourceMainHandItem);
		sourceOffhandItem = clean(sourceOffhandItem);
		targetUseItem = clean(targetUseItem);
		callerClass = clean(callerClass);
		callerMethod = clean(callerMethod);
		callerFile = clean(callerFile);
		callerModId = clean(callerModId);
		callerModName = clean(callerModName);
		callerModVersion = clean(callerModVersion);
		callerJar = clean(callerJar);
		confidence = confidence == null ? Confidence.UNKNOWN : confidence;
	}

	public static EffectSourceRecord unknown() {
		return new EffectSourceRecord("", "", "", "", "", "", "", "", "", -1, "", "", "", "",
				Confidence.UNKNOWN);
	}

	public CompoundTag toTag() {
		var tag = new CompoundTag();
		tag.putString("SourceEntityName", sourceEntityName);
		tag.putString("SourceEntityUuid", sourceEntityUuid);
		tag.putString("SourceEntityType", sourceEntityType);
		tag.putString("SourceMainHandItem", sourceMainHandItem);
		tag.putString("SourceOffhandItem", sourceOffhandItem);
		tag.putString("TargetUseItem", targetUseItem);
		tag.putString("CallerClass", callerClass);
		tag.putString("CallerMethod", callerMethod);
		tag.putString("CallerFile", callerFile);
		tag.putInt("CallerLine", callerLine);
		tag.putString("CallerModId", callerModId);
		tag.putString("CallerModName", callerModName);
		tag.putString("CallerModVersion", callerModVersion);
		tag.putString("CallerJar", callerJar);
		tag.putString("Confidence", confidence.name());
		return tag;
	}

	public static EffectSourceRecord fromTag(CompoundTag tag) {
		Confidence confidence;
		try {
			confidence = Confidence.valueOf(tag.getString("Confidence"));
		} catch (IllegalArgumentException exception) {
			confidence = Confidence.UNKNOWN;
		}
		return new EffectSourceRecord(tag.getString("SourceEntityName"), tag.getString("SourceEntityUuid"),
				tag.getString("SourceEntityType"), tag.getString("SourceMainHandItem"),
				tag.getString("SourceOffhandItem"), tag.getString("TargetUseItem"), tag.getString("CallerClass"),
				tag.getString("CallerMethod"), tag.getString("CallerFile"), tag.getInt("CallerLine"),
				tag.getString("CallerModId"), tag.getString("CallerModName"), tag.getString("CallerModVersion"),
				tag.getString("CallerJar"), confidence);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(sourceEntityName, MAX_TEXT_LENGTH);
		buffer.writeUtf(sourceEntityUuid, MAX_TEXT_LENGTH);
		buffer.writeUtf(sourceEntityType, MAX_TEXT_LENGTH);
		buffer.writeUtf(sourceMainHandItem, MAX_TEXT_LENGTH);
		buffer.writeUtf(sourceOffhandItem, MAX_TEXT_LENGTH);
		buffer.writeUtf(targetUseItem, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerClass, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerMethod, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerFile, MAX_TEXT_LENGTH);
		buffer.writeVarInt(callerLine);
		buffer.writeUtf(callerModId, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerModName, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerModVersion, MAX_TEXT_LENGTH);
		buffer.writeUtf(callerJar, MAX_TEXT_LENGTH);
		buffer.writeEnum(confidence);
	}

	public static EffectSourceRecord read(FriendlyByteBuf buffer) {
		return new EffectSourceRecord(buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH),
				buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH),
				buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH),
				buffer.readUtf(MAX_TEXT_LENGTH), buffer.readVarInt(), buffer.readUtf(MAX_TEXT_LENGTH),
				buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH), buffer.readUtf(MAX_TEXT_LENGTH),
				buffer.readEnum(Confidence.class));
	}

	private static String clean(String value) {
		if (value == null) {
			return "";
		}
		return value.length() <= MAX_TEXT_LENGTH ? value : value.substring(0, MAX_TEXT_LENGTH);
	}

	public enum Confidence {
		EXPLICIT,
		INFERRED,
		UNKNOWN
	}
}
