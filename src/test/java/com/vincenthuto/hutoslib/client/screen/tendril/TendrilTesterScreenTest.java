package com.vincenthuto.hutoslib.client.screen.tendril;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

class TendrilTesterScreenTest {

	@Test
	void shiftUsesFiveTimesStepperMultiplier() {
		assertEquals(1, TendrilTesterScreen.stepMultiplier(false));
		assertEquals(5, TendrilTesterScreen.stepMultiplier(true));
	}

	@Test
	void sectionsExposeFullDevLabControlsInStableOrder() {
		assertEquals(List.of(TendrilTesterScreen.Section.TARGET, TendrilTesterScreen.Section.LIFECYCLE,
				TendrilTesterScreen.Section.COLORS, TendrilTesterScreen.Section.SHAPE,
				TendrilTesterScreen.Section.BRANCHING, TendrilTesterScreen.Section.WRITHE,
				TendrilTesterScreen.Section.SURFACE, TendrilTesterScreen.Section.SEED), TendrilTesterScreen.sections());
	}

	@Test
	void compactLayoutUsesMoreColumnsWhenScreenIsWideEnough() {
		assertEquals(1, TendrilTesterScreen.compactColumnCount(320));
		assertEquals(2, TendrilTesterScreen.compactColumnCount(500));
		assertEquals(3, TendrilTesterScreen.compactColumnCount(800));
	}

	@Test
	void compactLayoutTightensRowsOnShortScreens() {
		assertEquals(14, TendrilTesterScreen.compactRowHeight(240));
		assertEquals(16, TendrilTesterScreen.compactRowHeight(400));
	}

	@Test
	void compactLayoutStartsHigherToLeaveRoomForActionButtons() {
		assertEquals(18, TendrilTesterScreen.compactTop(240));
		assertEquals(22, TendrilTesterScreen.compactTop(400));
		assertEquals(230, TendrilTesterScreen.actionButtonY(300, 220));
		assertEquals(278, TendrilTesterScreen.actionButtonY(300, 290));
	}

	@Test
	void collapsedSectionsKeepHeaderRowsButHideControls() {
		assertEquals(36, TendrilTesterScreen.visibleSectionRowCount(EnumSet.noneOf(TendrilTesterScreen.Section.class),
				false));
		assertEquals(28, TendrilTesterScreen.visibleSectionRowCount(EnumSet.of(TendrilTesterScreen.Section.SHAPE,
				TendrilTesterScreen.Section.WRITHE), false));
		assertEquals(32, TendrilTesterScreen.visibleSectionRowCount(EnumSet.of(TendrilTesterScreen.Section.TARGET),
				true));
	}

	@Test
	void categoriesDefaultToCollapsed() {
		assertEquals(EnumSet.allOf(TendrilTesterScreen.Section.class), TendrilTesterScreen.defaultCollapsedSections());
	}
}
