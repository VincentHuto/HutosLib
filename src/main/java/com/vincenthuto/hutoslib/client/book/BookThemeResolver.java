package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Resolves authored fields before deriving colors; no Minecraft singleton is needed. */
public final class BookThemeResolver {
    public static final ResourceLocation DEFAULT_ID = ResourceLocation.parse("hutoslib:default");
    private static final JsonObject DEFAULT = JsonParser.parseString("""
            {"colors":{"paper":"#D8B98A","ink":"#3A2A1C","accent":"#5A4632"},
             "fonts":{"title":"minecraft:default","body":"minecraft:default","label":"minecraft:default","badge":"hutoslib:book_badge"},
             "textures":{"page":"hutoslib:textures/gui/book/default/page.png","paper":"hutoslib:textures/gui/book/default/paper.png",
               "ui":"hutoslib:textures/gui/book/default/ui.png","leaf":"hutoslib:textures/gui/book/default/leaf.png","decals":[]},
             "layout":{"tabStyle":"fore_edge","spread":"auto","pageSize":[174,228],"redaction":"none","decals":0,"rule":true,"locked":"hide"},
             "motion":{"turnTicks":12,"tabTicks":2,"revealTicks":20},
             "sounds":{"open":"minecraft:item.book.page_turn","close":"minecraft:item.book.page_turn","turn":"minecraft:item.book.page_turn",
               "bookmark":"minecraft:item.book.put","unlock":"minecraft:block.enchantment_table.use"}}
            """).getAsJsonObject();

    private BookThemeResolver() {}

    public static Map<ResourceLocation, BookVisualTheme> resolve(Map<ResourceLocation, JsonObject> definitions,
                                                               Predicate<ResourceLocation> exists, Consumer<String> warning) {
        Map<ResourceLocation, JsonObject> sources = new HashMap<>(definitions);
        JsonObject root = DEFAULT.deepCopy();
        if (sources.containsKey(DEFAULT_ID)) merge(root, sources.get(DEFAULT_ID));
        root.remove("parent");
        sources.put(DEFAULT_ID, root);
        Map<ResourceLocation, BookVisualTheme> resolved = new HashMap<>();
        BookVisualTheme fallback;
        try {
            fallback = materialize(DEFAULT_ID, List.of(root), exists, warning);
        } catch (RuntimeException invalid) {
            warning.accept("Invalid default book theme: " + invalid.getMessage());
            root = DEFAULT.deepCopy();
            sources.put(DEFAULT_ID, root);
            fallback = materialize(DEFAULT_ID, List.of(root), exists, warning);
        }
        resolved.put(DEFAULT_ID, fallback);
        for (ResourceLocation id : new TreeSet<>(sources.keySet())) {
            if (id.equals(DEFAULT_ID)) continue;
            try {
                List<JsonObject> chain = chain(id, sources, new HashSet<>());
                resolved.put(id, materialize(id, chain, exists, warning));
            } catch (RuntimeException invalid) {
                warning.accept("Book theme " + id + " uses the default: " + invalid.getMessage());
                resolved.put(id, fallback);
            }
        }
        return Map.copyOf(resolved);
    }

    private static List<JsonObject> chain(ResourceLocation id, Map<ResourceLocation, JsonObject> sources, Set<ResourceLocation> visiting) {
        if (!visiting.add(id)) throw new IllegalArgumentException("parent cycle at " + id);
        JsonObject own = sources.get(id);
        if (own == null) throw new IllegalArgumentException("missing parent " + id);
        List<JsonObject> result = new ArrayList<>();
        result.add(own);
        if (!id.equals(DEFAULT_ID)) {
            ResourceLocation parent = own.has("parent") ? ResourceLocation.parse(own.get("parent").getAsString()) : DEFAULT_ID;
            result.addAll(chain(parent, sources, visiting));
        }
        return result;
    }

