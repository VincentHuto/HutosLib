package com.vincenthuto.hutoslib.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.ArrayList;
import java.util.List;

public class ModelArmBanner extends Model<ModelArmBanner.State> {
	public record State(boolean plateOnly) {
		public static final State SHOULDER = new State(false);
		public static final State PLATE = new State(true);
	}
	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition leftShoulder = partdefinition.addOrReplaceChild("leftShoulder",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-1.0F, -4.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 7)
						.addBox(3.0F, -2.0F, -2.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(4.0F, -3.75F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(7, 7)
						.addBox(-1.0F, -5.0F, -1.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(15, 0)
						.addBox(-1.0F, -2.5F, 2.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 11)
						.addBox(-1.0F, -2.5F, -3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(14, 16)
						.addBox(-1.0F, -3.5F, -3.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(6, 16)
						.addBox(-1.0F, -3.5F, 2.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(5.25F, 1.75F, 0.0F));
		partdefinition.addOrReplaceChild("plate",
				CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F),
				PartPose.offsetAndRotation(0, -2.0F,0, 0,0,0));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public final List<ModelPart> parts = new ArrayList<>();
	public final ModelPart leftShoulder;

	private final ModelPart plate;
	private final ModelPart root;

	public ModelArmBanner(ModelPart root) {
		super(root, RenderTypes::entitySolid);
		this.root = root;
		this.leftShoulder = root.getChild("leftShoulder");
		this.plate = root.getChild("plate");

	}

	public ModelPart plate() {
		return this.plate;
	}

	public void renderShoulder(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
			int color) {
		leftShoulder.render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	@Override
	public void setupAnim(State state) {
		this.leftShoulder.visible = !state.plateOnly();
		this.plate.visible = state.plateOnly();
	}

}
