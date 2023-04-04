package com.vincenthuto.hutoslib.math;

import java.nio.FloatBuffer;

import org.joml.Vector3f;

public final class Matrix4 {
	private static final int ORDER = 4;
	protected float m00;
	protected float m01;
	protected float m02;
	protected float m03;
	protected float m10;
	protected float m11;
	protected float m12;
	protected float m13;
	protected float m20;
	protected float m21;
	protected float m22;
	protected float m23;
	protected float m30;
	protected float m31;
	protected float m32;
	protected float m33;

	public Matrix4() {
	}

	public Matrix4(Matrix4 pOther) {
		this.m00 = pOther.m00;
		this.m01 = pOther.m01;
		this.m02 = pOther.m02;
		this.m03 = pOther.m03;
		this.m10 = pOther.m10;
		this.m11 = pOther.m11;
		this.m12 = pOther.m12;
		this.m13 = pOther.m13;
		this.m20 = pOther.m20;
		this.m21 = pOther.m21;
		this.m22 = pOther.m22;
		this.m23 = pOther.m23;
		this.m30 = pOther.m30;
		this.m31 = pOther.m31;
		this.m32 = pOther.m32;
		this.m33 = pOther.m33;
	}

	public Matrix4(Quaternion pQuaternion) {
		float f = pQuaternion.i();
		float f1 = pQuaternion.j();
		float f2 = pQuaternion.k();
		float f3 = pQuaternion.r();
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		this.m00 = 1.0F - f5 - f6;
		this.m11 = 1.0F - f6 - f4;
		this.m22 = 1.0F - f4 - f5;
		this.m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0F * (f7 + f12);
		this.m01 = 2.0F * (f7 - f12);
		this.m20 = 2.0F * (f9 - f11);
		this.m02 = 2.0F * (f9 + f11);
		this.m21 = 2.0F * (f8 + f10);
		this.m12 = 2.0F * (f8 - f10);
	}

	public boolean isInteger() {
		Matrix4 Matrix4 = new Matrix4();
		Matrix4.m30 = 1.0F;
		Matrix4.m31 = 1.0F;
		Matrix4.m32 = 1.0F;
		Matrix4.m33 = 0.0F;
		Matrix4 Matrix41 = this.copy();
		Matrix41.multiply(Matrix4);
		return isInteger(Matrix41.m00 / Matrix41.m03) && isInteger(Matrix41.m10 / Matrix41.m13)
				&& isInteger(Matrix41.m20 / Matrix41.m23) && isInteger(Matrix41.m01 / Matrix41.m03)
				&& isInteger(Matrix41.m11 / Matrix41.m13) && isInteger(Matrix41.m21 / Matrix41.m23)
				&& isInteger(Matrix41.m02 / Matrix41.m03) && isInteger(Matrix41.m12 / Matrix41.m13)
				&& isInteger(Matrix41.m22 / Matrix41.m23);
	}

	private static boolean isInteger(float pValue) {
		return (double) Math.abs(pValue - (float) Math.round(pValue)) <= 1.0E-5D;
	}

