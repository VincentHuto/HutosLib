/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
package com.vincenthuto.hutoslib.client.render;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

/**
 * GUI render type helpers using the 1.21.11 pipeline/RenderSetup API.
 * The old RenderStateShard/CompositeState approach is no longer available.
 */
public class HLRenderStateShards {
	private static final Function<Identifier, RenderType> GUI_CUTOUT;
	private static final Function<Identifier, RenderType> GUI_TRANSLUCENT;

	static {
		// Opaque textured: no blend, no depth test (GUI_OPAQUE_TEXTURED_BACKGROUND pipeline)
		GUI_CUTOUT = Util.memoize(texture -> RenderType.create(
				"hl_gui_" + texture,
				RenderSetup.builder(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND)
						.withTexture("Sampler0", texture)
						.createRenderSetup()));

		// Translucent textured: SRC_ALPHA / ONE_MINUS_SRC_ALPHA blend, no depth test (GUI_TEXTURED pipeline)
		GUI_TRANSLUCENT = Util.memoize(texture -> RenderType.create(
				"hl_gui_translucent_" + texture,
				RenderSetup.builder(RenderPipelines.GUI_TEXTURED)
						.withTexture("Sampler0", texture)
						.createRenderSetup()));
	}

	public static RenderType getGui(Identifier texture) {
		return GUI_CUTOUT.apply(texture);
	}

	public static RenderType getGuiTranslucent(Identifier texture) {
		return GUI_TRANSLUCENT.apply(texture);
	}
}
