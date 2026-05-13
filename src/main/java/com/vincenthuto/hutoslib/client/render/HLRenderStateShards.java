package com.vincenthuto.hutoslib.client.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class HLRenderStateShards {
	public static RenderType getGui(Identifier texture) {
		return RenderTypes.entityCutout(texture);
	}

	public static RenderType getGuiTranslucent(Identifier texture) {
		return RenderTypes.entityTranslucent(texture);
	}
}
