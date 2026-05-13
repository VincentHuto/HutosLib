package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class GuideBookSpecialRenderer implements NoDataSpecialModelRenderer {
	public static final Identifier TYPE = HutosLib.rloc("guide_book");
	public static final Identifier DEFAULT_TEXTURE = HutosLib.rloc("textures/gui/hl_guide_book_text_default.png");

	private final BookModel model;
	private final BookModel.State state;
	private final Identifier texture;

	public GuideBookSpecialRenderer(BookModel model, Identifier texture) {
		this.model = model;
		this.texture = texture;
		this.state = BookModel.State.forAnimation(0.65F, 0.15F, 0.85F, 0.85F);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodes, int light, int overlay, boolean foil,
			int outlineColor) {
		nodes.submitModel(this.model, this.state, poseStack, this.texture, light, overlay, outlineColor, null);
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		this.model.root().getExtentsForGui(new PoseStack(), output);
	}

	public record Unbaked(Identifier texture) implements NoDataSpecialModelRenderer.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture")
				.xmap(Unbaked::new, Unbaked::texture);

		@Override
		public GuideBookSpecialRenderer bake(BakingContext context) {
			return new GuideBookSpecialRenderer(new BookModel(context.entityModelSet().bakeLayer(ModelLayers.BOOK)),
					this.texture);
		}

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}
	}
}
