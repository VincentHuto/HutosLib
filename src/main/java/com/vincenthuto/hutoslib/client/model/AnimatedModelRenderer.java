package com.vincenthuto.hutoslib.client.model;

import java.util.List;
import java.util.Map;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class AnimatedModelRenderer extends ModelPart {


	public float defaultRotationX;
	public float defaultRotationY;
	public float defaultRotationZ;
	public float defaultPositionX;
	public float defaultPositionY;
	public float defaultPositionZ;
	public final Map<String, ModelPart> children;
	
	public AnimatedModelRenderer(List<Cube> p_171306_, Map<String, ModelPart> p_171307_) {
		super(p_171306_, p_171307_);
		this.children = null;
		
	}

	public void setDefaultPose() {
		defaultRotationX = xRot;
		defaultRotationY = yRot;
		defaultRotationZ = zRot;
		defaultPositionX = x;
		defaultPositionY = y;
		defaultPositionZ = z;
	}

	public void resetToDefaultPose() {
		xRot = defaultRotationX;
		yRot = defaultRotationY;
		zRot = defaultRotationZ;
		x = defaultPositionX;
		y = defaultPositionY;
		z = defaultPositionZ;
	}

	public void walk(float speed, float degree, boolean invert, float offset, float weight, float limbSwing,
			float limbSwingAmount) {
		float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
		xRot += invert ? -rotation : rotation;
	}

	public void swing(float speed, float degree, boolean invert, float offset, float weight, float limbSwing,
			float limbSwingAmount) {
		float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
		yRot += invert ? -rotation : rotation;
	}

	public void flap(float speed, float degree, boolean invert, float offset, float weight, float limbSwing,
			float limbSwingAmount) {
		float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
		zRot += invert ? -rotation : rotation;
	}

	public void bob(float speed, float degree, boolean bounce, float limbSwing, float limbSwingAmount) {
		y += bounce ? -Math.abs(Mth.sin(limbSwing * speed) * limbSwingAmount * degree)
				: Mth.sin(limbSwing * speed) * limbSwingAmount * degree - limbSwingAmount * degree;
	}

	public void copyRotationsTo(ModelPart box) {
		box.xRot = xRot;
		box.yRot = -yRot;
		box.zRot = -zRot;
	}
}