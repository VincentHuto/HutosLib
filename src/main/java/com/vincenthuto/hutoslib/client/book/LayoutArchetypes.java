package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LayoutArchetypes {
    private static final Map<ResourceLocation, BookLayout> LAYOUTS = new HashMap<>();
    static {
        for (BookLayout.Kind kind : BookLayout.Kind.values())
            LAYOUTS.put(ResourceLocation.fromNamespaceAndPath("hutoslib", kind.name().toLowerCase(Locale.ROOT)), new BookLayout(kind, null));
    }
    private LayoutArchetypes() {}

    public static void register(ResourceLocation id, BookLayout layout) {
        if (LAYOUTS.putIfAbsent(id, layout) != null) throw new IllegalArgumentException("Book layout already registered: " + id);
    }

    public static BookLayout resolve(String id, BookLayout.Kind fallback) {
        ResourceLocation key = ResourceLocation.tryParse(id.contains(":") ? id : "hutoslib:" + id);
        return LAYOUTS.getOrDefault(key, new BookLayout(fallback, null));
    }
}
