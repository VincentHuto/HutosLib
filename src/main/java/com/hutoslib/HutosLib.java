package com.hutoslib;

import com.hutoslib.client.model.IAnimatable;
import com.hutoslib.client.particle.ParticleInit;
import com.hutoslib.client.render.tile.RenderTileDisplayPedestal;
import com.hutoslib.common.HutosLibPacketHandler;
import com.hutoslib.common.block.HutosLibBlockInit;
import com.hutoslib.common.item.HutosLibItemInit;
import com.hutoslib.common.tile.HutosLibTileEntityInit;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {

	public static final String MOD_ID = "hutoslib";
	public static volatile boolean hasInitialized;

	public HutosLib() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ParticleInit.PARTICLE_TYPES.register(modEventBus);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		HutosLibItemInit.ITEMS.register(modEventBus);
		HutosLibBlockInit.BLOCKS.register(modEventBus);
		HutosLibTileEntityInit.TILES.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
	}

	synchronized public static void initialize() {
		if (!hasInitialized) {
		}
		hasInitialized = true;
	}

	// Creative Tab
	public static class HutosLibItemGroup extends ItemGroup {
		public static final HutosLibItemGroup instance = new HutosLibItemGroup(ItemGroup.GROUPS.length, "hutoslibtab");

		public HutosLibItemGroup(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(HutosLibItemInit.obsidian_flakes.get());
		}
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		// Automatically Registers BlockItems
		final IForgeRegistry<Item> registry = event.getRegistry();
		HutosLibBlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			final Item.Properties properties = new Item.Properties().group(HutosLibItemGroup.instance);
			final BlockItem blockItem = new BlockItem(block, properties);
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		IAnimatable.registerCapability();
		HutosLibPacketHandler.registerChannels();

	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(HutosLibTileEntityInit.display_pedestal.get(),
				RenderTileDisplayPedestal::new);

	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
	}
}
