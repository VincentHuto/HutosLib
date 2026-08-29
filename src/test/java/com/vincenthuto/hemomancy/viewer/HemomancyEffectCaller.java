package com.vincenthuto.hemomancy.viewer;

import com.vincenthuto.hutoslib.common.effectsource.EffectSourceInference;

public final class HemomancyEffectCaller {

	public static EffectSourceInference.CapturedCaller capture() {
		return EffectSourceInference.capture();
	}

	private HemomancyEffectCaller() {
	}
}
