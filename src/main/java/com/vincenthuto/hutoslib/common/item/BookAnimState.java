package com.vincenthuto.hutoslib.common.item;

/**
 * Per-player, per-item-type client-side animation state for an
 * {@link ItemGuideBook}.
 *
 * <p>Each guide-book item singleton gets an independent state for every player,
 * preventing other guide-book types in the same inventory from modifying its
 * animation.
 */
public final class BookAnimState {
	public long  lastAnimationTick = Long.MIN_VALUE;
	public int   ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float nextPageAngle;
	public float pageAngle;
	public float tRot;
	public float close;
}
