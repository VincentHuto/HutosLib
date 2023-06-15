package com.vincenthuto.hutoslib.common.block;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HLBlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HutosLib.MOD_ID);

	public static final RegistryObject<Block> display_pedestal = BLOCKS.register("display_pedestal",
			() -> new BlockDisplayPedestal(
					Block.Properties.of().strength(50f, 2000f).sound(SoundType.STONE).noOcclusion()));

	public static final RegistryObject<Block> display_glass = BLOCKS.register("display_glass",
			() -> new BlockDisplayGlass(
					Block.Properties.of().strength(0.1f, 1f).sound(SoundType.GLASS).noOcclusion()));

}
