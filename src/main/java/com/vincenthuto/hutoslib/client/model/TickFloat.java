package com.vincenthuto.hutoslib.client.model;

import com.vincenthuto.hutoslib.math.MathUtils;

import net.minecraft.util.Mth;

public class TickFloat {
	private float min;
	private float max;
	private float current;
	private float previous;
	private boolean clamp = false;

	public TickFloat() {
		current = previous = 0;
	}

	public TickFloat(float value) {
		current = previous = value;
	}

	public TickFloat setLimit(float min, float max) {
		clamp = true;
		this.min = min;
		this.max = max;
		set(current);
		return this;
	}

	public float get(float x) {
		return MathUtils.linTerp(previous, current, x);
	}

	public float get() {
		return current;
	}

	public void set(float value) {
		sync();
		current = clamp ? Mth.clamp(value, min, max) : value;
	}

	public void add(float value) {
		sync();
		current += value;
		if (clamp)
			current = Mth.clamp(current, min, max);
	}

	public void sync() {
		previous = current;
	}

	public float getPrevious() {
		return previous;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}
}