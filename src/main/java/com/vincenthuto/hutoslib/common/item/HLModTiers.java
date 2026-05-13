package com.vincenthuto.hutoslib.common.item;

import net.minecraft.world.item.ToolMaterial;

public enum HLModTiers {
	WOOD(ToolMaterial.WOOD),
	STONE(ToolMaterial.STONE),
	IRON(ToolMaterial.IRON),
	DIAMOND(ToolMaterial.DIAMOND),
	GOLD(ToolMaterial.GOLD),
	NETHERITE(ToolMaterial.NETHERITE);

	private final ToolMaterial material;

	HLModTiers(ToolMaterial material) {
		this.material = material;
	}

	public ToolMaterial material() {
		return material;
	}
}
