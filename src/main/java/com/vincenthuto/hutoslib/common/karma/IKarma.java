package com.vincenthuto.hutoslib.common.karma;

public interface IKarma {
	public boolean isActive();

	public void subtractKarma(float points);

	public void addKarma(float points);

	public void setKarma(float points);

	public void setActive(boolean set);

	public void toggleActive();

	public float getKarma();
}
