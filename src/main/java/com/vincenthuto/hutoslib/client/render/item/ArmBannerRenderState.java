package com.vincenthuto.hutoslib.client.render.item;

public record ArmBannerRenderState(boolean shouldRenderPlate, boolean hasPatternLayers) {
	public static ArmBannerRenderState fromBannerData(boolean hasBaseColor, int patternLayerCount) {
		boolean hasPatternLayers = patternLayerCount > 0;
		return new ArmBannerRenderState(hasBaseColor || hasPatternLayers, hasPatternLayers);
	}
}
