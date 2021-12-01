package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.client.ForgeEvents;
import com.vincenthuto.hutoslib.client.particle.HLParticleInit;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.client.screen.guide.lib.HLLib;
import com.vincenthuto.hutoslib.common.block.HLBlockInit;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot.AttachHandlers;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot.EventHandlers;
import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import com.vincenthuto.hutoslib.common.enchant.HLEnchantInit;
import com.vincenthuto.hutoslib.common.item.HLItemInit;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaEvents;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
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
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {
	public static final String MOD_ID = "hutoslib";
	public static IProxy proxy = new IProxy() {
	};

	public HutosLib() {
		MinecraftForge.EVENT_BUS.register(this);
		DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::registerCapability);
		modEventBus.addListener(this::loadComplete);
		modEventBus.addGenericListener(MenuType.class, this::registerContainers);
		HLItemInit.ITEMS.register(modEventBus);
		HLItemInit.SPECIALITEMS.register(modEventBus);
		HLBlockInit.BLOCKS.register(modEventBus);
		HLParticleInit.PARTICLE_TYPES.register(modEventBus);
		HLBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		HLEnchantInit.ENCHANTS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(KarmaEvents.class);
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
			return new ItemStack(HLItemInit.obsidian_flakes.get());
		}
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		// Automatically Registers BlockItems
		final IForgeRegistry<Item> registry = event.getRegistry();
		HLBlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			final Item.Properties properties = new Item.Properties().tab(HutosLibItemGroup.instance);
			final BlockItem blockItem = new BlockItem(block, properties);
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		HLPacketHandler.registerChannels();
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
	//	event.register(IAnimatable.class);
		event.register(IBannerSlotItem.class);
		event.register(BannerExtensionSlot.class);
		event.register(IKarma.class);

	}

	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HLBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
		event.enqueueWork(() -> {
			MenuScreens.register(BannerSlotContainer.TYPE, BannerSlotScreen::new);
		});
		HLLib hemo = new HLLib();
		hemo.registerTome();
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
