package com.vincenthuto.hutoslib.common.item;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemGuideBook extends Item {
	private static final Random random = new Random();

	/**
	 * Client-side animation state keyed by entity UUID.
	 *
	 * <p>Items are singletons shared across all players, so animation state must
	 * <em>not</em> be stored as instance fields. One {@link BookAnimState} entry is
	 * created on demand for each entity that holds this book, keeping their
	 * animation independent.
	 *
	 * <p>Only populated on the logical client (guarded by
	 * {@link Level#isClientSide} in {@link #inventoryTick}).
	 *
	 * <p>Entries are removed via {@link #clearState(UUID)} when a player
	 * disconnects to prevent unbounded growth.
	 */
	private static final Map<UUID, BookAnimState> ANIM_STATES = new ConcurrentHashMap<>();

	/**
	 * Fallback state used when no local player is available (e.g. GUI preview).
	 * Never mutated — represents a default closed-book pose.
	 */
	private static final BookAnimState DEFAULT_STATE = new BookAnimState();

	private ResourceLocation texture;

	public ItemGuideBook(Properties prop, ResourceLocation loc) {
		super(prop);
		this.texture = loc;
	}

	/**
	 * Returns the animation state for the given entity UUID, creating a default
	 * state if one does not yet exist. Returns {@link #DEFAULT_STATE} when
	 * {@code entityUuid} is {@code null}.
	 */
	public static BookAnimState getOrCreateState(UUID entityUuid) {
		if (entityUuid == null) {
			return DEFAULT_STATE;
		}
		return ANIM_STATES.computeIfAbsent(entityUuid, id -> new BookAnimState());
	}

	/**
	 * Removes the cached animation state for the given entity UUID.
	 * Call this when a player disconnects to prevent unbounded map growth.
	 */
	public static void clearState(UUID entityUuid) {
		ANIM_STATES.remove(entityUuid);
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		// Animation is purely cosmetic — only update on the client.
		if (!worldIn.isClientSide) {
			return;
		}

		if (entityIn instanceof Player player) {
			BookAnimState s = getOrCreateState(player.getUUID());

			ItemStack oheld = player.getOffhandItem();
			boolean offHand = stack.getItem() == oheld.getItem();

			ItemStack mheld = player.getMainHandItem();
			boolean mainHand = stack.getItem() == mheld.getItem();

			if (mainHand || offHand) {
				s.pageTurningSpeed = s.nextPageTurningSpeed;
				s.pageAngle = s.nextPageAngle;
				s.nextPageTurningSpeed += 0.1F;
				if (s.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
					float f1 = s.flipT;
					do {
						s.flipT += random.nextInt(4) - random.nextInt(4);
					} while (f1 == s.flipT);
				}
				while (s.nextPageAngle >= (float) Math.PI) {
					s.nextPageAngle -= ((float) Math.PI * 2F);
				}
				while (s.nextPageAngle < -(float) Math.PI) {
					s.nextPageAngle += ((float) Math.PI * 2F);
				}
				while (s.tRot >= (float) Math.PI) {
					s.tRot -= ((float) Math.PI * 2F);
				}
				while (s.tRot < -(float) Math.PI) {
					s.tRot += ((float) Math.PI * 2F);
				}
				float f2;
				for (f2 = s.tRot - s.nextPageAngle; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
				}
				while (f2 < -(float) Math.PI) {
					f2 += ((float) Math.PI * 2F);
				}
				s.nextPageAngle += f2 * 0.4F;
				s.nextPageTurningSpeed = Mth.clamp(s.nextPageTurningSpeed, 0.0F, 1.0F);
				s.oFlip = s.flip;
				float f = (s.flipT - s.flip) * 0.4F;
				f = Mth.clamp(f, -0.2F, 0.2F);
				s.flipA += (f - s.flipA) * 0.9F;
				s.flip += s.flipA;
				if (s.close < 1f) {
					s.close += 0.015f;
				}
			} else {
				if (s.close > 0f) {
					s.close -= 0.015f;
				}
			}
		}
	}

	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}
}
