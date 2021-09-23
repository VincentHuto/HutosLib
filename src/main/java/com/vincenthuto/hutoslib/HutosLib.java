package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.client.ForgeEvents;
import com.vincenthuto.hutoslib.client.model.capability.IAnimatable;
import com.vincenthuto.hutoslib.client.particle.HutosLibParticleInit;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.common.block.HutosLibBlockInit;
import com.vincenthuto.hutoslib.common.block.entity.HutosLibBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot.AttachHandlers;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot.EventHandlers;
import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import com.vincenthuto.hutoslib.common.item.HutosLibItemInit;
import com.vincenthuto.hutoslib.common.network.HutosLibPacketHandler;
import com.vincenthuto.hutoslib.common.recipe.ArmBannerCraftRecipe;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {

	public static final String MOD_ID = "hutoslib";

	public HutosLib() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::registerCapability);
		modEventBus.addListener(this::loadComplete);
		modEventBus.addGenericListener(MenuType.class, this::registerContainers);
		HutosLibItemInit.ITEMS.register(modEventBus);
		HutosLibItemInit.SPECIALITEMS.register(modEventBus);
		HutosLibBlockInit.BLOCKS.register(modEventBus);
		HutosLibParticleInit.PARTICLE_TYPES.register(modEventBus);
		HutosLibBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);

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
		MinecraftForge.EVENT_BUS.register(new AttachHandlers());
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			if (FMLEnvironment.dist == Dist.CLIENT)
				ForgeEvents.initKeybinds();
		});
	}

	private void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IAnimatable.class);
		event.register(IBannerSlotItem.class);
		event.register(BannerExtensionSlot.class);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HutosLibBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
		event.enqueueWork(() -> {
			MenuScreens.register(BannerSlotContainer.TYPE, BannerSlotScreen::new);
		});
	}

	public void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
		event.getRegistry()
				.registerAll(new MenuType<>(BannerSlotContainer::new).setRegistryName("banner_slot_container"));
	}

	@SubscribeEvent
	public static void onRecipeRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
		event.getRegistry().register(new SimpleRecipeSerializer<>(ArmBannerCraftRecipe::new)
				.setRegistryName(new ResourceLocation(MOD_ID, "arm_banner_craft")));
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
