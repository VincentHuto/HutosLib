package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.registry.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class ArmBannerSpecialRenderer implements SpecialModelRenderer<ArmBannerSpecialRenderer.RenderData> {
	public static final Identifier TYPE = HutosLib.rloc("arm_banner");

	private final ModelArmBanner model;
	private final SpriteGetter sprites;
	private final Identifier texture;

	public ArmBannerSpecialRenderer(ModelArmBanner model, SpriteGetter sprites, Identifier texture) {
		this.model = model;
		this.sprites = sprites;
		this.texture = texture;
	}

	@Override
	public RenderData extractArgument(ItemStack stack) {
		DyeColor baseColor = stack.get(DataComponents.BASE_COLOR);
		BannerPatternLayers patterns = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
		return new RenderData(
				baseColor != null ? baseColor : DyeColor.WHITE,
				patterns,
				baseColor != null || !patterns.layers().isEmpty());
	}

	@Override
	public void submit(RenderData data, PoseStack poseStack, SubmitNodeCollector nodes, int light, int overlay,
			boolean foil, int outlineColor) {
		poseStack.pushPose();
		poseStack.scale(4.1F, 5.0F, 4.1F);
		poseStack.translate(-0.21, 0.02, -0.53);
		poseStack.mulPose(new Quaternion(Vector3.ZP, -105.0F, true).toMoj());
		poseStack.mulPose(new Quaternion(Vector3.YP, 90.0F, true).toMoj());
		nodes.submitModel(this.model, ModelArmBanner.State.SHOULDER, poseStack, this.texture, light, overlay,
				outlineColor, null);
		poseStack.popPose();

		if (data.hasBannerData()) {
			poseStack.pushPose();
			poseStack.scale(1.0F, -1.0F, -1.0F);
			poseStack.translate(0, 0.05, -0.25);
			poseStack.mulPose(new Quaternion(Vector3.ZN, 75.0F, true).toMoj());
			poseStack.scale(1.7F, 1.7F, 1.7F);
			BannerRenderer.submitPatterns(this.sprites, poseStack, nodes, light, overlay, this.model,
					ModelArmBanner.State.PLATE, false, data.baseColor(), data.patterns(), null);
			poseStack.popPose();
		}
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		this.model.root().getExtentsForGui(new PoseStack(), output);
	}

	public record RenderData(DyeColor baseColor, BannerPatternLayers patterns, boolean hasBannerData) {
	}

	public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked<RenderData> {
		public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture")
				.xmap(Unbaked::new, Unbaked::texture);

		@Override
		public SpecialModelRenderer<RenderData> bake(BakingContext context) {
			return new ArmBannerSpecialRenderer(
					new ModelArmBanner(context.entityModelSet().bakeLayer(HutosLibModelLayersInit.arm_banner)),
					context.sprites(),
					this.texture);
		}

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}
	}
}
