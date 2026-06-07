package com.vincenthuto.hutoslib.client.screen.lightning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LightningTesterScreenTest {

	@Test
	void shiftUsesFiveTimesStepperMultiplier() {
		assertEquals(1, LightningTesterScreen.stepMultiplier(false));
		assertEquals(5, LightningTesterScreen.stepMultiplier(true));
	}
}
