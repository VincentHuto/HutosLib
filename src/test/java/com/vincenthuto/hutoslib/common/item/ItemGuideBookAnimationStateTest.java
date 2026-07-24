package com.vincenthuto.hutoslib.common.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ItemGuideBookAnimationStateTest {
	private final UUID firstPlayer = UUID.randomUUID();
	private final UUID secondPlayer = UUID.randomUUID();
	private final BookAnimStateCache states = new BookAnimStateCache();

	@Test
	void differentGuideBookItemsHaveIndependentStateForOnePlayer() {
		Object firstBook = new Object();
		Object secondBook = new Object();

		BookAnimState firstState = states.getOrCreate(firstPlayer, firstBook);
		BookAnimState secondState = states.getOrCreate(firstPlayer, secondBook);

		assertNotSame(firstState, secondState);

		ItemGuideBook.tickAnimation(firstState, 10L, true);
		ItemGuideBook.tickAnimation(secondState, 10L, false);

		assertEquals(0.015F, firstState.close, 0.0001F);
		assertEquals(0.0F, secondState.close, 0.0001F);
	}

	@Test
	void duplicateStacksOfOneBookTypeUpdateOnlyOncePerGameTick() {
		Object guideBook = new Object();
		BookAnimState state = states.getOrCreate(firstPlayer, guideBook);

		ItemGuideBook.tickAnimation(state, 20L, true);
		ItemGuideBook.tickAnimation(state, 20L, false);

		assertEquals(0.015F, state.close, 0.0001F);
	}

	@Test
	void stateCanUpdateAgainOnTheNextGameTick() {
		Object guideBook = new Object();
		BookAnimState state = states.getOrCreate(firstPlayer, guideBook);

		ItemGuideBook.tickAnimation(state, 30L, true);
		ItemGuideBook.tickAnimation(state, 31L, true);

		assertEquals(0.030F, state.close, 0.0001F);
	}

	@Test
	void clearingPlayerRemovesAllOfThatPlayersBookStatesOnly() {
		Object firstBook = new Object();
		Object secondBook = new Object();
		BookAnimState firstState = states.getOrCreate(firstPlayer, firstBook);
		BookAnimState secondState = states.getOrCreate(firstPlayer, secondBook);
		BookAnimState otherPlayerState = states.getOrCreate(secondPlayer, firstBook);

		states.clear(firstPlayer);

		assertNotSame(firstState, states.getOrCreate(firstPlayer, firstBook));
		assertNotSame(secondState, states.getOrCreate(firstPlayer, secondBook));
		assertSame(otherPlayerState, states.getOrCreate(secondPlayer, firstBook));
	}
}
