/*
 *  Modified code from BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
package com.vincenthuto.hutoslib.client.render;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

//This extends RenderStateShard to get access to various protected members
public class HLRenderStateShards extends RenderStateShard {
	private static final Function<ResourceLocation, RenderType> GUI_CUTOUT;
	private static final Function<ResourceLocation, RenderType> GUI_TRANSLUCENT;

	static {
		GUI_CUTOUT = Util.memoize(texture -> createDefault("gui_" + texture, DefaultVertexFormat.POSITION_COLOR_TEX,
				Mode.QUADS, makeGuiState(texture).createCompositeState(false)));
		GUI_TRANSLUCENT = Util.memoize(texture -> createDefault("gui_translucent_" + texture,
				DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS,
				makeGuiState(texture).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(false)));
	}

	private HLRenderStateShards(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
		super(p_110161_, p_110162_, p_110163_);
	}

	public static RenderType getGui(ResourceLocation texture) {
		return GUI_CUTOUT.apply(texture);
	}

	public static RenderType getGuiTranslucent(ResourceLocation texture) {
		return GUI_TRANSLUCENT.apply(texture);
	}

	private static CompositeState.CompositeStateBuilder makeGuiState(ResourceLocation texture) {
		return RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false))
				.setShaderState(POSITION_COLOR_TEX_SHADER);
	}

	private static RenderType createDefault(String name, VertexFormat format, VertexFormat.Mode mode,
			RenderType.CompositeState state) {
		return RenderType.create(name, format, mode, 256, false, false, state);
	}

}
