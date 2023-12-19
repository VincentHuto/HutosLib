package com.vincenthuto.hutoslib.common.data;

import java.util.Map;
import java.util.stream.Collectors;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class HLBlockLootTableProvider extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(HLBlockInit.display_glass.get());
        dropSelf(HLBlockInit.display_pedestal.get());

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(HutosLib.MOD_ID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}