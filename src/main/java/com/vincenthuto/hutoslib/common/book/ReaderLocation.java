package com.vincenthuto.hutoslib.common.book;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Objects;

/** An entry-relative source anchor. Leaf numbers deliberately do not participate in identity. */
public record ReaderLocation(ResourceLocation bookId, ResourceLocation entryId, String blockId, int codePointOffset) {
    public ReaderLocation {
        Objects.requireNonNull(bookId);
        Objects.requireNonNull(entryId);
        Objects.requireNonNull(blockId);
        codePointOffset = Math.max(0, codePointOffset);
    }

    public JsonObject toJson() {
        JsonObject value = new JsonObject();
        value.addProperty("book", bookId.toString());
        value.addProperty("entry", entryId.toString());
        value.addProperty("block", blockId);
        value.addProperty("offset", codePointOffset);
        return value;
    }

    public static ReaderLocation fromJson(JsonObject value) {
        return new ReaderLocation(ResourceLocation.parse(value.get("book").getAsString()),
                ResourceLocation.parse(value.get("entry").getAsString()), value.get("block").getAsString(),
                value.get("offset").getAsInt());
    }
}
