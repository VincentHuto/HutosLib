package com.vincenthuto.hutoslib.common.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores guide-book animation state by player and item identity.
 */
final class BookAnimStateCache {
	private final Map<AnimationStateKey, BookAnimState> states = new ConcurrentHashMap<>();

	BookAnimState getOrCreate(UUID playerUuid, Object bookIdentity) {
		return states.computeIfAbsent(
				new AnimationStateKey(playerUuid, bookIdentity),
				key -> new BookAnimState());
	}

	void clear(UUID playerUuid) {
		states.keySet().removeIf(key -> key.playerUuid.equals(playerUuid));
	}

	private static final class AnimationStateKey {
		private final UUID playerUuid;
		private final Object bookIdentity;

		private AnimationStateKey(UUID playerUuid, Object bookIdentity) {
			this.playerUuid = playerUuid;
			this.bookIdentity = bookIdentity;
		}

		@Override
		public boolean equals(Object other) {
			return this == other
					|| (other instanceof AnimationStateKey key
					&& playerUuid.equals(key.playerUuid)
					&& bookIdentity == key.bookIdentity);
		}

		@Override
		public int hashCode() {
			return 31 * playerUuid.hashCode() + System.identityHashCode(bookIdentity);
		}
	}
}