    private static BookVisualTheme materialize(ResourceLocation id, List<JsonObject> chain,
                                               Predicate<ResourceLocation> exists, Consumer<String> warning) {
        JsonObject merged = new JsonObject();
        for (int i = chain.size() - 1; i >= 0; i--) merge(merged, chain.get(i));
        Map<String, Integer> explicit = new HashMap<>();
        object(merged, "colors").entrySet().forEach(entry -> explicit.put(entry.getKey(), BookColors.parse(entry.getValue().getAsString())));
        Map<String, Integer> colors = BookColors.derive(explicit);
        if (BookColors.contrast(colors.get("ink"), colors.get("paper")) < 4.5)
            warning.accept("Book theme " + id + ": ink contrast is below 4.5:1");
        if (BookColors.contrast(colors.get("inkMuted"), colors.get("paper")) < 3)
            warning.accept("Book theme " + id + ": muted ink contrast is below 3:1");

        Map<String, ResourceLocation> fonts = new HashMap<>();
        for(String role:object(merged,"fonts").keySet()) {
            for(JsonObject layer:chain) {
                JsonElement field=object(layer,"fonts").get(role);
                if(field==null)continue;
                if(field.isJsonNull()) {
                    if(Set.of("title","body").contains(role))continue;
                    break;
                }
                ResourceLocation font=ResourceLocation.parse(field.getAsString());
                if(exists.test(font.withPath("font/"+font.getPath()+".json"))) {fonts.put(role,font);break;}
                warning.accept("Book theme "+id+": missing font "+font+"; checking parent");
            }
        }
        fonts.putIfAbsent("title",ResourceLocation.withDefaultNamespace("default"));
        fonts.putIfAbsent("body",ResourceLocation.withDefaultNamespace("default"));

        Map<String, ResourceLocation> textures = new HashMap<>();
        Set<String> roles = new HashSet<>();
        chain.forEach(layer -> roles.addAll(object(layer, "textures").keySet()));
        for (String role : roles) {
            if (role.equals("decals")) continue;
            ResourceLocation texture = resolveAsset(chain, "textures", role, exists, warning);
            if (texture != null) textures.put(role, texture);
        }
        List<ResourceLocation> decals = new ArrayList<>();
        JsonElement decalList = object(merged, "textures").get("decals");
        if (decalList != null && decalList.isJsonArray()) {
            for (JsonElement value : decalList.getAsJsonArray()) {
                ResourceLocation texture = ResourceLocation.parse(value.getAsString());
                if (exists.test(texture)) decals.add(texture);
                else warning.accept("Book theme " + id + ": missing decal " + texture);
            }
        }
        decals.sort(Comparator.naturalOrder());

        Map<BookAtlas, BookVisualTheme.Sprite> sprites = new EnumMap<>(BookAtlas.class);
        for (BookAtlas region : BookAtlas.values()) {
            boolean clearedOverride = false;
            for (JsonObject layer : chain) {
                JsonElement field = object(layer, "sprites").get(region.name());
                clearedOverride |= field != null && field.isJsonNull();
                ResourceLocation override = clearedOverride ? null : asset(field, exists, warning);
                if (override != null) {
                    sprites.put(region, new BookVisualTheme.Sprite(override, 0, 0, region.width, region.height));
                    break;
                }
                ResourceLocation atlas = asset(object(layer, "textures").get("ui"), exists, warning);
                if (atlas != null) {
                    sprites.put(region, new BookVisualTheme.Sprite(atlas, region.u, region.v, 256, 256));
                    break;
                }
            }
            sprites.putIfAbsent(region, new BookVisualTheme.Sprite(ResourceLocation.parse("hutoslib:textures/gui/book/default/ui.png"),
                    region.u, region.v, 256, 256));
        }

        JsonObject layout = object(merged, "layout"), motion = object(merged, "motion");
        var size = layout.getAsJsonArray("pageSize");
        if (size.size() != 2) throw new IllegalArgumentException("pageSize must have two dimensions");
        int width = size.get(0).getAsInt(), height = size.get(1).getAsInt();
        if (width < 64 || height < 96 || width > 1024 || height > 1024) throw new IllegalArgumentException("pageSize is outside 64x96 through 1024x1024");
        String tabStyle = choice(layout, "tabStyle", Set.of("fore_edge", "head", "spine", "none"));
        String spread = choice(layout, "spread", Set.of("auto", "always", "never"));
        String redaction = choice(layout, "redaction", Set.of("whitewash", "none"));
        String locked = choice(layout, "locked", Set.of("hide", "show"));
        double chance = layout.get("decals").getAsDouble();
        if (!Double.isFinite(chance)) throw new IllegalArgumentException("Non-finite decal chance");
        Map<String, BookVisualTheme.Sound> sounds = new HashMap<>();
        object(merged, "sounds").entrySet().forEach(entry -> {
            JsonElement value = entry.getValue();
            if (value.isJsonNull()) return;
            String sound = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
            float pitch = value.isJsonObject() && value.getAsJsonObject().has("pitch") ? value.getAsJsonObject().get("pitch").getAsFloat() : 1;
            if (!Float.isFinite(pitch)) throw new IllegalArgumentException("Non-finite sound pitch");
            sounds.put(entry.getKey(), new BookVisualTheme.Sound(ResourceLocation.parse(sound), Math.clamp(pitch, 0.1f, 4f)));
        });
        BookVisualTheme.ColorShift unreadTitleShift = null;
        JsonElement shift = merged.get("unreadTitleShift");
        if (shift != null && !shift.isJsonNull()) {
            JsonObject config = shift.getAsJsonObject();
            unreadTitleShift = new BookVisualTheme.ColorShift(BookColors.parse(config.get("from").getAsString()),
                    BookColors.parse(config.get("to").getAsString()), config.get("periodTicks").getAsInt());
        }
        return new BookVisualTheme(id, colors, fonts, textures, decals, sprites,
                new BookVisualTheme.Layout(tabStyle, spread, width, height, redaction, Math.clamp(chance, 0, 1), layout.get("rule").getAsBoolean(), locked),
                new BookVisualTheme.Motion(ticks(motion, "turnTicks"), ticks(motion, "tabTicks"), ticks(motion, "revealTicks")), sounds, false, null, unreadTitleShift);
    }

