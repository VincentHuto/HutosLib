package com.vincenthuto.hutoslib.common.effectsource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonParser;

class EffectSourceLensResourceTest {

	private static final Path RESOURCES = Path.of("src/main/resources");

	@Test
	void lensHasLocalizedModelAndTransparentSixteenPixelTexture() throws Exception {
		Path modelPath = RESOURCES.resolve("assets/hutoslib/models/item/effect_source_lens.json");
		Path texturePath = RESOURCES.resolve("assets/hutoslib/textures/item/effect_source_lens.png");
		assertTrue(Files.isRegularFile(modelPath));
		assertTrue(Files.isRegularFile(texturePath));

		var model = JsonParser.parseString(Files.readString(modelPath)).getAsJsonObject();
		assertEquals("hutoslib:item/effect_source_lens",
				model.getAsJsonObject("textures").get("layer0").getAsString());
		assertTrue(Files.readString(RESOURCES.resolve("assets/hutoslib/lang/en_us.json"))
				.contains("\"item.hutoslib.effect_source_lens\""));

		BufferedImage image = ImageIO.read(texturePath.toFile());
		assertEquals(16, image.getWidth());
		assertEquals(16, image.getHeight());
		assertTrue(image.getColorModel().hasAlpha());
	}

	@Test
	void lensHasNoSurvivalRecipe() {
		assertFalse(Files.exists(RESOURCES.resolve("data/hutoslib/recipe/effect_source_lens.json")));
	}
}
