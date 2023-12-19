package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.BlockDisplayGlass;
import com.vincenthuto.hutoslib.common.block.BlockDisplayPedestal;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HLBlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HutosLib.MOD_ID);
	public static final DeferredRegister<Block> MODELEDBLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HutosLib.MOD_ID);


	public static final RegistryObject<Block> display_pedestal = MODELEDBLOCKS.register("display_pedestal",
			() -> new BlockDisplayPedestal(
					Block.Properties.of().requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.STONE).noOcclusion()));

	public static final RegistryObject<Block> display_glass = BLOCKS.register("display_glass",
			() -> new BlockDisplayGlass(
					Block.Properties.of().strength(0.3f).sound(SoundType.GLASS).noOcclusion()));

}
