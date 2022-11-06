package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;

public class HLLocHelper {

	public static ResourceLocation entityPrefix(String loc) {
		return new ResourceLocation(HutosLib.MOD_ID, "textures/entity/" + loc);
	}

	public static ResourceLocation guiPrefix(String loc) {
		return new ResourceLocation(HutosLib.MOD_ID, "textures/gui/" + loc);
	}

	public static ResourceLocation texturePrefix(String loc) {
		return new ResourceLocation(HutosLib.MOD_ID, "textures/" + loc);
	}

	ResourceLocation test = new ResourceLocation(HutosLib.MOD_ID, "textures/gui/title_tab.png");
}
