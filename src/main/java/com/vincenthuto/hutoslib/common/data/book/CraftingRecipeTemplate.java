package com.vincenthuto.hutoslib.common.data.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuideCraftingPage;
import com.vincenthuto.hutoslib.common.data.shadow.LazySupplier;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;
import com.vincenthuto.hutoslib.common.data.shadow.RecipeHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingRecipeTemplate extends BookDataTemplate implements IPageTemplate{
	private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<>(RecipeHelper::new);

	public static final Codec<CraftingRecipeTemplate> CODEC = RecordCodecBuilder.create(inst -> inst
			.group(Codec.INT.fieldOf("ordinality").forGetter(CraftingRecipeTemplate::getOrdinality),
					Codec.STRING.fieldOf("texture").forGetter(CraftingRecipeTemplate::getTexture),
					Codec.STRING.fieldOf("title").forGetter(CraftingRecipeTemplate::getTitle),
					Codec.STRING.fieldOf("subtitle").forGetter(CraftingRecipeTemplate::getSubtitle),
					Codec.STRING.fieldOf("text").forGetter(CraftingRecipeTemplate::getText),
					Codec.STRING.fieldOf("icon").forGetter(CraftingRecipeTemplate::getIcon))
			.apply(inst, CraftingRecipeTemplate::new));
	public static final PSerializer<CraftingRecipeTemplate> SERIALIZER = PSerializer.fromCodec("craftingpage",
			CODEC);

	String title, chapter, subtitle, text, icon, texture;

	public CraftingRecipeTemplate(int ordinality) {
		super(0);

	}

	public CraftingRecipeTemplate(int ordinality, String texture, String title, String subtitle, String text,
			String icon) {
		super(ordinality);
		this.texture = texture;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.icon = icon;

	}
	
	public GhostRecipe getItemRecipe() {
		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);

		var recipeStream = world.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
				.filter(r -> r.getResultItem(world.registryAccess()).getItem() == getIconItem().getItem());
		var recipes = recipeStream.toList();

		GhostRecipe ghost = new GhostRecipe();
		if (!recipes.isEmpty()) {
			float time = Minecraft.getInstance().level.getGameTime();
			var test = Mth.floor(time / 30.0F) % recipes.size();
			var currentRecipe = recipes.get(test);
			ghost.setRecipe(currentRecipe);
			ItemStack itemstack = currentRecipe.getResultItem(world.registryAccess());
			ghost.addIngredient(Ingredient.of(itemstack), 1 * 18 + 98, 1 * 18 + 31);

			List<List<ItemStack>> inputs = currentRecipe.getIngredients().stream()
					.map(ingredient -> List.of(ingredient.getItems())).toList();

			int w = recipeHelper.get().getWidth(currentRecipe);
			int h = recipeHelper.get().getHeight(currentRecipe);
			List<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
			for (int y = 0; y < 3; ++y) {
				for (int x = 0; x < 3; ++x) {
					coords.add(Pair.of(x * 18 + 29, y * 18 + 31));
				}
			}

			for (int i = 0; i < inputs.size(); i++) {
				int index = getCraftingIndex(i, w, h);
				var coord = coords.get(index);
				var ingredient = currentRecipe.getIngredients().get(i);
				ghost.addIngredient(ingredient, coord.getLeft(), coord.getRight());
			}

		}

		return ghost;
	}

	public static int[][] getCoordinates(int position) {
		// Check if the position is within the valid range
		if (position < 0 || position > 8) {
			System.out.println("Invalid input. Please provide a number between 0 and 8.");
			return null;
		}

		int i = position / 3; // Row index
		int j = position % 3; // Column index

		return new int[][] { { i, j } };
	}

	private static int getCraftingIndex(int i, int width, int height) {
		int index;
		if (width == 1) {
			if (height == 3) {
				index = (i * 3) + 1;
			} else if (height == 2) {
				index = (i * 3) + 1;
			} else {
				index = 4;
			}
		} else if (height == 1) {
			index = i + 3;
		} else if (width == 2) {
			index = i;
			if (i > 1) {
				index++;
				if (i > 3) {
					index++;
				}
			}
		} else if (height == 2) {
			index = i + 3;
		} else {
			index = i;
		}
		return index;
	}

	public ResourceLocation getTextureLocation() {

		return HLLocHelper.getBySplit(texture);

	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "Page number: " + getOrdinality() + ", Title: " + title;
	}

	@Override
	public PSerializer<? extends BookDataTemplate> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
		HLGuiGuideCraftingPage.openScreenViaItem(pageNum, book, chapter);
	}

}
