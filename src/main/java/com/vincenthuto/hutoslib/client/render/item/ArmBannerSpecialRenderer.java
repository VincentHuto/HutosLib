package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class ArmBannerSpecialRenderer implements SpecialModelRenderer<ArmBannerSpecialRenderer.RenderData> {
	public static final Identifier TYPE = HutosLib.rloc("arm_banner");

	private final ModelArmBanner model;
	private final SpriteGetter sprites;
	private final Identifier texture;
	private final Profile profile;

	public ArmBannerSpecialRenderer(ModelArmBanner model, SpriteGetter sprites, Identifier texture) {
		this(model, sprites, texture, Profile.HELD);
	}

	public ArmBannerSpecialRenderer(ModelArmBanner model, SpriteGetter sprites, Identifier texture, Profile profile) {
		this.model = model;
		this.sprites = sprites;
		this.texture = texture;
		this.profile = profile;
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
		this.applyProfileTransform(poseStack);
		this.applyShoulderTransform(poseStack);
		nodes.submitModel(this.model, ModelArmBanner.State.SHOULDER, poseStack, this.texture, light, overlay,
				outlineColor, null);
		poseStack.popPose();

		if (data.hasBannerData()) {
			poseStack.pushPose();
			this.applyProfileTransform(poseStack);
			this.applyPlateTransform(poseStack);
			BannerRenderer.submitPatterns(this.sprites, poseStack, nodes, light, overlay, this.model,
					ModelArmBanner.State.PLATE, false, data.baseColor(), data.patterns(), null);
			poseStack.popPose();
		}
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack shoulderPose = new PoseStack();
		this.applyProfileTransform(shoulderPose);
		this.applyShoulderTransform(shoulderPose);
		this.model.root().getExtentsForGui(shoulderPose, output);

		PoseStack platePose = new PoseStack();

		this.applyProfileTransform(platePose);
		this.applyPlateTransform(platePose);
		this.model.root().getExtentsForGui(platePose, output);
	}

	private void applyProfileTransform(PoseStack poseStack) {
		if (this.profile == Profile.GUI) {
			poseStack.translate(-1.25, -0.25, -0.53);
			poseStack.scale(0.45F, 0.45F, 0.45F);
			poseStack.mulPose(new Quaternion(Vector3.ZP, -73.5F, true).toMoj());
			poseStack.translate(0.2, 1.66, 1.25);
			poseStack.mulPose(new Quaternion(Vector3.ZP, 180.0F, true).toMoj());
		}
	}

	private void applyShoulderTransform(PoseStack poseStack) {
		poseStack.scale(4.1F, 5.0F, 4.1F);
		poseStack.translate(-0.21, -0.32, 0.2);

		poseStack.mulPose(new Quaternion(Vector3.ZP, -105.0F, true).toMoj());
		poseStack.mulPose(new Quaternion(Vector3.YP, this.profile.shoulderYRot(), true).toMoj());
		if (this.profile == Profile.GUI) {
			poseStack.mulPose(new Quaternion(Vector3.YP, 180.0F, true).toMoj());
		}
	}

	private void applyPlateTransform(PoseStack poseStack) {
		poseStack.scale(1.0F, -1.0F, -1.0F);
		poseStack.translate(0, 0.05, -0.25);
		poseStack.mulPose(new Quaternion(Vector3.ZN, 75.0F, true).toMoj());
		poseStack.scale(1.7F, 1.7F, 1.7F);
		if (this.profile == Profile.GUI) {
			poseStack.mulPose(new Quaternion(Vector3.YP, 180.0F, true).toMoj());
			poseStack.translate(1, 0.3, -1);
		} else {
			poseStack.mulPose(new Quaternion(Vector3.YP, 180.0F, true).toMoj());
			poseStack.translate(1, 0.3, -1);
		}
	}

	public record RenderData(DyeColor baseColor, BannerPatternLayers patterns, boolean hasBannerData) {
	}

	public enum Profile implements StringRepresentable {
		HELD("held", 90.0F),
		GUI("gui", -90.0F);

		public static final Codec<Profile> CODEC = StringRepresentable.fromEnum(Profile::values);
		private final String serializedName;
		private final float shoulderYRot;

		Profile(String serializedName, float shoulderYRot) {
			this.serializedName = serializedName;
			this.shoulderYRot = shoulderYRot;
		}

		@Override
		public String getSerializedName() {
			return this.serializedName;
		}

		public float shoulderYRot() {
			return this.shoulderYRot;
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
		public SpecialModelRenderer<RenderData> bake(BakingContext context) {
			return new ArmBannerSpecialRenderer(
					new ModelArmBanner(context.entityModelSet().bakeLayer(HutosLibModelLayersInit.arm_banner)),
					context.sprites(),
					this.texture,
					this.profile);
		}

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}
	}
}
