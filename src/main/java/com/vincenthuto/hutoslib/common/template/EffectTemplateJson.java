package com.vincenthuto.hutoslib.common.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.nbt.CompoundTag;

public final class EffectTemplateJson {
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private EffectTemplateJson() {
	}

	public static LightningTestConfig parseLightning(String json) {
		return LightningTestConfig.fromTag(toTag(configObject(json)));
	}

	public static TendrilEffectConfig parseTendril(String json) {
		return TendrilEffectConfig.fromTag(toTag(configObject(json)));
	}

	public static String toLightningJson(LightningTestConfig config) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "hutoslib:lightning");
		root.add("config", toJson(config.toTag()));
		return GSON.toJson(root);
	}

	public static String toTendrilJson(TendrilEffectConfig config) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "hutoslib:tendril");
		root.add("config", toJson(config.toTag()));
		return GSON.toJson(root);
	}

	private static JsonObject configObject(String json) {
		JsonElement element = JsonParser.parseString(json);
		if (!element.isJsonObject()) {
			throw new IllegalArgumentException("Template JSON must be an object.");
		}
		JsonObject object = element.getAsJsonObject();
		JsonElement config = object.get("config");
		if (config != null) {
			if (!config.isJsonObject()) {
				throw new IllegalArgumentException("Template config must be an object.");
			}
			return config.getAsJsonObject();
		}
		return object;
	}

	private static JsonObject toJson(CompoundTag tag) {
		JsonObject object = new JsonObject();
		for (String key : tag.getAllKeys()) {
			if (tag.contains(key, CompoundTag.TAG_STRING)) {
				object.addProperty(key, tag.getString(key));
			} else if (tag.contains(key, CompoundTag.TAG_BYTE)) {
				object.addProperty(key, tag.getBoolean(key));
			} else if (tag.contains(key, CompoundTag.TAG_INT)) {
				object.addProperty(key, tag.getInt(key));
			} else if (tag.contains(key, CompoundTag.TAG_LONG)) {
				object.addProperty(key, tag.getLong(key));
			} else if (tag.contains(key, CompoundTag.TAG_FLOAT)) {
				object.addProperty(key, tag.getFloat(key));
			} else if (tag.contains(key, CompoundTag.TAG_DOUBLE)) {
				object.addProperty(key, tag.getDouble(key));
			}
		}
		return object;
	}

	private static CompoundTag toTag(JsonObject object) {
		CompoundTag tag = new CompoundTag();
		for (String key : object.keySet()) {
			JsonElement value = object.get(key);
			if (value == null || value.isJsonNull() || !value.isJsonPrimitive()) {
				continue;
			}
			var primitive = value.getAsJsonPrimitive();
			if (primitive.isBoolean()) {
				tag.putBoolean(key, primitive.getAsBoolean());
			} else if (primitive.isNumber()) {
				putNumber(tag, key, primitive.getAsString());
			} else if (primitive.isString()) {
				tag.putString(key, primitive.getAsString());
			}
		}
		return tag;
	}

	private static void putNumber(CompoundTag tag, String key, String value) {
		if (value.indexOf('.') >= 0 || value.indexOf('e') >= 0 || value.indexOf('E') >= 0) {
			tag.putFloat(key, Float.parseFloat(value));
			return;
		}
		long parsed = Long.parseLong(value);
		if (parsed >= Integer.MIN_VALUE && parsed <= Integer.MAX_VALUE) {
			tag.putInt(key, (int) parsed);
		} else {
			tag.putLong(key, parsed);
		}
	}
}
