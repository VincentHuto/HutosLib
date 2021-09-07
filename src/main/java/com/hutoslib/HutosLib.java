package com.hutoslib;

import com.hutoslib.client.model.capability.IAnimatable;
import com.hutoslib.client.particle.HutosLibParticleInit;
import com.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.hutoslib.common.block.HutosLibBlockInit;
import com.hutoslib.common.block.entity.HutosLibBlockEntityInit;
import com.hutoslib.common.item.HutosLibItemInit;
import com.hutoslib.common.network.HutosLibPacketHandler;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {

	public static final String MOD_ID = "hutoslib";
	public static volatile boolean hasInitialized;

	public HutosLib() {

		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapability);
		HutosLibItemInit.ITEMS.register(modEventBus);
		HutosLibBlockInit.BLOCKS.register(modEventBus);
		HutosLibParticleInit.PARTICLE_TYPES.register(modEventBus);
		HutosLibBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::onClientTick);

	}

	synchronized public static void initialize() {
		if (!hasInitialized) {
		}
		hasInitialized = true;
	}

	// Creative Tab
	public static class HutosLibItemGroup extends CreativeModeTab {
		public static final HutosLibItemGroup instance = new HutosLibItemGroup(CreativeModeTab.getGroupCountSafe(),
				"hutoslibtab");

		public HutosLibItemGroup(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(HutosLibItemInit.obsidian_flakes.get());
		}
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		// Automatically Registers BlockItems
		final IForgeRegistry<Item> registry = event.getRegistry();
		HutosLibBlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			final Item.Properties properties = new Item.Properties().tab(HutosLibItemGroup.instance);
			final BlockItem blockItem = new BlockItem(block, properties);
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		HutosLibPacketHandler.registerChannels();

	}

	private void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IAnimatable.class);

	}

	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HutosLibBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);

	}

	public void onClientTick(ClientTickEvent event) {
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartedEvent event) {
	}

	// Combined a few methods into one more generic one
	public static ItemStack findItemInPlayerInv(Player player, Class<? extends Item> item) {
		if (item.isInstance(player.getOffhandItem().getItem()))
			return player.getMainHandItem();
		if (item.isInstance(player.getMainHandItem().getItem()))
			return player.getOffhandItem();
		Inventory inventory = player.getInventory();
		for (int i = 0; i <= 35; i++) {
			ItemStack stack = inventory.getItem(i);
			if (item.isInstance(stack.getItem()))
				return stack;
		}
		return ItemStack.EMPTY;
	}

}
