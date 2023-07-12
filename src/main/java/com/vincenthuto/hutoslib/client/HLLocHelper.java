package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;

public class HLLocHelper {

	public static ResourceLocation entityPrefix(String loc) {
		return HutosLib.rloc( "textures/entity/" + loc);
	}

	public static ResourceLocation guiPrefix(String loc) {
		return HutosLib.rloc( "textures/gui/" + loc);
	}

	public static ResourceLocation texturePrefix(String loc) {
		return HutosLib.rloc( "textures/" + loc);
	}

	ResourceLocation test = HutosLib.rloc( "textures/gui/title_tab.png");
}
