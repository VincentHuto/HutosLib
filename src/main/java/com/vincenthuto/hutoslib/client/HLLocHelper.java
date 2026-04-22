package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;

public class HLLocHelper {

	public static ResourceLocation getBySplit(String loc) {
		if (loc != null && loc.contains(":")) {
			String[] split = loc.split(":");
			ResourceLocation rl = ResourceLocation.tryBuild(split[0], split[1]);
			if (rl != null) {
				return rl;
			}
		}
		return  HutosLib.rloc(loc);
	}

	public static ResourceLocation entityPrefix(String loc) {
		return HutosLib.rloc("textures/entity/" + loc);
	}

	public static ResourceLocation guiPrefix(String loc) {
		return HutosLib.rloc("textures/gui/guide/" + loc);
	}

	public static ResourceLocation texturePrefix(String loc) {
		return HutosLib.rloc("textures/" + loc);
	}

}
