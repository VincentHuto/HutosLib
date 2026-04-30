package com.vincenthuto.hutoslib.common.item;

/**
 * Per-entity client-side animation state for an {@link ItemGuideBook}.
 *
 * <p>One instance is kept in {@link ItemGuideBook#ANIM_STATES} per entity UUID
 * so that every player (or NPC) holding the book has independent animation
 * state rather than sharing the item singleton's fields.
 */
public final class BookAnimState {
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
