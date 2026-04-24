package com.vincenthuto.hutoslib.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import static com.vincenthuto.hutoslib.HutosLib.MOD_ID;

@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
public class HLRenderTypeInit {

	// --- Custom RenderPipelines ---

	/** Additive (SRC_ALPHA, ONE) particle pipeline, no depth write — for GLOW / LIGHTNING_BOLT particles */
	public static final RenderPipeline PARTICLE_ADDITIVE_NO_DEPTH = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
			.withLocation(Identifier.parse(MOD_ID + ":pipeline/particle_additive_no_depth"))
			.withBlend(BlendFunction.LIGHTNING)
			.withDepthWrite(false)
			.build();

	/** Translucent (SRC_ALPHA, ONE_MINUS_SRC_ALPHA) particle pipeline, no depth write — for DARK_GLOW particles */
	public static final RenderPipeline PARTICLE_TRANSLUCENT_NO_DEPTH = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
			.withLocation(Identifier.parse(MOD_ID + ":pipeline/particle_translucent_no_depth"))
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthWrite(false)
			.build();

	/** Additive particle pipeline, no depth write, no cull — for LIGHTNING_BOLT particles */
	public static final RenderPipeline PARTICLE_ADDITIVE_NO_DEPTH_NO_CULL = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
			.withLocation(Identifier.parse(MOD_ID + ":pipeline/particle_additive_no_depth_no_cull"))
			.withBlend(BlendFunction.LIGHTNING)
			.withDepthWrite(false)
			.withCull(false)
			.build();

	@SubscribeEvent
	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(PARTICLE_ADDITIVE_NO_DEPTH);
		event.registerPipeline(PARTICLE_TRANSLUCENT_NO_DEPTH);
		event.registerPipeline(PARTICLE_ADDITIVE_NO_DEPTH_NO_CULL);
	}

	// --- SingleQuadParticle.Layer constants for custom particles ---

	/** Additive-blended glow particle layer (particle atlas, no depth write). */
	public static final SingleQuadParticle.Layer LAYER_GLOW = new SingleQuadParticle.Layer(
			true, TextureAtlas.LOCATION_PARTICLES, PARTICLE_ADDITIVE_NO_DEPTH);

	/** Translucent dark-glow particle layer (particle atlas, no depth write). */
	public static final SingleQuadParticle.Layer LAYER_DARK_GLOW = new SingleQuadParticle.Layer(
			true, TextureAtlas.LOCATION_PARTICLES, PARTICLE_TRANSLUCENT_NO_DEPTH);

	/** Additive, no-cull lightning-bolt particle layer (particle atlas, no depth write). */
	public static final SingleQuadParticle.Layer LAYER_LIGHTNING_BOLT = new SingleQuadParticle.Layer(
			true, TextureAtlas.LOCATION_PARTICLES, PARTICLE_ADDITIVE_NO_DEPTH_NO_CULL);

	// --- RenderTypes using the vanilla LIGHTNING pipeline (POSITION_COLOR, additive) ---
	public static final RenderType LIGHTNING = RenderType.create("hutoslib_lightning",
			RenderSetup.builder(RenderPipelines.LIGHTNING).createRenderSetup());

	// --- Particle RenderTypes (grouping keys — just names in 1.21.11) ---

	public static final ParticleRenderType GLOW_RENDER =
			new ParticleRenderType("hutoslib:glow_rend");

	public static final ParticleRenderType DARK_GLOW_RENDER =
			new ParticleRenderType("hutoslib:dark_glow_rend");

	public static final ParticleRenderType LIGHTNING_BOLT_RENDER =
			new ParticleRenderType("hutoslib:lightning_bolt_rend");
}
