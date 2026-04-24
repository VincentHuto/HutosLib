package com.vincenthuto.hutoslib.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class HLRenderTypeInit extends RenderType {

	// --- Existing lightning render type (POSITION_COLOR, additive blend) ---

	static RenderType.CompositeState lightningState = RenderType.CompositeState.builder()
			.setShaderState(POSITION_COLOR_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY)
			.createCompositeState(false);

	public static final RenderType LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS, 256, false, true, lightningState);

	// --- Particle backing RenderTypes ---

	// Additive blend (SRC_ALPHA, ONE), no depth write — used by GLOW particles
	static RenderType.CompositeState particleGlowState = RenderType.CompositeState.builder()
			.setShaderState(new ShaderStateShard(GameRenderer::getParticleShader))
			.setTextureState(new TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.setWriteMaskState(COLOR_WRITE)
			.createCompositeState(false);

	public static final RenderType PARTICLE_GLOW = create("hutoslib:particle_glow", DefaultVertexFormat.PARTICLE,
			VertexFormat.Mode.QUADS, 256, false, false, particleGlowState);

	// Translucent blend (SRC_ALPHA, ONE_MINUS_SRC_ALPHA), no depth write — used by DARK_GLOW particles
	static RenderType.CompositeState particleDarkGlowState = RenderType.CompositeState.builder()
			.setShaderState(new ShaderStateShard(GameRenderer::getParticleShader))
			.setTextureState(new TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setWriteMaskState(COLOR_WRITE)
			.createCompositeState(false);

	public static final RenderType PARTICLE_DARK_GLOW = create("hutoslib:particle_dark_glow", DefaultVertexFormat.PARTICLE,
			VertexFormat.Mode.QUADS, 256, false, false, particleDarkGlowState);

	// Additive blend, no cull, no depth write — used by LIGHTNING_BOLT particles
	static RenderType.CompositeState particleLightningBoltState = RenderType.CompositeState.builder()
			.setShaderState(new ShaderStateShard(GameRenderer::getParticleShader))
			.setTextureState(new TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.setWriteMaskState(COLOR_WRITE)
			.setCullState(NO_CULL)
			.createCompositeState(false);

	public static final RenderType PARTICLE_LIGHTNING_BOLT = create("hutoslib:particle_lightning_bolt", DefaultVertexFormat.PARTICLE,
			VertexFormat.Mode.QUADS, 256, false, false, particleLightningBoltState);

	// --- ParticleRenderType records (1.21.4+ record constructor) ---

	public static final ParticleRenderType GLOW_RENDER =
			new ParticleRenderType("hutoslib:glow_rend", PARTICLE_GLOW);

	public static final ParticleRenderType DARK_GLOW_RENDER =
			new ParticleRenderType("hutoslib:dark_glow_rend", PARTICLE_DARK_GLOW);

	public HLRenderTypeInit(String nameIn, VertexFormat formatIn, Mode drawModeIn, int bufferSizeIn,
			boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

}
