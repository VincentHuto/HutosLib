package com.vincenthuto.hutoslib.common.karma;

public interface IKarma {
	public void addKarma(float points);

	public float getKarma();

	public boolean isActive();

	public void setActive(boolean set);

	public void setKarma(float points);

	public void subtractKarma(float points);

	public void toggleActive();
}
