package com.vincenthuto.hutoslib.common.util;

import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.resources.Identifier;

public final class HLResourceUtils {
	private HLResourceUtils() {
	}

	public static Identifier getBySplit(String loc) {
		if (loc != null && loc.contains(":")) {
			String[] split = loc.split(":", 2);
			if (split.length == 2) {
				Identifier id = Identifier.tryBuild(split[0], split[1]);
				if (id != null) {
					return id;
				}
			}
		}
		return HutosLib.rloc(loc);
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