    private static int ticks(JsonObject motion, String key) { return Math.clamp(motion.get(key).getAsInt(), 0, 1200); }

    private static String choice(JsonObject source, String field, Set<String> choices) {
        String value = source.get(field).getAsString();
        if (!choices.contains(value)) throw new IllegalArgumentException("Invalid " + field + ": " + value);
        return value;
    }

    private static ResourceLocation resolveAsset(List<JsonObject> chain, String group, String role,
                                                 Predicate<ResourceLocation> exists, Consumer<String> warning) {
        for (JsonObject layer : chain) {
            JsonElement field = object(layer, group).get(role);
            if (field != null && field.isJsonNull()) return null;
            ResourceLocation value = asset(field, exists, warning);
            if (value != null) return value;
        }
        return null;
    }

    private static ResourceLocation asset(JsonElement field, Predicate<ResourceLocation> exists, Consumer<String> warning) {
        if (field == null || field.isJsonNull()) return null;
        ResourceLocation value = ResourceLocation.parse(field.getAsString());
        if (exists.test(value)) return value;
        warning.accept("Missing book asset " + value);
        return null;
    }

    private static JsonObject object(JsonObject source, String field) {
        JsonElement value = source.get(field);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
    }

    private static void merge(JsonObject parent, JsonObject child) {
        child.entrySet().forEach(group -> {
            if (group.getValue().isJsonObject()) {
                JsonObject target = object(parent, group.getKey()).deepCopy();
                group.getValue().getAsJsonObject().entrySet().forEach(field -> {
                    boolean required = group.getKey().equals("colors") || (group.getKey().equals("fonts")
                            && Set.of("title", "body").contains(field.getKey()));
                    if (!(required && field.getValue().isJsonNull())) target.add(field.getKey(), field.getValue().deepCopy());
                });
                parent.add(group.getKey(), target);
            } else if (!group.getValue().isJsonNull() || group.getKey().equals("unreadTitleShift"))
                parent.add(group.getKey(), group.getValue().deepCopy());
        });
    }
}