	public boolean equals(Object pOther) {
		if (this == pOther) {
			return true;
		} else if (pOther != null && this.getClass() == pOther.getClass()) {
			Matrix4 Matrix4 = (Matrix4) pOther;
			return Float.compare(Matrix4.m00, this.m00) == 0 && Float.compare(Matrix4.m01, this.m01) == 0
					&& Float.compare(Matrix4.m02, this.m02) == 0 && Float.compare(Matrix4.m03, this.m03) == 0
					&& Float.compare(Matrix4.m10, this.m10) == 0 && Float.compare(Matrix4.m11, this.m11) == 0
					&& Float.compare(Matrix4.m12, this.m12) == 0 && Float.compare(Matrix4.m13, this.m13) == 0
					&& Float.compare(Matrix4.m20, this.m20) == 0 && Float.compare(Matrix4.m21, this.m21) == 0
					&& Float.compare(Matrix4.m22, this.m22) == 0 && Float.compare(Matrix4.m23, this.m23) == 0
					&& Float.compare(Matrix4.m30, this.m30) == 0 && Float.compare(Matrix4.m31, this.m31) == 0
					&& Float.compare(Matrix4.m32, this.m32) == 0 && Float.compare(Matrix4.m33, this.m33) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
		i = 31 * i + (this.m03 != 0.0F ? Float.floatToIntBits(this.m03) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
		i = 31 * i + (this.m13 != 0.0F ? Float.floatToIntBits(this.m13) : 0);
		i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
		i = 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
		i = 31 * i + (this.m23 != 0.0F ? Float.floatToIntBits(this.m23) : 0);
		i = 31 * i + (this.m30 != 0.0F ? Float.floatToIntBits(this.m30) : 0);
		i = 31 * i + (this.m31 != 0.0F ? Float.floatToIntBits(this.m31) : 0);
		i = 31 * i + (this.m32 != 0.0F ? Float.floatToIntBits(this.m32) : 0);
		return 31 * i + (this.m33 != 0.0F ? Float.floatToIntBits(this.m33) : 0);
	}

	private static int bufferIndex(int pX, int pY) {
		return pY * 4 + pX;
	}

	public void load(FloatBuffer pBuffer) {
		this.m00 = pBuffer.get(bufferIndex(0, 0));
		this.m01 = pBuffer.get(bufferIndex(0, 1));
		this.m02 = pBuffer.get(bufferIndex(0, 2));
		this.m03 = pBuffer.get(bufferIndex(0, 3));
		this.m10 = pBuffer.get(bufferIndex(1, 0));
		this.m11 = pBuffer.get(bufferIndex(1, 1));
		this.m12 = pBuffer.get(bufferIndex(1, 2));
		this.m13 = pBuffer.get(bufferIndex(1, 3));
		this.m20 = pBuffer.get(bufferIndex(2, 0));
		this.m21 = pBuffer.get(bufferIndex(2, 1));
		this.m22 = pBuffer.get(bufferIndex(2, 2));
		this.m23 = pBuffer.get(bufferIndex(2, 3));
		this.m30 = pBuffer.get(bufferIndex(3, 0));
		this.m31 = pBuffer.get(bufferIndex(3, 1));
		this.m32 = pBuffer.get(bufferIndex(3, 2));
		this.m33 = pBuffer.get(bufferIndex(3, 3));
	}

	public void loadTransposed(FloatBuffer pBuffer) {
		this.m00 = pBuffer.get(bufferIndex(0, 0));
		this.m01 = pBuffer.get(bufferIndex(1, 0));
		this.m02 = pBuffer.get(bufferIndex(2, 0));
		this.m03 = pBuffer.get(bufferIndex(3, 0));
		this.m10 = pBuffer.get(bufferIndex(0, 1));
		this.m11 = pBuffer.get(bufferIndex(1, 1));
		this.m12 = pBuffer.get(bufferIndex(2, 1));
		this.m13 = pBuffer.get(bufferIndex(3, 1));
		this.m20 = pBuffer.get(bufferIndex(0, 2));
		this.m21 = pBuffer.get(bufferIndex(1, 2));
		this.m22 = pBuffer.get(bufferIndex(2, 2));
		this.m23 = pBuffer.get(bufferIndex(3, 2));
		this.m30 = pBuffer.get(bufferIndex(0, 3));
		this.m31 = pBuffer.get(bufferIndex(1, 3));
		this.m32 = pBuffer.get(bufferIndex(2, 3));
		this.m33 = pBuffer.get(bufferIndex(3, 3));
	}

	public void load(FloatBuffer pBuffer, boolean pTranspose) {
		if (pTranspose) {
			this.loadTransposed(pBuffer);
		} else {
			this.load(pBuffer);
		}

	}

	public void load(Matrix4 pOther) {
		this.m00 = pOther.m00;
		this.m01 = pOther.m01;
		this.m02 = pOther.m02;
		this.m03 = pOther.m03;
		this.m10 = pOther.m10;
		this.m11 = pOther.m11;
		this.m12 = pOther.m12;
		this.m13 = pOther.m13;
		this.m20 = pOther.m20;
		this.m21 = pOther.m21;
		this.m22 = pOther.m22;
		this.m23 = pOther.m23;
		this.m30 = pOther.m30;
		this.m31 = pOther.m31;
		this.m32 = pOther.m32;
		this.m33 = pOther.m33;
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("Matrix4:\n");
		stringbuilder.append(this.m00);
		stringbuilder.append(" ");
		stringbuilder.append(this.m01);
		stringbuilder.append(" ");
		stringbuilder.append(this.m02);
		stringbuilder.append(" ");
		stringbuilder.append(this.m03);
		stringbuilder.append("\n");
		stringbuilder.append(this.m10);
		stringbuilder.append(" ");
		stringbuilder.append(this.m11);
		stringbuilder.append(" ");
		stringbuilder.append(this.m12);
		stringbuilder.append(" ");
		stringbuilder.append(this.m13);
		stringbuilder.append("\n");
		stringbuilder.append(this.m20);
		stringbuilder.append(" ");
		stringbuilder.append(this.m21);
		stringbuilder.append(" ");
		stringbuilder.append(this.m22);
		stringbuilder.append(" ");
		stringbuilder.append(this.m23);
		stringbuilder.append("\n");
		stringbuilder.append(this.m30);
		stringbuilder.append(" ");
		stringbuilder.append(this.m31);
		stringbuilder.append(" ");
		stringbuilder.append(this.m32);
		stringbuilder.append(" ");
		stringbuilder.append(this.m33);
		stringbuilder.append("\n");
		return stringbuilder.toString();
	}

	public void store(FloatBuffer pBuffer) {
		pBuffer.put(bufferIndex(0, 0), this.m00);
		pBuffer.put(bufferIndex(0, 1), this.m01);
		pBuffer.put(bufferIndex(0, 2), this.m02);
		pBuffer.put(bufferIndex(0, 3), this.m03);
		pBuffer.put(bufferIndex(1, 0), this.m10);
		pBuffer.put(bufferIndex(1, 1), this.m11);
		pBuffer.put(bufferIndex(1, 2), this.m12);
		pBuffer.put(bufferIndex(1, 3), this.m13);
		pBuffer.put(bufferIndex(2, 0), this.m20);
		pBuffer.put(bufferIndex(2, 1), this.m21);
		pBuffer.put(bufferIndex(2, 2), this.m22);
		pBuffer.put(bufferIndex(2, 3), this.m23);
		pBuffer.put(bufferIndex(3, 0), this.m30);
		pBuffer.put(bufferIndex(3, 1), this.m31);
		pBuffer.put(bufferIndex(3, 2), this.m32);
		pBuffer.put(bufferIndex(3, 3), this.m33);
	}

	public void storeTransposed(FloatBuffer pBuffer) {
		pBuffer.put(bufferIndex(0, 0), this.m00);
		pBuffer.put(bufferIndex(1, 0), this.m01);
		pBuffer.put(bufferIndex(2, 0), this.m02);
		pBuffer.put(bufferIndex(3, 0), this.m03);
		pBuffer.put(bufferIndex(0, 1), this.m10);
		pBuffer.put(bufferIndex(1, 1), this.m11);
		pBuffer.put(bufferIndex(2, 1), this.m12);
		pBuffer.put(bufferIndex(3, 1), this.m13);
		pBuffer.put(bufferIndex(0, 2), this.m20);
		pBuffer.put(bufferIndex(1, 2), this.m21);
		pBuffer.put(bufferIndex(2, 2), this.m22);
		pBuffer.put(bufferIndex(3, 2), this.m23);
		pBuffer.put(bufferIndex(0, 3), this.m30);
		pBuffer.put(bufferIndex(1, 3), this.m31);
		pBuffer.put(bufferIndex(2, 3), this.m32);
		pBuffer.put(bufferIndex(3, 3), this.m33);
	}

	public void store(FloatBuffer pBuffer, boolean pTranspose) {
		if (pTranspose) {
			this.storeTransposed(pBuffer);
		} else {
			this.store(pBuffer);
		}

	}

	public void setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public float adjugateAndDet() {
		float f = this.m00 * this.m11 - this.m01 * this.m10;
		float f1 = this.m00 * this.m12 - this.m02 * this.m10;
		float f2 = this.m00 * this.m13 - this.m03 * this.m10;
		float f3 = this.m01 * this.m12 - this.m02 * this.m11;
		float f4 = this.m01 * this.m13 - this.m03 * this.m11;
		float f5 = this.m02 * this.m13 - this.m03 * this.m12;
		float f6 = this.m20 * this.m31 - this.m21 * this.m30;
		float f7 = this.m20 * this.m32 - this.m22 * this.m30;
		float f8 = this.m20 * this.m33 - this.m23 * this.m30;
		float f9 = this.m21 * this.m32 - this.m22 * this.m31;
		float f10 = this.m21 * this.m33 - this.m23 * this.m31;
		float f11 = this.m22 * this.m33 - this.m23 * this.m32;
		float f12 = this.m11 * f11 - this.m12 * f10 + this.m13 * f9;
		float f13 = -this.m10 * f11 + this.m12 * f8 - this.m13 * f7;
		float f14 = this.m10 * f10 - this.m11 * f8 + this.m13 * f6;
		float f15 = -this.m10 * f9 + this.m11 * f7 - this.m12 * f6;
		float f16 = -this.m01 * f11 + this.m02 * f10 - this.m03 * f9;
		float f17 = this.m00 * f11 - this.m02 * f8 + this.m03 * f7;
		float f18 = -this.m00 * f10 + this.m01 * f8 - this.m03 * f6;
		float f19 = this.m00 * f9 - this.m01 * f7 + this.m02 * f6;
		float f20 = this.m31 * f5 - this.m32 * f4 + this.m33 * f3;
		float f21 = -this.m30 * f5 + this.m32 * f2 - this.m33 * f1;
		float f22 = this.m30 * f4 - this.m31 * f2 + this.m33 * f;
		float f23 = -this.m30 * f3 + this.m31 * f1 - this.m32 * f;
		float f24 = -this.m21 * f5 + this.m22 * f4 - this.m23 * f3;
		float f25 = this.m20 * f5 - this.m22 * f2 + this.m23 * f1;
		float f26 = -this.m20 * f4 + this.m21 * f2 - this.m23 * f;
		float f27 = this.m20 * f3 - this.m21 * f1 + this.m22 * f;
		this.m00 = f12;
		this.m10 = f13;
		this.m20 = f14;
		this.m30 = f15;
		this.m01 = f16;
		this.m11 = f17;
		this.m21 = f18;
		this.m31 = f19;
		this.m02 = f20;
		this.m12 = f21;
		this.m22 = f22;
		this.m32 = f23;
		this.m03 = f24;
		this.m13 = f25;
		this.m23 = f26;
		this.m33 = f27;
		return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
	}

	public float determinant() {
		float f = this.m00 * this.m11 - this.m01 * this.m10;
		float f1 = this.m00 * this.m12 - this.m02 * this.m10;
		float f2 = this.m00 * this.m13 - this.m03 * this.m10;
		float f3 = this.m01 * this.m12 - this.m02 * this.m11;
		float f4 = this.m01 * this.m13 - this.m03 * this.m11;
		float f5 = this.m02 * this.m13 - this.m03 * this.m12;
		float f6 = this.m20 * this.m31 - this.m21 * this.m30;
		float f7 = this.m20 * this.m32 - this.m22 * this.m30;
		float f8 = this.m20 * this.m33 - this.m23 * this.m30;
		float f9 = this.m21 * this.m32 - this.m22 * this.m31;
		float f10 = this.m21 * this.m33 - this.m23 * this.m31;
		float f11 = this.m22 * this.m33 - this.m23 * this.m32;
		return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
	}

	public void transpose() {
		float f = this.m10;
		this.m10 = this.m01;
		this.m01 = f;
		f = this.m20;
		this.m20 = this.m02;
		this.m02 = f;
		f = this.m21;
		this.m21 = this.m12;
		this.m12 = f;
		f = this.m30;
		this.m30 = this.m03;
		this.m03 = f;
		f = this.m31;
		this.m31 = this.m13;
		this.m13 = f;
		f = this.m32;
		this.m32 = this.m23;
		this.m23 = f;
	}

	public boolean invert() {
		float f = this.adjugateAndDet();
		if (Math.abs(f) > 1.0E-6F) {
			this.multiply(f);
			return true;
		} else {
			return false;
		}
	}

	public void multiply(Matrix4 pOther) {
		float f = this.m00 * pOther.m00 + this.m01 * pOther.m10 + this.m02 * pOther.m20 + this.m03 * pOther.m30;
		float f1 = this.m00 * pOther.m01 + this.m01 * pOther.m11 + this.m02 * pOther.m21 + this.m03 * pOther.m31;
		float f2 = this.m00 * pOther.m02 + this.m01 * pOther.m12 + this.m02 * pOther.m22 + this.m03 * pOther.m32;
		float f3 = this.m00 * pOther.m03 + this.m01 * pOther.m13 + this.m02 * pOther.m23 + this.m03 * pOther.m33;
		float f4 = this.m10 * pOther.m00 + this.m11 * pOther.m10 + this.m12 * pOther.m20 + this.m13 * pOther.m30;
		float f5 = this.m10 * pOther.m01 + this.m11 * pOther.m11 + this.m12 * pOther.m21 + this.m13 * pOther.m31;
		float f6 = this.m10 * pOther.m02 + this.m11 * pOther.m12 + this.m12 * pOther.m22 + this.m13 * pOther.m32;
		float f7 = this.m10 * pOther.m03 + this.m11 * pOther.m13 + this.m12 * pOther.m23 + this.m13 * pOther.m33;
		float f8 = this.m20 * pOther.m00 + this.m21 * pOther.m10 + this.m22 * pOther.m20 + this.m23 * pOther.m30;
		float f9 = this.m20 * pOther.m01 + this.m21 * pOther.m11 + this.m22 * pOther.m21 + this.m23 * pOther.m31;
		float f10 = this.m20 * pOther.m02 + this.m21 * pOther.m12 + this.m22 * pOther.m22 + this.m23 * pOther.m32;
		float f11 = this.m20 * pOther.m03 + this.m21 * pOther.m13 + this.m22 * pOther.m23 + this.m23 * pOther.m33;
		float f12 = this.m30 * pOther.m00 + this.m31 * pOther.m10 + this.m32 * pOther.m20 + this.m33 * pOther.m30;
		float f13 = this.m30 * pOther.m01 + this.m31 * pOther.m11 + this.m32 * pOther.m21 + this.m33 * pOther.m31;
		float f14 = this.m30 * pOther.m02 + this.m31 * pOther.m12 + this.m32 * pOther.m22 + this.m33 * pOther.m32;
		float f15 = this.m30 * pOther.m03 + this.m31 * pOther.m13 + this.m32 * pOther.m23 + this.m33 * pOther.m33;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m03 = f3;
		this.m10 = f4;
		this.m11 = f5;
		this.m12 = f6;
		this.m13 = f7;
		this.m20 = f8;
		this.m21 = f9;
		this.m22 = f10;
		this.m23 = f11;
		this.m30 = f12;
		this.m31 = f13;
		this.m32 = f14;
		this.m33 = f15;
	}

	public void multiply(Quaternion pQuaternion) {
		this.multiply(new Matrix4(pQuaternion));
	}

	public void multiply(float pMultiplier) {
		this.m00 *= pMultiplier;
		this.m01 *= pMultiplier;
		this.m02 *= pMultiplier;
		this.m03 *= pMultiplier;
		this.m10 *= pMultiplier;
		this.m11 *= pMultiplier;
		this.m12 *= pMultiplier;
		this.m13 *= pMultiplier;
		this.m20 *= pMultiplier;
		this.m21 *= pMultiplier;
		this.m22 *= pMultiplier;
		this.m23 *= pMultiplier;
		this.m30 *= pMultiplier;
		this.m31 *= pMultiplier;
		this.m32 *= pMultiplier;
		this.m33 *= pMultiplier;
	}

	public void add(Matrix4 pOther) {
		this.m00 += pOther.m00;
		this.m01 += pOther.m01;
		this.m02 += pOther.m02;
		this.m03 += pOther.m03;
		this.m10 += pOther.m10;
		this.m11 += pOther.m11;
		this.m12 += pOther.m12;
		this.m13 += pOther.m13;
		this.m20 += pOther.m20;
		this.m21 += pOther.m21;
		this.m22 += pOther.m22;
		this.m23 += pOther.m23;
		this.m30 += pOther.m30;
		this.m31 += pOther.m31;
		this.m32 += pOther.m32;
		this.m33 += pOther.m33;
	}

	public void subtract(Matrix4 pOther) {
		this.m00 -= pOther.m00;
		this.m01 -= pOther.m01;
		this.m02 -= pOther.m02;
		this.m03 -= pOther.m03;
		this.m10 -= pOther.m10;
		this.m11 -= pOther.m11;
		this.m12 -= pOther.m12;
		this.m13 -= pOther.m13;
		this.m20 -= pOther.m20;
		this.m21 -= pOther.m21;
		this.m22 -= pOther.m22;
		this.m23 -= pOther.m23;
		this.m30 -= pOther.m30;
		this.m31 -= pOther.m31;
		this.m32 -= pOther.m32;
		this.m33 -= pOther.m33;
	}

	public float trace() {
		return this.m00 + this.m11 + this.m22 + this.m33;
	}

	public static Matrix4 perspective(double pFov, float pAspectRatio, float pNearPlane, float pFarPlane) {
		float f = (float) (1.0D / Math.tan(pFov * (double) ((float) Math.PI / 180F) / 2.0D));
		Matrix4 Matrix4 = new Matrix4();
		Matrix4.m00 = f / pAspectRatio;
		Matrix4.m11 = f;
		Matrix4.m22 = (pFarPlane + pNearPlane) / (pNearPlane - pFarPlane);
		Matrix4.m32 = -1.0F;
		Matrix4.m23 = 2.0F * pFarPlane * pNearPlane / (pNearPlane - pFarPlane);
		return Matrix4;
	}

	public static Matrix4 orthographic(float pWidth, float pHeight, float pNearPlane, float pFarPlane) {
		Matrix4 Matrix4 = new Matrix4();
		Matrix4.m00 = 2.0F / pWidth;
		Matrix4.m11 = 2.0F / pHeight;
		float f = pFarPlane - pNearPlane;
		Matrix4.m22 = -2.0F / f;
		Matrix4.m33 = 1.0F;
		Matrix4.m03 = -1.0F;
		Matrix4.m13 = 1.0F;
		Matrix4.m23 = -(pFarPlane + pNearPlane) / f;
		return Matrix4;
	}

	public static Matrix4 orthographic(float pMinX, float pMaxX, float pMinY, float pMaxY, float pMinZ, float pMaxZ) {
		Matrix4 Matrix4 = new Matrix4();
		float f = pMaxX - pMinX;
		float f1 = pMinY - pMaxY;
		float f2 = pMaxZ - pMinZ;
		Matrix4.m00 = 2.0F / f;
		Matrix4.m11 = 2.0F / f1;
		Matrix4.m22 = -2.0F / f2;
		Matrix4.m03 = -(pMaxX + pMinX) / f;
		Matrix4.m13 = -(pMinY + pMaxY) / f1;
		Matrix4.m23 = -(pMaxZ + pMinZ) / f2;
		Matrix4.m33 = 1.0F;
		return Matrix4;
	}

	public void translate(Vector3f pVector) {
		this.m03 += pVector.x();
		this.m13 += pVector.y();
		this.m23 += pVector.z();
	}

	public Matrix4 copy() {
		return new Matrix4(this);
	}

	public void multiplyWithTranslation(float pScalarX, float pScalarY, float pScalarZ) {
		this.m03 += this.m00 * pScalarX + this.m01 * pScalarY + this.m02 * pScalarZ;
		this.m13 += this.m10 * pScalarX + this.m11 * pScalarY + this.m12 * pScalarZ;
		this.m23 += this.m20 * pScalarX + this.m21 * pScalarY + this.m22 * pScalarZ;
		this.m33 += this.m30 * pScalarX + this.m31 * pScalarY + this.m32 * pScalarZ;
	}

	public static Matrix4 createScaleMatrix(float pScalarX, float pScalarY, float pScalarZ) {
		Matrix4 Matrix4 = new Matrix4();
		Matrix4.m00 = pScalarX;
		Matrix4.m11 = pScalarY;
		Matrix4.m22 = pScalarZ;
		Matrix4.m33 = 1.0F;
		return Matrix4;
	}

	public static Matrix4 createTranslateMatrix(float pX, float pY, float pZ) {
		Matrix4 Matrix4 = new Matrix4();
		Matrix4.m00 = 1.0F;
		Matrix4.m11 = 1.0F;
		Matrix4.m22 = 1.0F;
		Matrix4.m33 = 1.0F;
		Matrix4.m03 = pX;
		Matrix4.m13 = pY;
		Matrix4.m23 = pZ;
		return Matrix4;
	}

	// Forge start
	public Matrix4(float[] values) {
		m00 = values[0];
		m01 = values[1];
		m02 = values[2];
		m03 = values[3];
		m10 = values[4];
		m11 = values[5];
		m12 = values[6];
		m13 = values[7];
		m20 = values[8];
		m21 = values[9];
		m22 = values[10];
		m23 = values[11];
		m30 = values[12];
		m31 = values[13];
		m32 = values[14];
		m33 = values[15];
	}

	public void multiplyBackward(Matrix4 other) {
		Matrix4 copy = other.copy();
		copy.multiply(this);
		this.load(copy);
	}

	public void setTranslation(float x, float y, float z) {
		this.m00 = 1.0F;
		this.m11 = 1.0F;
		this.m22 = 1.0F;
		this.m33 = 1.0F;
		this.m03 = x;
		this.m13 = y;
		this.m23 = z;
	}
}
