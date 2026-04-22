package com.vincenthuto.hutoslib.common.data.shadow;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import net.minecraft.network.FriendlyByteBuf;

public class JsonUtil {

// getRegistryObject and makeSerializer(IForgeRegistry) removed — IForgeRegistry no longer exists in NeoForge 1.21.1

public static <T> Object makeSerializer(com.google.gson.JsonDeserializer<T> jds, com.google.gson.JsonSerializer<T> js) {
return new SDS2<>(jds, js);
}

private static record SDS2<T>(com.google.gson.JsonDeserializer<T> jds, com.google.gson.JsonSerializer<T> js)
implements com.google.gson.JsonDeserializer<T>, com.google.gson.JsonSerializer<T> {

@Override
public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
return js.serialize(src, typeOfSrc, context);
}

@Override
public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
return jds.deserialize(json, typeOfT, context);
}
}

public static interface JsonSerializer<V> {
public JsonObject write(V src);
}

public static interface JsonDeserializer<V> {
public V read(JsonObject json);
}

public static interface NetSerializer<V> {
public void write(V src, FriendlyByteBuf buf);
}

public static interface NetDeserializer<V> {
public V read(FriendlyByteBuf buf);
}
}
