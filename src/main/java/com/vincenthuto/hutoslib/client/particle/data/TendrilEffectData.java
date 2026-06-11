package com.vincenthuto.hutoslib.client.particle.data;

import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

public record TendrilEffectData(TendrilAnchor start, TendrilAnchor end, TendrilEffectConfig config, long seed) {
	public TendrilEffectData {
		config = config.clamped();
	}
}
