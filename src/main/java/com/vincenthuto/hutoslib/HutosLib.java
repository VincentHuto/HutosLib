package com.vincenthuto.hutoslib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.common.banner.BannerFinderBannerSlot;
import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import com.vincenthuto.hutoslib.common.data.HLDataGeneration;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.skilltree.SkillTreePlaceboReloadListener;
import com.vincenthuto.hutoslib.common.enchant.HLEnchantInit;
import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaEvents;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod("hutoslib")
@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLib {
	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "hutoslib";

	public static IProxy proxy = new IProxy() {
	};

	public static Pair<ResourceLocation, BlockItem> createItemBlock(Pair<Block, ResourceLocation> block) {
		return Pair.of(block.getSecond(), new BlockItem(block.getFirst(), new Item.Properties()));
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegisterEvent event) {
		if (event.getRegistryKey() != ForgeRegistries.Keys.ITEMS) {
			return;
		}
		HLBlockInit.BLOCKS.getEntries().stream().map(m -> new Pair<>(m.get(), m.getId())).map(t -> createItemBlock(t))
				.forEach(item -> registerBlockItem(event, item));
		HLBlockInit.MODELEDBLOCKS.getEntries().stream().map(m -> new Pair<>(m.get(), m.getId()))
				.map(t -> createItemBlock(t)).forEach(item -> registerBlockItem(event, item));
	}

	private static void registerBlockItem(RegisterEvent event, Pair<ResourceLocation, BlockItem> item) {
		event.register(ForgeRegistries.Keys.ITEMS, helper -> helper.register(item.getFirst(), item.getSecond()));
	}

	public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, HutosLib.MOD_ID);
	public static final RegistryObject<CreativeModeTab> hutoslibtab = CREATIVETABS.register("hutoslibtab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group." + MOD_ID + ".hutoslibtab"))
					.icon(() -> new ItemStack(HLItemInit.obsidian_flakes.get())).build());

	public HutosLib() {
		MinecraftForge.EVENT_BUS.register(this);
		DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::registerCapability);
		modEventBus.addListener(this::buildContents);
		modEventBus.addListener(HLDataGeneration::generate);
		HLItemInit.ITEMS.register(modEventBus);
		HLItemInit.HANDHELDITEMS.register(modEventBus);
		HLItemInit.SPECIALITEMS.register(modEventBus);
		HLItemInit.BANNERPATTERNS.register(modEventBus);
		HLBlockInit.BLOCKS.register(modEventBus);
		HLBlockInit.MODELEDBLOCKS.register(modEventBus);
		CREATIVETABS.register(modEventBus);
		HLParticleInit.PARTICLE_TYPES.register(modEventBus);
		HLBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
		HLEnchantInit.ENCHANTS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(KarmaEvents.class);
		HlContainerInit.CONTAINERS.register(modEventBus);
		HlContainerInit.RECIPESERIALIZERS.register(modEventBus);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HLBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ItemBlockRenderTypes.setRenderLayer(HLBlockInit.display_glass.get(), RenderType.cutoutMipped());

		}
		event.enqueueWork(() -> {
			MenuScreens.register(HlContainerInit.banner_slot_container.get(), BannerSlotScreen::new);
		});

	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		BookPlaceboReloadListener.INSTANCE.registerToBus();
		SkillTreePlaceboReloadListener.INSTANCE.registerToBus();

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

	public void buildContents(BuildCreativeModeTabContentsEvent output) {

		if (output.getTabKey() == hutoslibtab.getKey()) {

			// Items
			HLItemInit.ITEMS.getEntries().forEach(i -> output.accept(i.get()));
			HLItemInit.HANDHELDITEMS.getEntries().forEach(i -> output.accept(i.get()));
			HLItemInit.SPECIALITEMS.getEntries().forEach(i -> output.accept(i.get()));
			// Blocks
			HLBlockInit.BLOCKS.getEntries().forEach(i -> output.accept(i.get()));
			HLBlockInit.MODELEDBLOCKS.getEntries().forEach(i -> output.accept(i.get()));
		}

	}

	public static ResourceLocation rloc(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
