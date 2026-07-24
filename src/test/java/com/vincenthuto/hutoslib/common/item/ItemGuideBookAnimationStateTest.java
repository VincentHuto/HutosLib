package com.vincenthuto.hutoslib.common.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

class ItemGuideBookAnimationStateTest {
	private final UUID firstPlayer = UUID.randomUUID();
	private final UUID secondPlayer = UUID.randomUUID();

	@AfterEach
	void clearAnimationStates() {
		ItemGuideBook.clearState(firstPlayer);
		ItemGuideBook.clearState(secondPlayer);
	}

	@Test
	void differentGuideBookItemsHaveIndependentStateForOnePlayer() {
		ItemGuideBook firstBook = book("first");
		ItemGuideBook secondBook = book("second");

		BookAnimState firstState = ItemGuideBook.getOrCreateState(firstPlayer, firstBook);
		BookAnimState secondState = ItemGuideBook.getOrCreateState(firstPlayer, secondBook);

		assertNotSame(firstState, secondState);

		ItemGuideBook.tickAnimation(firstState, 10L, true);
		ItemGuideBook.tickAnimation(secondState, 10L, false);

		assertEquals(0.015F, firstState.close, 0.0001F);
		assertEquals(0.0F, secondState.close, 0.0001F);
	}

	@Test
	void duplicateStacksOfOneBookTypeUpdateOnlyOncePerGameTick() {
		ItemGuideBook guideBook = book("duplicate");
		BookAnimState state = ItemGuideBook.getOrCreateState(firstPlayer, guideBook);

		ItemGuideBook.tickAnimation(state, 20L, true);
		ItemGuideBook.tickAnimation(state, 20L, false);

		assertEquals(0.015F, state.close, 0.0001F);
	}

	@Test
	void stateCanUpdateAgainOnTheNextGameTick() {
		ItemGuideBook guideBook = book("next_tick");
		BookAnimState state = ItemGuideBook.getOrCreateState(firstPlayer, guideBook);

		ItemGuideBook.tickAnimation(state, 30L, true);
		ItemGuideBook.tickAnimation(state, 31L, true);

		assertEquals(0.030F, state.close, 0.0001F);
	}

	@Test
	void clearingPlayerRemovesAllOfThatPlayersBookStatesOnly() {
		ItemGuideBook firstBook = book("clear_first");
		ItemGuideBook secondBook = book("clear_second");
		BookAnimState firstState = ItemGuideBook.getOrCreateState(firstPlayer, firstBook);
		BookAnimState secondState = ItemGuideBook.getOrCreateState(firstPlayer, secondBook);
		BookAnimState otherPlayerState = ItemGuideBook.getOrCreateState(secondPlayer, firstBook);

		ItemGuideBook.clearState(firstPlayer);

		assertNotSame(firstState, ItemGuideBook.getOrCreateState(firstPlayer, firstBook));
		assertNotSame(secondState, ItemGuideBook.getOrCreateState(firstPlayer, secondBook));
		assertSame(otherPlayerState, ItemGuideBook.getOrCreateState(secondPlayer, firstBook));
	}

	private static ItemGuideBook book(String path) {
		return new ItemGuideBook(
				new Item.Properties(),
				ResourceLocation.fromNamespaceAndPath("hutoslib", path));
	}
}
