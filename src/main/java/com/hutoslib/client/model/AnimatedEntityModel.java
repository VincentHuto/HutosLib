package com.hutoslib.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.hutoslib.math.MathUtils;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public abstract class AnimatedEntityModel<T extends Entity> extends EntityModel<T> {
	public T entity;
	public float globalSpeed = 0.5f;
	public final List<ModelPart> boxList = new ArrayList<>();
	public float time;

	public AnimatedEntityModel() {
	}

	public AnimatedEntityModel(Function<ResourceLocation, RenderType> type) {
		super(type);
	}

	public void setDefaultPose() {
		for (ModelPart box : boxList)
			if (box instanceof AnimatedModelRenderer)
				((AnimatedModelRenderer) box).setDefaultPose();
	}

	public void resetToDefaultPose() {
		globalSpeed = 0.5f;
		for (ModelPart box : boxList)
			if (box instanceof AnimatedModelRenderer)
				((AnimatedModelRenderer) box).resetToDefaultPose();
	}

	public void setRotateAngle(ModelPart model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	public void faceTarget(float yaw, float pitch, float rotationDivisor, ModelPart... boxes) {
		rotationDivisor *= boxes.length;
		yaw = (float) Math.toRadians(yaw) / rotationDivisor;
		pitch = (float) Math.toRadians(pitch) / rotationDivisor;

		for (ModelPart box : boxes) {
			box.xRot += pitch;
			box.yRot += yaw;
		}
	}

	/**
	 * Rotate Angle X
	 */
	public void walk(AnimatedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight,
			float walk, float walkAmount) {
		box.walk(speed, degree, invert, offset, weight, walk, walkAmount);
	}

	/**
	 * Rotate Angle Z
	 */
	public void flap(AnimatedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight,
			float flap, float flapAmount) {
		box.flap(speed, degree, invert, offset, weight, flap, flapAmount);
	}

	/**
	 * Rotate Angle Y
	 */
	public void swing(AnimatedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight,
			float swing, float swingAmount) {
		box.swing(speed, degree, invert, offset, weight, swing, swingAmount);
	}

	/**
	 * Bob the box up and down
	 *
	 * @param bounce back and forth
	 */
	public void bob(AnimatedModelRenderer box, float speed, float degree, boolean bounce, float limbSwing,
			float limbSwingAmount) {
		box.bob(speed, degree, bounce, limbSwing, limbSwingAmount);
	}

	/**
	 * Chain Wave (rotateAngleX)
	 */
	public void chainWave(ModelPart[] boxes, float speed, float degree, double rootOffset, float swing,
			float swingAmount) {
		float offset = calculateChainOffset(rootOffset, boxes);
		for (int index = 0; index < boxes.length; ++index)
			boxes[index].xRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
	}

	/**
	 * Chain Swing (rotateAngleY)
	 */
	public void chainSwing(ModelPart[] boxes, float speed, float degree, double rootOffset, float swing,
			float swingAmount) {
		float offset = calculateChainOffset(rootOffset, boxes);
		for (int index = 0; index < boxes.length; ++index)
			boxes[index].yRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
	}

	/**
	 * Chain Flap (rotateAngleZ)
	 */
	public void chainFlap(ModelPart[] boxes, float speed, float degree, double rootOffset, float swing,
			float swingAmount) {
		float offset = calculateChainOffset(rootOffset, boxes);
		for (int index = 0; index < boxes.length; ++index)
			boxes[index].zRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
	}

	private float calculateChainRotation(float speed, float degree, float swing, float swingAmount, float offset,
			int boxIndex) {
		return Mth.cos(swing * speed + offset * boxIndex) * swingAmount * degree;
	}

	private float calculateChainOffset(double rootOffset, ModelPart... boxes) {
		return (float) rootOffset * MathUtils.PI / (2f * boxes.length);
	}

	public void setTime(float x) {
		this.time = x;
	}

	public void toDefaultPose() {
		for (ModelPart modelRenderer : boxList) {
			if (modelRenderer instanceof AnimatedModelRenderer) {
				AnimatedModelRenderer box = (AnimatedModelRenderer) modelRenderer;
				box.x = MathUtils.linTerp(box.x, box.defaultPositionX, time);
				box.y = MathUtils.linTerp(box.y, box.defaultPositionY, time);
				box.z = MathUtils.linTerp(box.z, box.defaultPositionZ, time);
				box.xRot = MathUtils.linTerp(box.xRot, box.defaultRotationX, time);
				box.yRot = MathUtils.linTerp(box.yRot, box.defaultRotationY, time);
				box.zRot = MathUtils.linTerp(box.zRot, box.defaultRotationZ, time);
			}
		}
	}

	public void move(ModelPart box, float x, float y, float z) {
		box.x += time * x;
		box.y += time * y;
		box.z += time * z;
	}

	public void rotate(ModelPart box, float x, float y, float z) {
		box.xRot += time * x;
		box.yRot += time * y;
		box.zRot += time * z;
	}

	public void idle(float frame) {
	}

	public float getAnimationSwingDelta(float speed, float tick, float partialTick) {
		float end = Mth.clamp(-(tick / speed) + 1, 0, 1);
		float start = Mth.clamp(-((tick - 1f) / speed) + 1, 0, 1);
		return Mth.lerp(partialTick, start, end);
	}

	public void idle(float frame, float limbSwing, float limbSwingAmount) {

	}
}