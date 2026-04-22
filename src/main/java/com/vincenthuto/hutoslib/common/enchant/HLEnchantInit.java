package com.vincenthuto.hutoslib.common.enchant;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class HLEnchantInit {
	public static final ResourceKey<Enchantment> GLIMMER =
		ResourceKey.create(Registries.ENCHANTMENT, HutosLib.rloc("glimmer"));
}
