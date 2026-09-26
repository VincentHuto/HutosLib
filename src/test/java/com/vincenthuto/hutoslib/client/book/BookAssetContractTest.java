package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class BookAssetContractTest {
    @Test
    void shippedNeutralTexturesAndBadgeMatchTheNativePixelContract() throws Exception {
        Path assets = Path.of("src/main/resources/assets/hutoslib");
        for (var entry : Map.of("page", new int[]{48,48}, "paper", new int[]{64,64}, "spread", new int[]{24,232},
                "cover", new int[]{174,228}, "ui", new int[]{256,256}, "leaf", new int[]{174,1}, "toast", new int[]{160,32}).entrySet()) {
            var image = ImageIO.read(assets.resolve("textures/gui/book/default/" + entry.getKey() + ".png").toFile());
            assertEquals(entry.getValue()[0], image.getWidth(), entry.getKey());
            assertEquals(entry.getValue()[1], image.getHeight(), entry.getKey());
        }
        var atlas = ImageIO.read(assets.resolve("textures/gui/book/default/ui.png").toFile());
        for (BookAtlas region : BookAtlas.values()) {
            boolean hasPixels = false;
            for (int x = region.u; x < region.u + region.width; x++)
                for (int y = region.v; y < region.v + region.height; y++) hasPixels |= (atlas.getRGB(x,y) >>> 24) != 0;
            assertTrue(hasPixels, "Empty atlas region " + region);
        }
        var font = JsonParser.parseString(Files.readString(assets.resolve("font/book_badge.json"))).getAsJsonObject();
        assertEquals(5, font.getAsJsonArray("providers").get(0).getAsJsonObject().get("height").getAsInt());
        var digits = ImageIO.read(assets.resolve("textures/font/book_badge.png").toFile());
        assertEquals(30, digits.getWidth());
        assertEquals(5, digits.getHeight());
    }
}
