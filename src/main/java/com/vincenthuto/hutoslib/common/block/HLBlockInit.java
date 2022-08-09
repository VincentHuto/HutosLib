package com.vincenthuto.hutoslib.common.block;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.HutosLib.HutosLibItemGroup;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)

public class HLBlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HutosLib.MOD_ID);
	public static final DeferredRegister<Item> BLOCKITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			HutosLib.MOD_ID);

	public static final RegistryObject<Block> display_pedestal = HLBlockUtils.registerBlockItems("display_pedestal",
			() -> new BlockDisplayPedestal(
					Block.Properties.of(Material.STONE).strength(50f, 2000f).sound(SoundType.STONE).noOcclusion()),
			new Item.Properties().tab(HutosLibItemGroup.instance), BLOCKS, BLOCKITEMS);

	public static final RegistryObject<Block> display_glass = HLBlockUtils.registerBlockItems("display_glass",
			() -> new BlockDisplayGlass(
					Block.Properties.of(Material.GLASS).strength(0.1f, 1f).sound(SoundType.GLASS).noOcclusion()),
			new Item.Properties().tab(HutosLibItemGroup.instance), BLOCKS, BLOCKITEMS);


	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		ItemBlockRenderTypes.setRenderLayer(HLBlockInit.display_glass.get(), RenderType.translucent());

	}
	
}
