package com.vincenthuto.hutoslib;

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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
		modEventBus.addListener(this::loadComplete);
		HLItemInit.SPECIALITEMS.register(modEventBus);
		HLItemInit.BANNERPATTERNS.register(modEventBus);
		HLBlockInit.BLOCKS.register(modEventBus);
		HLItemInit.ITEMS.register(modEventBus);
		HLBlockInit.BLOCKITEMS.register(modEventBus);
		HLParticleInit.PARTICLE_TYPES.register(modEventBus);
		HLBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		HlContainerInit.CONTAINERS.register(modEventBus);
		HlContainerInit.RECIPESERIALIZERS.register(modEventBus);
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

	private void commonSetup(final FMLCommonSetupEvent event) {
		HLPacketHandler.registerChannels();
		BannerExtensionSlot.register();
		BannerFinderBannerSlot.initFinder();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			if (FMLEnvironment.dist == Dist.CLIENT)
				HLForgeEvents.initKeybinds();
		});
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
