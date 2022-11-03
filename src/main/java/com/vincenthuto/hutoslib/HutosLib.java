package com.vincenthuto.hutoslib;

import com.mojang.datafixers.util.Pair;
import com.vincenthuto.hutoslib.client.HLForgeEvents;
import com.vincenthuto.hutoslib.client.particle.HLParticleInit;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.client.screen.guide.lib.HLLib;
import com.vincenthuto.hutoslib.common.banner.BannerFinderBannerSlot;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;
import com.vincenthuto.hutoslib.common.block.HLBlockInit;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import com.vincenthuto.hutoslib.common.enchant.HLEnchantInit;
import com.vincenthuto.hutoslib.common.item.HLItemInit;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaEvents;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {
	public static final String MOD_ID = "hutoslib";
	public static IProxy proxy = new IProxy() {
	};

	@SuppressWarnings("deprecation")
	public HutosLib() {
		MinecraftForge.EVENT_BUS.register(this);
		DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::registerCapability);
		modEventBus.addListener(HLForgeEvents::initKeybinds);
		HLItemInit.ITEMS.register(modEventBus);
		HLItemInit.SPECIALITEMS.register(modEventBus);
		HLItemInit.BANNERPATTERNS.register(modEventBus);
		HLBlockInit.BLOCKS.register(modEventBus);
		HLParticleInit.PARTICLE_TYPES.register(modEventBus);
		HLBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		HLEnchantInit.ENCHANTS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(KarmaEvents.class);
		HlContainerInit.CONTAINERS.register(modEventBus);
		HlContainerInit.RECIPESERIALIZERS.register(modEventBus);
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
	public static void onRegisterItems(final RegisterEvent event) {
		if (event.getRegistryKey() != ForgeRegistries.Keys.ITEMS) {
			return;
		}

		HLBlockInit.BLOCKS.getEntries().stream().map(m -> new Pair<>(m.get(), m.getId())).map(t -> createItemBlock(t))
				.forEach(item -> registerBlockItem(event, item));
	}

	private static void registerBlockItem(RegisterEvent event, Pair<ResourceLocation, BlockItem> item) {
		event.register(ForgeRegistries.Keys.ITEMS, helper -> helper.register(item.getFirst(), item.getSecond()));
	}

	public static Pair<ResourceLocation, BlockItem> createItemBlock(Pair<Block, ResourceLocation> block) {
		return Pair.of(block.getSecond(),
				new BlockItem((Block) block.getFirst(), new Item.Properties().tab(HutosLibItemGroup.instance)));
	}
	
	private void commonSetup(final FMLCommonSetupEvent event) {
		HLPacketHandler.registerChannels();
		BannerExtensionSlot.register();
		BannerFinderBannerSlot.initFinder();

	}

	private void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IBannerSlotItem.class);
		event.register(BannerExtensionSlot.class);
		event.register(IKarma.class);
		BannerSlotCapability.register(event);

	}


	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HLBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ItemBlockRenderTypes.setRenderLayer(HLBlockInit.display_glass.get(), RenderType.cutoutMipped());

		}
		event.enqueueWork(() -> {
			MenuScreens.register(HlContainerInit.banner_slot_container.get(), BannerSlotScreen::new);
		});
		HLLib hl = new HLLib();
		hl.registerTome();

	}

}
