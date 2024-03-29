package com.vincenthuto.hutoslib.client.particle.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HLParticleUtils {
	public static Random r = new Random();

	/***
	 *
	 * @param toThisBlock
	 * @param fromThisBlock
	 * @param world
	 * @param type          DEFAULT ParticleTypes.ENCHANT
	 * @param type2         DEFAULT ParticleTypes.WITCH
	 */
	public static void beam(BlockPos toThisBlock, BlockPos fromThisBlock, Level world, ParticleOptions type,
			ParticleOptions type2) {

		double x2 = getCenterOfBlock(toThisBlock.getX());
		double z2 = getCenterOfBlock(toThisBlock.getZ());
		double y2 = getCenterOfBlock(toThisBlock.getY());
		double x1 = getCenterOfBlock(fromThisBlock.getX());
		double z1 = getCenterOfBlock(fromThisBlock.getZ());
		double y1 = getCenterOfBlock(fromThisBlock.getY());
		double d5 = 1.2;
		double d0 = x2 - x1;
		double d1 = y2 - y1;
		double d2 = z2 - z1;
		double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
		d0 = d0 / d3;
		d1 = d1 / d3;
		d2 = d2 / d3;

		double d4 = r.nextDouble();

		while ((d4 + .65) < d3) {
			d4 += 1.8D - d5 + r.nextDouble() * (1.5D - d5);
			if (world.isClientSide)
				world.addParticle(type, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, 0.0D, 0.0D, 0.0D);
			if (world instanceof ServerLevel) {
				((ServerLevel) world).sendParticles(type2, x1 + d0 * d4, y1 + d1 * d4, z1 + d2 * d4, r.nextInt(4), 0,
						0.0, 0, 0.0);
			}
		}
	}

	/*
	 * Like a lotus fountain with point twords y+
	 */
	public static Vec3[] bloomingFlower(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = -Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.15);
			double radius = Math.cbrt(-Math.max(Math.sqrt(1 - y * y) * 1, 1.0));
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 2;
			points[i] = new Vec3(x * Math.pow(Math.cos(rotThing) * radMod, exp),
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.cos(rotThing), exp) * radMod);

		}
		return points;
	}

	/*
	 * Like a lotus fountain with point twords y-
	 */
	public static Vec3[] bloomingFlowerFlip(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.15);
			double radius = Math.max(Math.sqrt(1 - y * y) * 1, 0.75);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.cos(rotThing) * radMod, exp),
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.cos(rotThing), exp) * radMod);

		}
		return points;
	}

	/*
	 * A misty cloud that grows up and down in a blazar shape
	 */
	public static Vec3[] cosmicBirth(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.25);
			double radius = Math.sqrt(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0. ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.sin(rotThing), exp) * radMod,
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.sin(rotThing), exp) * radMod);

		}
		return points;
	}

	/*
	 * A misty cloud that grows down and up in a blazar shape
	 */
	public static Vec3[] cosmicBirthFlip(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		// odd numbers = horizontal lines
		// even numbers = vertical lines
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = -Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.25);
			double radius = Math.sqrt(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0. ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.sin(rotThing), exp) * radMod,
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.sin(rotThing), exp) * radMod);

		}
		return points;
	}

	/*
	 * Like Cosmic Birth but Large on outside and not inside
	 */
	public static Vec3[] cosmicBirthInverse(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.25);
			double radius = Math.exp(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0. ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.sin(rotThing), exp) * radMod,
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.sin(rotThing), exp) * radMod);

		}
		return points;
	}

	/*
	 * Like Cosmic Birth but Large on outside and not inside
	 */
	public static Vec3[] cosmicBirthInverseFlip(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = -Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.25);
			double radius = Math.exp(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0. ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.sin(rotThing) * radMod, exp),
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.sin(rotThing), exp) * radMod);

		}
		return points;
	}

	public static ParticleColor defaultParticleColor() {
		return new ParticleColor(255, 25, 180);
	}

	public static ParticleColor.IntWrapper defaultParticleColorWrapper() {
		return new ParticleColor.IntWrapper(255, 25, 180);
	}

	/*
	 * Fibbonachi Sphere with vertical Lines
	 */
	public static Vec3[] fibboSphere(int numPoint, double rotMod, double radMod) {
		Vec3[] points = new Vec3[numPoint];
		// odd numbers = horizontal lines
		// even numbers = vertical lines
		double phiE = Math.PI * (3.0 - Math.sqrt(.35)); // Golden angle in radians
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2; // y goes from 1 to -1
			double radius = Math.sqrt(1 - y * y);
			double theta = phiE * i; // Golden angle Increment
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			// Fibonachi sphere
			points[i] = new Vec3(x * radMod, y * radMod, z * radMod);

		}
		return points;
	}

	public static double getCenterOfBlock(double a) {
		return (a + .5);
	}

	/*
	 * An Imploding Sphere Shape
	 */
	public static Vec3[] implode(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		// odd numbers = horizontal lines
		// even numbers = vertical lines
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			points[i] = new Vec3(x * Math.sqrt(Math.pow(Math.tan(rotThing), 0.75)) * radMod,
					y * Math.sqrt(Math.pow(Math.tan(rotThing), 0.75)) * radMod,
					z * Math.sqrt(Math.pow(Math.tan(rotThing), 0.75)) * radMod);

		}
		return points;
	}

	public static double inRange(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	/*
	 * Starting at a small point shoots rays out to the radius
	 */
	public static Vec3[] inversedSphere(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.sin(rotThing), exp) * radMod,
					y * Math.pow(Math.sin(rotThing), exp * 1) * radMod, z * Math.pow(Math.sin(rotThing), exp) * radMod);

		}
		return points;
	}

	public static Vec3[] lotusFountain(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !!isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = -Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.15);
			double radius = Math.sqrt(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.cos(rotThing) * radMod, exp),
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.cos(rotThing), exp) * radMod);

		}
		return points;
	}

	public static Vec3[] lotusFountainFlip(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = Math.abs(Math.sin(rotMod) - (i / ((float) numPoint - 1)) * 0.15);
			double radius = Math.sqrt(1 - y * y) * 1;
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			double exp = 3;
			points[i] = new Vec3(x * Math.pow(Math.cos(rotThing) * radMod, exp),
					y * Math.pow(Math.sin(rotThing), exp * 2) * radMod, z * Math.pow(Math.cos(rotThing), exp) * radMod);

		}
		return points;
	}

	// https://karthikkaranth.me/blog/generating-random-points-in-a-sphere/
	public static Vec3 pointInSphere() {
		double u = Math.random();
		double v = Math.random();
		double theta = u * 2.0 * Math.PI;
		double phi = Math.acos(2.0 * v - 1.0);
		double r = Math.cbrt(Math.random());
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);
		double sinPhi = Math.sin(phi);
		double cosPhi = Math.cos(phi);
		double x = r * sinPhi * cosTheta;
		double y = r * sinPhi * sinTheta;
		double z = r * cosPhi;
		return new Vec3(x, y, z);
	}

	/*
	 * A cloud of ever changing particles sorta like an electron cloud
	 */
	public static Vec3[] randomSphere(int numPoint, double rotMod, double radMod) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			points[i] = new Vec3(x * radMod, y * radMod, z * radMod);

		}
		return points;
	}

	/*
	 * Like a School of swimming tetras
	 */
	public static Vec3[] randomSwimming(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			points[i] = new Vec3(x + Math.cos(rotThing) * radMod, y * Math.pow(Math.sin(radius), 3),
					z + Math.sin(rotThing) * radMod);

		}
		return points;
	}

	public static void spawnPoof(ServerLevel world, BlockPos pos, ParticleOptions type) {
		for (int i = 0; i < 10; i++) {
			double d0 = pos.getX() + 0.5;
			double d1 = pos.getY() + 1.2;
			double d2 = pos.getZ() + .5;
			(world).sendParticles(type, d0, d1, d2, 2, (world.random.nextFloat() * 1 - 0.5) / 3,
					(world.random.nextFloat() * 1 - 0.5) / 3, (world.random.nextFloat() * 1 - 0.5) / 3, 0.1f);
		}
	}

	/*
	 * Pulls in the X direction and Pinches in the Z
	 */
	public static Vec3[] squashAndStretch(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			points[i] = new Vec3(x * Math.pow(Math.tan(rotThing) * radMod, -0.35), y * radMod,
					z * Math.pow(Math.tan(rotThing), 0.35) * radMod);

		}
		return points;
	}

	/*
	 * Like Imploding Sphere except its always funneling down
	 */
	public static Vec3[] tangentFunnel(int numPoint, double rotMod, double radMod, boolean isRand) {
		Vec3[] points = new Vec3[numPoint];
		// odd numbers = horizontal lines
		// even numbers = vertical lines
		double phiX = !isRand ? Math.PI * (2.6 - Math.sqrt(.1135)) : Math.PI * (2.6 - Math.sqrt(Math.random()));
		double phiO = !isRand ? Math.PI * (3.0 - Math.sqrt(.75)) : Math.PI * (3.0 - Math.sqrt(Math.random()));
		double phiE = !isRand ? Math.PI * (2.0 - Math.sqrt(.35)) : Math.PI * (2.0 - Math.sqrt(Math.random()));
		for (int i = 0; i < numPoint; i++) {
			double y = 1 - (i / ((float) numPoint - 1)) * 2;
			double radius = Math.sqrt(1 - y * y);
			double theta = i % 2 == 0 ? phiE * i : Math.random() > 0.5 ? phiO * i : phiX * i;
			double x = Math.cos(theta) * radius;
			double z = Math.sin(theta) * radius;
			double rotThing = rotMod * radius;
			points[i] = new Vec3(x * Math.sqrt(Math.pow(Math.tan(rotThing), 0.75)) * radMod, y * radMod,
					z * Math.sqrt(Math.pow(Math.tan(rotThing), 0.75)) * radMod);

		}
		return points;
	}

}