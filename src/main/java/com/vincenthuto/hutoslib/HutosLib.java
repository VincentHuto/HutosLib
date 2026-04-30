package com.vincenthuto.hutoslib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.common.banner.BannerFinderBannerSlot;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.data.HLDataGeneration;
import com.vincenthuto.hutoslib.common.book.knowledge.BookEntryRegistry;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.skilltree.SkillTreePlaceboReloadListener;
import com.vincenthuto.hutoslib.common.event.GuideBookUnlockEvents;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod("hutoslib")
@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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
if (event.getRegistryKey() != Registries.ITEM) {
return;
}
HLBlockInit.BLOCKS.getEntries().stream().map(m -> new Pair<Block, ResourceLocation>(m.get(), m.getId())).map(t -> createItemBlock(t))
.forEach(item -> registerBlockItem(event, item));
HLBlockInit.MODELEDBLOCKS.getEntries().stream().map(m -> new Pair<Block, ResourceLocation>(m.get(), m.getId()))
.map(t -> createItemBlock(t)).forEach(item -> registerBlockItem(event, item));
}

private static void registerBlockItem(RegisterEvent event, Pair<ResourceLocation, BlockItem> item) {
event.register(Registries.ITEM, helper -> helper.register(item.getFirst(), item.getSecond()));
}

public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
.create(Registries.CREATIVE_MODE_TAB, HutosLib.MOD_ID);
public static final DeferredHolder<CreativeModeTab, CreativeModeTab> hutoslibtab = CREATIVETABS.register("hutoslibtab",
() -> CreativeModeTab.builder().title(Component.translatable("item_group." + MOD_ID + ".hutoslibtab"))
.icon(() -> new ItemStack(HLItemInit.obsidian_flakes.get())).build());

public HutosLib(IEventBus modEventBus) {
if (FMLEnvironment.dist.isClient()) {
proxy = new ClientProxy();
}
modEventBus.addListener(this::commonSetup);
modEventBus.addListener(this::clientSetup);
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
HlContainerInit.CONTAINERS.register(modEventBus);
HlContainerInit.RECIPESERIALIZERS.register(modEventBus);
HLAttachmentTypes.register(modEventBus);
}

	private void clientSetup(final FMLClientSetupEvent event) {
		BlockEntityRenderers.register(HLBlockEntityInit.display_pedestal.get(), RenderTileDisplayPedestal::new);
	}

	@SubscribeEvent
	public static void onRegisterMenuScreens(final net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
		event.register(HlContainerInit.banner_slot_container.get(), BannerSlotScreen::new);
	}

private void commonSetup(final FMLCommonSetupEvent event) {
BookPlaceboReloadListener.INSTANCE.registerToBus();
SkillTreePlaceboReloadListener.INSTANCE.registerToBus();
BannerFinderBannerSlot.initFinder();
// Register test unlock: picking up a diamond unlocks the guide's locked_test chapter
BookEntryRegistry.registerItemUnlock(
        BuiltInRegistries.ITEM.getKey(Items.DIAMOND),
        GuideBookUnlockEvents.LOCKED_TEST_ENTRY);
}

public void buildContents(BuildCreativeModeTabContentsEvent output) {
if (output.getTabKey() == hutoslibtab.getKey()) {
HLItemInit.ITEMS.getEntries().forEach(i -> output.accept(i.get()));
HLItemInit.HANDHELDITEMS.getEntries().forEach(i -> output.accept(i.get()));
HLItemInit.SPECIALITEMS.getEntries().forEach(i -> output.accept(i.get()));
HLBlockInit.BLOCKS.getEntries().forEach(i -> output.accept(i.get()));
HLBlockInit.MODELEDBLOCKS.getEntries().forEach(i -> output.accept(i.get()));
}
}

public static ResourceLocation rloc(String path) {
return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
}
}
