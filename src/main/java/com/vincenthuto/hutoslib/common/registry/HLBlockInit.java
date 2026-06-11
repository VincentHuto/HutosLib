package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.BlockDisplayGlass;
import com.vincenthuto.hutoslib.common.block.BlockDisplayPedestal;
import com.vincenthuto.hutoslib.common.block.BlockGenericParticleTester;
import com.vincenthuto.hutoslib.common.block.BlockLightningTester;
import com.vincenthuto.hutoslib.common.block.BlockTendrilTester;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HLBlockInit {
public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, HutosLib.MOD_ID);
public static final DeferredRegister<Block> MODELEDBLOCKS = DeferredRegister.create(Registries.BLOCK, HutosLib.MOD_ID);

public static final DeferredHolder<Block, Block> display_pedestal = MODELEDBLOCKS.register("display_pedestal",
() -> new BlockDisplayPedestal(
Block.Properties.of().requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.STONE).noOcclusion()));

public static final DeferredHolder<Block, Block> display_glass = BLOCKS.register("display_glass",
() -> new BlockDisplayGlass(
Block.Properties.of().strength(0.3f).sound(SoundType.GLASS).noOcclusion()));

public static final DeferredHolder<Block, Block> lightning_tester_block = BLOCKS.register("lightning_tester_block",
() -> new BlockLightningTester(
Block.Properties.of().strength(1.5F, 6.0F).sound(SoundType.AMETHYST).noOcclusion()));

public static final DeferredHolder<Block, Block> tendril_tester_block = BLOCKS.register("tendril_tester_block",
() -> new BlockTendrilTester(
Block.Properties.of().strength(1.5F, 6.0F).sound(SoundType.AMETHYST).noOcclusion()));

public static final DeferredHolder<Block, Block> generic_particle_tester_block = BLOCKS.register("generic_particle_tester_block",
() -> new BlockGenericParticleTester(
Block.Properties.of().strength(1.5F, 6.0F).sound(SoundType.AMETHYST).noOcclusion()));
}
