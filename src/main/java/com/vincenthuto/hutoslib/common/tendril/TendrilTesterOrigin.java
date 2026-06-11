package com.vincenthuto.hutoslib.common.tendril;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TendrilTesterOrigin {
	private static final double DOWN_OFFSET = 0.65D;
	private static final double FORWARD_OFFSET = 1.15D;
	private static final double SIDE_OFFSET = 0.55D;

	private TendrilTesterOrigin() {
	}

	public static TendrilAnchor playerHandPoint(Player player, InteractionHand hand) {
		return new TendrilAnchor.Point(playerHandPosition(player, hand));
	}

	public static Vec3 playerHandPosition(Player player, InteractionHand hand) {
		return shiftedEyePosition(player.getEyePosition(), player.getLookAngle(), handSide(player, hand));
	}

	static Vec3 shiftedEyePosition(Vec3 eye, Vec3 look, int handSide) {
		Vec3 forward = look.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : look.normalize();
		Vec3 right = horizontalRight(look);
		int side = handSide < 0 ? -1 : 1;
		return eye.add(forward.scale(FORWARD_OFFSET)).add(right.scale(SIDE_OFFSET * side)).add(0.0D,
				-DOWN_OFFSET, 0.0D);
	}

	private static int handSide(Player player, InteractionHand hand) {
		boolean mainHandIsRight = player.getMainArm() == HumanoidArm.RIGHT;
		boolean usedHandIsRight = hand == InteractionHand.MAIN_HAND ? mainHandIsRight : !mainHandIsRight;
		return usedHandIsRight ? 1 : -1;
	}

	private static Vec3 horizontalRight(Vec3 look) {
		Vec3 flat = new Vec3(look.x, 0.0D, look.z);
		if (flat.lengthSqr() < 1.0E-6D) {
			return new Vec3(1.0D, 0.0D, 0.0D);
		}
		flat = flat.normalize();
		return new Vec3(-flat.z, 0.0D, flat.x);
	}
}
