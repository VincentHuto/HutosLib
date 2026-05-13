package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.BlockDisplayGlass;
import com.vincenthuto.hutoslib.common.block.BlockDisplayPedestal;

import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HLBlockInit {
public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HutosLib.MOD_ID);
public static final DeferredRegister.Blocks MODELEDBLOCKS = DeferredRegister.createBlocks(HutosLib.MOD_ID);

public static final DeferredBlock<BlockDisplayPedestal> display_pedestal = MODELEDBLOCKS.registerBlock(
		"display_pedestal",
		BlockDisplayPedestal::new,
		properties -> properties.requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.STONE)
				.noOcclusion());

public static final DeferredBlock<BlockDisplayGlass> display_glass = BLOCKS.registerBlock("display_glass",
		BlockDisplayGlass::new, properties -> properties.strength(0.3f).sound(SoundType.GLASS).noOcclusion());
}
