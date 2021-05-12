package com.hutoslib.math;

import java.awt.Point;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Maf utility class to make my life like way easier.
 * <p>
 * Half of this shit is just me throwing numbers in and hoping it works, seems
 * to be going well so far!
 */
public final class MathUtils {
	private MathUtils() {
		/* good try */}

	/**
	 * Float Version of PI. Why? so we don't have to cast the fucking official one
	 * 314159265358979323846 (heh) times
	 */
	public static final float PI = (float) Math.PI;

	/**
	 * Returns a new pseudo random double value constrained to the values of
	 * {@code (-1.0d)} and {@code (1.0d)}
	 */
	public static double nextDouble(Random rand) {
		return 2 * rand.nextDouble() - 1;
	}

	/**
	 * A good way to get a position offset by the direction of a yaw angle.
	 */
	public static Vector3d getYawVec(float yaw, double xOffset, double zOffset) {
		return new Vector3d(xOffset, 0, zOffset).rotateYaw(-yaw * (PI / 180f));
	}

	/**
	 * Get the angle between 2 sources
	 *
	 * TODO: Adjust so that the angle is closest to 0 in the SOUTH direction!,
	 * currently it is only doing it for east!
	 */
	public static double getAngle(double sourceX, double sourceZ, double targetX, double targetZ) {
		return MathHelper.atan2(targetZ - sourceZ, targetX - sourceX) * 180 / Math.PI + 180;
	}

	public static double getAngle(Entity source, Entity target) {
		return MathHelper.atan2(target.getPosZ() - source.getPosZ(), target.getPosX() - source.getPosX())
				* (180 / Math.PI) + 180;
	}

	/**
	 * Clamped (0-1) Linear Interpolation (Float version)
	 */
	public static float linTerp(float a, float b, float x) {
		if (x <= 0)
			return a;
		if (x >= 1)
			return b;
		return a + x * (b - a);
	}

	public static Point rotatePointAbout(Point in, Point about, double degrees) {
		double rad = degrees * Math.PI / 180.0;
		double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
		double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
		return new Point((int) newX, (int) newY);
	}

}