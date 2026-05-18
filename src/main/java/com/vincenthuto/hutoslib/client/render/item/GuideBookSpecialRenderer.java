package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.BookAnimState;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class GuideBookSpecialRenderer implements SpecialModelRenderer<GuideBookSpecialRenderer.RenderData> {
	public static final Identifier TYPE = HutosLib.rloc("guide_book");
	public static final Identifier DEFAULT_TEXTURE = HutosLib.rloc("textures/gui/hl_guide_book_text_default.png");
	private static final float GUI_MAX_OPENNESS = 0.78F;

	private final BookModel model;
	private final Identifier texture;
	private final Profile profile;

	public GuideBookSpecialRenderer(BookModel model, Identifier texture) {
		this(model, texture, Profile.HELD);
	}

	public GuideBookSpecialRenderer(BookModel model, Identifier texture, Profile profile) {
		this.model = model;
		this.texture = texture;
		this.profile = profile;
	}

	@Override
	public RenderData extractArgument(ItemStack stack) {
		return new RenderData(this.createModelState(stack));
	}

	@Override
	public void submit(RenderData data, PoseStack poseStack, SubmitNodeCollector nodes, int light, int overlay, boolean foil,
			int outlineColor) {
		poseStack.pushPose();
		this.applyProfileTransform(poseStack);
		nodes.submitModel(this.model, data.state(), poseStack, this.texture, light, overlay, outlineColor, null);
		poseStack.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack poseStack = new PoseStack();
		this.applyProfileTransform(poseStack);
		this.model.root().getExtentsForGui(poseStack, output);
	}

	private void applyProfileTransform(PoseStack poseStack) {
		if (this.profile == Profile.GUI) {
			poseStack.translate(0.50D, -0.60D, 0.0D);
			poseStack.scale(0.90F, 0.90F, 0.90F);
			poseStack.mulPose(Vector3.XN.rotationDegrees(48.0F).toMoj());
			poseStack.mulPose(Vector3.YP.rotationDegrees(76.0F).toMoj());
			poseStack.mulPose(Vector3.ZP.rotationDegrees(36.0F).toMoj());
		}
	}

	private BookModel.State createModelState(ItemStack stack) {
		Player player = Minecraft.getInstance().player;
		BookAnimState state = ItemGuideBook.getOrCreateState(player != null ? player.getUUID() : null);
		if (this.profile == Profile.GUI) {
			float openness = player != null && (isSelectedGuideBookStack(player, stack) || state.close > 0.0F)
					? Mth.clamp(state.close, 0.0F, GUI_MAX_OPENNESS)
					: 0.0F;
			return BookModel.State.forAnimation(0.65F, 0.15F, 0.85F, openness);
		}

		float ticks = state.ticks + 1F;
		float pageFlip = Mth.lerp(1.0F, state.oFlip, state.flip);
		float pageFlip1 = Mth.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
		float pageFlip2 = Mth.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
		return BookModel.State.forAnimation(ticks, Mth.clamp(pageFlip1, 0.0F, 1.0F),
				Mth.clamp(pageFlip2, 0.0F, 1.0F), state.close);
	}

	private static boolean isSelectedGuideBookStack(Player player, ItemStack stack) {
		return isMatchingGuideBookStack(stack, player.getMainHandItem())
				|| isMatchingGuideBookStack(stack, player.getOffhandItem());
	}

	private static boolean isMatchingGuideBookStack(ItemStack renderedStack, ItemStack heldStack) {
		return heldStack.getItem() instanceof ItemGuideBook
				&& (renderedStack == heldStack || ItemStack.matches(renderedStack, heldStack));
	}

	public record RenderData(BookModel.State state) {
	}

	public enum Profile implements StringRepresentable {
		HELD("held"),
		GUI("gui");

		public static final Codec<Profile> CODEC = StringRepresentable.fromEnum(Profile::values);
		private final String serializedName;

		Profile(String serializedName) {
			this.serializedName = serializedName;
		}

		@Override
		public String getSerializedName() {
			return this.serializedName;
		}
	}

	public record Unbaked(Identifier texture, Profile profile) implements SpecialModelRenderer.Unbaked<RenderData> {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
				Profile.CODEC.optionalFieldOf("profile", Profile.HELD).forGetter(Unbaked::profile))
				.apply(instance, Unbaked::new));

		public Unbaked(Identifier texture) {
			this(texture, Profile.HELD);
		}

		@Override
		public GuideBookSpecialRenderer bake(BakingContext context) {
			return new GuideBookSpecialRenderer(new BookModel(context.entityModelSet().bakeLayer(ModelLayers.BOOK)),
					this.texture,
					this.profile);
		}

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}
	}
}
