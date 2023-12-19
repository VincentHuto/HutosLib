package com.vincenthuto.hutoslib.common.karma;

public class Karma implements IKarma {
	private boolean active = false;
	private float karma = 0.0F;

	@Override
	public void addKarma(float points) {
		this.karma += points;
	}

	@Override
	public float getKarma() {
		return this.karma;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean set) {
		this.active = set;
	}

	@Override
	public void setKarma(float points) {
		this.karma = points;
	}

	@Override
	public void subtractKarma(float points) {
		this.karma -= points;
	}

	@Override
	public void toggleActive() {
		this.active = !active;
	}

}
