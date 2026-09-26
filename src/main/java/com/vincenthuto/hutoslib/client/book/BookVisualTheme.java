package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

/** A fully resolved, immutable client resource snapshot. */
public record BookVisualTheme(ResourceLocation id, Map<String, Integer> colors,
                              Map<String, ResourceLocation> fonts, Map<String, ResourceLocation> textures,
                              List<ResourceLocation> decals, Map<BookAtlas, Sprite> sprites,
                              Layout layout, Motion motion, Map<String, Sound> sounds,
                              boolean legacy, ResourceLocation legacyTabs, ColorShift unreadTitleShift) {
    public BookVisualTheme(ResourceLocation id, Map<String, Integer> colors,
                           Map<String, ResourceLocation> fonts, Map<String, ResourceLocation> textures,
                           List<ResourceLocation> decals, Map<BookAtlas, Sprite> sprites,
                           Layout layout, Motion motion, Map<String, Sound> sounds,
                           boolean legacy, ResourceLocation legacyTabs) {
        this(id, colors, fonts, textures, decals, sprites, layout, motion, sounds, legacy, legacyTabs, null);
    }
    public BookVisualTheme {
        colors = Map.copyOf(colors);
        fonts = Map.copyOf(fonts);
        textures = Map.copyOf(textures);
        decals = List.copyOf(decals);
        sprites = Map.copyOf(sprites);
        sounds = Map.copyOf(sounds);
    }
    public record Sprite(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {}
    public record Layout(String tabStyle, String spread, int pageWidth, int pageHeight,
                         String redaction, double decalChance, boolean rule, String locked) {}
    public record Motion(int turnTicks, int tabTicks, int revealTicks) {}
    public record Sound(ResourceLocation id, float pitch) {}

    /** A smooth round trip between two ARGB colors; periodTicks is the full cycle. */
    public record ColorShift(int from, int to, int periodTicks) {
        public ColorShift {
            if (periodTicks < 1 || periodTicks > 1200)
                throw new IllegalArgumentException("Color shift periodTicks must be between 1 and 1200");
        }
        public int colorAt(long millis) {
            long period = periodTicks * 50L;
            long phase = Math.floorMod(millis, period);
            long distance = Math.min(phase, period - phase);
            double amount = (1 - Math.cos(distance * Math.PI * 2 / period)) / 2;
            return BookColors.blend(from, to, amount);
        }
    }

    public int entryTitleColor(boolean hovered, boolean unread, long millis) {
        if (unread && unreadTitleShift != null) {
            int animated = unreadTitleShift.colorAt(millis);
            return hovered ? BookColors.blend(animated, color("paper"), 0.25) : animated;
        }
        return color(hovered ? "inkRead" : "ink");
    }

    public int color(String role) { return colors.getOrDefault(role, colors.get("ink")); }
    public ResourceLocation font(String role) { return fonts.get(role); }
    public ResourceLocation texture(String role) { return textures.get(role); }
    public Sprite sprite(BookAtlas region) { return sprites.get(region); }
    public boolean darkPaper() { return BookColors.luminance(color("paper")) < 0.3; }
}
