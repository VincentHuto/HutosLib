package com.vincenthuto.hutoslib.client;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class HLRenderTypeInit {
	public static final RenderType LIGHTNING = RenderTypes.lightning();
	public static final ParticleRenderType GLOW_RENDER = ParticleRenderType.SINGLE_QUADS;
	public static final ParticleRenderType DARK_GLOW_RENDER = ParticleRenderType.SINGLE_QUADS;

	private HLRenderTypeInit() {
	}
}
