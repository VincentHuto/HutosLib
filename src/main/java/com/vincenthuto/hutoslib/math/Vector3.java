package com.vincenthuto.hutoslib.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Vector3 {

	public float x;

	public float y;

	public float z;

	public static final Vector3 ZERO = new Vector3(0, 0, 0);
	public static final Vector3 ONE = new Vector3(1, 1, 1);
	public static Vector3 XN = new Vector3(-1.0F, 0.0F, 0.0F);
	public static Vector3 XP = new Vector3(1.0F, 0.0F, 0.0F);
	public static Vector3 YN = new Vector3(0.0F, -1.0F, 0.0F);
	public static Vector3 YP = new Vector3(0.0F, 1.0F, 0.0F);
	public static Vector3 ZN = new Vector3(0.0F, 0.0F, -1.0F);
	public static Vector3 ZP = new Vector3(0.0F, 0.0F, 1.0F);

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public Quaternion rotation(float pValue) {
		return new Quaternion(this, pValue, false);
	}

	public Quaternion rotationDegrees(float pValue) {
		return new Quaternion(this, pValue, true);
	}

	public static Vector3 fromBlockEntity(BlockEntity e) {
		return fromBlockPos(e.getBlockPos());
	}

	public static Vector3 fromBlockEntityCenter(BlockEntity e) {
		return fromBlockEntity(e).add(0.5);
	}

	public static Vector3 fromBlockPos(BlockPos pos) {
		return new Vector3(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vector3 fromEntityCenter(Entity e) {
		return new Vector3(e.getX(), e.getY() - e.getMyRidingOffset() + e.getBbHeight() / 2, e.getZ());
	}

	public static Vector3 getPerpendicular(Vector3 vec) {
		if (vec.z == 0.0f) {
			return Vector3.zCrossProduct(vec);
		}
		return Vector3.xCrossProduct(vec);
	}

	public static Vector3 xCrossProduct(Vector3 vec) {
		return new Vector3(0.0, vec.z, -vec.y);
	}

	public static Vector3 zCrossProduct(Vector3 vec) {
		return new Vector3(-vec.y, vec.x, 0.0);
	}

	public Vector3(Vec3 vec) {
		this(vec.x, vec.y, vec.z);
	}

	public Vector3 add(double d) {
		return add(d, d, d);
	}

	public Vector3 add(double d, double d1, double d2) {
		return new Vector3(x + d, y + d1, z + d2);
	}

	public Vector3 add(Vector3 vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vec3 addToVec3(double d, double d1, double d2) {
		return new Vec3(x + d, y + d1, z + d2);
	}

	public double angle(Vector3 vec) {
		double projection = normalize().dotProduct(vec.normalize());
		return Math.acos(Mth.clamp(projection, -1, 1));
	}

	public Vector3 crossProduct(Vector3 vec) {
		double d = y * vec.z - z * vec.y;
		double d1 = z * vec.x - x * vec.z;
		double d2 = x * vec.y - y * vec.x;
		return new Vector3(d, d1, d2);
	}

	/**
	 * Euclidean distance between this and the specified vector, returned as double.
	 */
	public double distanceTo(Vector3 vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
	}

	public double dotProduct(double d, double d1, double d2) {
		return d * x + d1 * y + d2 * z;
	}

	public double dotProduct(Vector3 vec) {
		double d = vec.x * x + vec.y * y + vec.z * z;

		if (d > 1 && d < 1.00001) {
			d = 1;
		} else if (d < -1 && d > -1.00001) {
			d = -1;
		}
		return d;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector3)) {
			return false;
		}

		Vector3 v = (Vector3) o;
		return x == v.x && y == v.y && z == v.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double magSquared() {
		return x * x + y * y + z * z;
	}

	public Vector3 multiply(double d) {
		return multiply(d, d, d);
	}

	public Vector3 multiply(double fx, double fy, double fz) {
		return new Vector3(x * fx, y * fy, z * fz);
	}

	public Vector3 multiply(Vector3 f) {
		return multiply(f.x, f.y, f.z);
	}

	public Vector3 negate() {
		return new Vector3(-x, -y, -z);
	}

	public Vector3 copy() {
		return new Vector3(this.x, this.y, this.z);
	}

	public Vector3 normalize() {
		double d = mag();
		if (d != 0) {
			return multiply(1 / d);
		}

		return this;
	}

	public Vector3 perpendicular() {
		if (z == 0) {
			return zCrossProduct();
		}
		return xCrossProduct();
	}

	public Vector3 project(Vector3 b) {
		double l = b.magSquared();
		if (l == 0) {
			return ZERO;
		}

		double m = dotProduct(b) / l;
		return b.multiply(m);
	}

	public Vector3 rotate(double angle, Vector3 axis) {
		return Quat.aroundAxis(axis.normalize(), angle).rotate(this);
	}

	public double scalarProject(Vector3 b) {
		double l = b.mag();
		return l == 0 ? 0 : dotProduct(b) / l;
	}

	public Vector3 scale(float scale) {
		return this.scale(scale, scale, scale);
	}

	public Vector3 scale(float scalex, float scaley, float scalez) {
		return new Vector3(this.x * scalex, this.y * scaley, this.z * scalez);
	}

	public double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.x;
		double d1 = yIn - this.y;
		double d2 = zIn - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	/**
	 * The square of the Euclidean distance between this and the specified vector.
	 */
	public double squareDistanceTo(Vector3 vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public Vector3 subtract(Vector3 vec) {
		return new Vector3(x - vec.x, y - vec.y, z - vec.z);
	}

	@Override
	public String toString() {
		MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
		return "Vector3(" + new BigDecimal(x, cont) + ", " + new BigDecimal(y, cont) + ", " + new BigDecimal(z, cont)
				+ ")";
	}

	public Vec3 toVec3() {
		return new Vec3(this.x, this.y, this.z);
	}

	public Vector3d toVector3d() {
		return new Vector3d(x, y, z);
	}

	@OnlyIn(Dist.CLIENT)
	public void vertex(Matrix4f mat, VertexConsumer buffer) {
		buffer.vertex(mat, (float) x, (float) y, (float) z);
	}

	public Vector3 xCrossProduct() {
		double d = z;
		double d1 = -y;
		return new Vector3(0, d, d1);
	}

	public Vector3 yCrossProduct() {
		double d = -z;
		double d1 = x;
		return new Vector3(d, 0, d1);
	}

	public Vector3 zCrossProduct() {
		double d = y;
		double d1 = -x;
		return new Vector3(d, d1, 0);
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void transform(Matrix3f pMatrix) {
		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		this.x = pMatrix.m00 * f + pMatrix.m01 * f1 + pMatrix.m02 * f2;
		this.y = pMatrix.m10 * f + pMatrix.m11 * f1 + pMatrix.m12 * f2;
		this.z = pMatrix.m20 * f + pMatrix.m21 * f1 + pMatrix.m22 * f2;
	}

	public void transform(Quaternion pQuaternion) {
		Quaternion quaternion = new Quaternion(pQuaternion);
		quaternion.mul(new Quaternion(this.x, this.y, this.z, 0.0F));
		Quaternion quaternion1 = new Quaternion(pQuaternion);
		quaternion1.conj();
		quaternion.mul(quaternion1);
		this.set(quaternion.i(), quaternion.j(), quaternion.k());
	}

	public void set(float pX, float pY, float pZ) {
		this.x = pX;
		this.y = pY;
		this.z = pZ;
	}

}
