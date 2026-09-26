package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class BookThemeManager extends SimplePreparableReloadListener<Map<ResourceLocation, BookVisualTheme>> {
    public static final BookThemeManager INSTANCE = new BookThemeManager();
    private static final Logger LOGGER = LogManager.getLogger(BookThemeManager.class);
    private Map<ResourceLocation, BookVisualTheme> themes = BookThemeResolver.resolve(Map.of(), ignored -> true, ignored -> {});
    private final Set<ResourceLocation> warnedMissing = new HashSet<>();
    private long generation;
    private Map<ResourceLocation,int[]> textureSizes=Map.of();
    private Map<ResourceLocation,int[]> preparedSizes=Map.of();

    public long generation() { return generation; }
    public int[] dimensions(ResourceLocation texture) { return textureSizes.getOrDefault(texture,new int[]{64,64}); }

    @Override
    protected Map<ResourceLocation, BookVisualTheme> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonObject> definitions = new HashMap<>();
        Map<ResourceLocation,int[]> sizes=new HashMap<>();
        manager.listResources("book_themes", location -> location.getPath().endsWith(".json")).forEach((location, resource) -> {
            String path = location.getPath();
            ResourceLocation id = location.withPath(path.substring("book_themes/".length(), path.length() - 5));
            try (var reader = resource.openAsReader()) {
                JsonObject definition=JsonParser.parseReader(reader).getAsJsonObject();
                validateAssets(id,definition,manager,sizes);
                definitions.put(id, definition);
            } catch (Exception exception) {
                LOGGER.warn("Cannot load book theme {}", id, exception);
            }
        });
        preparedSizes=Map.copyOf(sizes);
        return BookThemeResolver.resolve(definitions, location -> manager.getResource(location).isPresent(), LOGGER::warn);
    }

    private static void validateAssets(ResourceLocation id,JsonObject definition,ResourceManager manager,Map<ResourceLocation,int[]> sizes) {
        JsonObject sprites=definition.has("sprites")?definition.getAsJsonObject("sprites"):new JsonObject();
        for(BookAtlas region:BookAtlas.values()) {
            var field=sprites.get(region.name());
            if(field!=null&&!field.isJsonNull()&&!validImage(ResourceLocation.parse(field.getAsString()),region.width,region.height,false,manager,sizes)) {
                LOGGER.warn("Book theme {}: {} requires a {}x{} sprite; using inherited region",id,region,region.width,region.height);
                sprites.remove(region.name());
            }
        }
        JsonObject textures=definition.has("textures")?definition.getAsJsonObject("textures"):new JsonObject();
        Map<String,int[]> expected=Map.of("page",new int[]{48,48},"paper",new int[]{64,64},"spread",new int[]{24,232},
                "cover",new int[]{174,228},"ui",new int[]{256,256},"leaf",new int[]{174,1},"toast",new int[]{160,32});
        expected.forEach((role,size)-> {
            var field=textures.get(role);
            if(field!=null&&!field.isJsonNull()&&!validImage(ResourceLocation.parse(field.getAsString()),size[0],size[1],false,manager,sizes)) {
                LOGGER.warn("Book theme {}: {} requires {}x{}; using inherited asset",id,role,size[0],size[1]);textures.remove(role);
            }
        });
        if(textures.has("decals")&&textures.get("decals").isJsonArray()) {
            var valid=new com.google.gson.JsonArray();
            for(var value:textures.getAsJsonArray("decals")) {
                if(validImage(ResourceLocation.parse(value.getAsString()),64,64,true,manager,sizes))valid.add(value);
                else LOGGER.warn("Book theme {}: decal {} is missing or exceeds 64x64",id,value);
            }
            textures.add("decals",valid);
        }
    }

    private static boolean validImage(ResourceLocation texture,int width,int height,boolean maximum,
                                      ResourceManager manager,Map<ResourceLocation,int[]> sizes) {
        int[] size=sizes.get(texture);
        if(size==null) {
            var resource=manager.getResource(texture);if(resource.isEmpty())return false;
            try(var stream=resource.get().open();var image=com.mojang.blaze3d.platform.NativeImage.read(stream)) {
                size=new int[]{image.getWidth(),image.getHeight()};sizes.put(texture,size);
            } catch(java.io.IOException exception) {return false;}
        }
        return maximum?size[0]<=width&&size[1]<=height:size[0]==width&&size[1]==height;
    }

    @Override
    protected void apply(Map<ResourceLocation, BookVisualTheme> prepared, ResourceManager manager, ProfilerFiller profiler) {
        themes = prepared;
        textureSizes=preparedSizes;
        warnedMissing.clear();
        generation++;
    }

    public BookVisualTheme get(ResourceLocation id) {
        BookVisualTheme result = themes.get(id);
        if (result != null) return result;
        if (warnedMissing.add(id)) LOGGER.warn("Missing book theme {}; using hutoslib:default", id);
        return themes.get(BookThemeResolver.DEFAULT_ID);
    }

    public BookVisualTheme resolve(BookCodeModel book, PageTemplate page) {
        return resolve(book, page, null);
    }

    public BookVisualTheme resolve(BookCodeModel book, PageTemplate page, com.vincenthuto.hutoslib.common.data.book.ChapterTemplate chapter) {
        var override = book.getTheme();
        if (override == null && book.getTemplate().getThemeId().isPresent()) return get(book.getTemplate().getThemeId().get());
        BookVisualTheme base = get(BookThemeResolver.DEFAULT_ID);
        ResourceLocation background = page != null ? (override != null && override.backgroundTexture() != null
                ? override.backgroundTexture() : page.getTextureLocation())
                : chapter != null ? chapter.getTextureLocation() : book.getTemplate().getCoverImage();
        Map<String, ResourceLocation> textures = new HashMap<>(base.textures());
        if (background != null) {
            textures.put("page", background);
        }
        textures.put("cover", book.getTemplate().getCoverImage());
        ResourceLocation overlay = override != null && override.backgroundTexture() != null
                ? override.backgroundTexture() : book.getTemplate().getOverlayImage();
        if (overlay != null) textures.put("overlay", overlay);
        Map<String, Integer> colors = new HashMap<>();
        for(String role:List.of("paper","ink","accent"))colors.put(role,base.color(role));
        if (override != null && override.accentColor() != 0) colors.put("accent", override.accentColor() | 0xFF000000);
        colors=BookColors.derive(colors);
        var layout=base.layout();
        layout=new BookVisualTheme.Layout(layout.tabStyle(),"never",layout.pageWidth(),layout.pageHeight(),layout.redaction(),
                layout.decalChance(),layout.rule(),layout.locked());
        return new BookVisualTheme(base.id(), colors, base.fonts(), textures, base.decals(), base.sprites(),
                layout, base.motion(), base.sounds(), true, override == null ? null : override.tabTexture());
    }
}
