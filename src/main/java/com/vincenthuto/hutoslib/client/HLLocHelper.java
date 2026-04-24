package com.vincenthuto.hutoslib.client;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.Identifier;

public class HLLocHelper {

	public static Identifier getBySplit(String loc) {
		if (loc != null && loc.contains(":")) {
			String[] split = loc.split(":");
			Identifier rl = Identifier.tryBuild(split[0], split[1]);
			if (rl != null) {
				return rl;
			}
		}
		return  HutosLib.rloc(loc);
	}

	public static Identifier entityPrefix(String loc) {
		return HutosLib.rloc("textures/entity/" + loc);
	}

	public static Identifier guiPrefix(String loc) {
		return HutosLib.rloc("textures/gui/guide/" + loc);
	}

	public static Identifier texturePrefix(String loc) {
		return HutosLib.rloc("textures/" + loc);
	}

}
