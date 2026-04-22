package com.vincenthuto.hutoslib.common.enchant;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public class HLEnchantInit {

public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(Registries.ENCHANTMENT,
HutosLib.MOD_ID);

public static final EnchantmentCategory ALL = EnchantmentCategory.create("all", (t) -> t instanceof Item);
public static final RegistryObject<Enchantment> glimmer = ENCHANTS.register("glimmer",
() -> new EnchantmentGlimmer(Enchantment.Rarity.COMMON, EquipmentSlot.values()));
}
