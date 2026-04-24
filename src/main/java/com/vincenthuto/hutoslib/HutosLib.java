package com.vincenthuto.hutoslib;

import com.mojang.datafixers.util.Pair;
import com.vincenthuto.hutoslib.client.render.block.RenderTileDisplayPedestal;
import com.vincenthuto.hutoslib.client.screen.BannerSlotScreen;
import com.vincenthuto.hutoslib.common.banner.BannerFinderBannerSlot;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.data.HLDataGeneration;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.skilltree.SkillTreePlaceboReloadListener;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hutoslib")
@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HutosLib {
public static final Logger LOGGER = LogManager.getLogger();

public static final String MOD_ID = "hutoslib";

public static IProxy proxy = new IProxy() {
};

public static Pair<Identifier, BlockItem> createItemBlock(Pair<Block, Identifier> block) {
return Pair.of(block.getSecond(), new BlockItem(block.getFirst(), new Item.Properties()));
}

@SubscribeEvent
public static void onRegisterItems(final RegisterEvent event) {
if (event.getRegistryKey() != Registries.ITEM) {
return;
}
HLBlockInit.BLOCKS.getEntries().stream().map(m -> new Pair<Block, Identifier>(m.get(), m.getId())).map(t -> createItemBlock(t))
.forEach(item -> registerBlockItem(event, item));
HLBlockInit.MODELEDBLOCKS.getEntries().stream().map(m -> new Pair<Block, Identifier>(m.get(), m.getId()))
.map(t -> createItemBlock(t)).forEach(item -> registerBlockItem(event, item));
}

private static void registerBlockItem(RegisterEvent event, Pair<Identifier, BlockItem> item) {
event.register(Registries.ITEM, helper -> helper.register(item.getFirst(), item.getSecond()));
}

public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
.create(Registries.CREATIVE_MODE_TAB, HutosLib.MOD_ID);
public static final DeferredHolder<CreativeModeTab, CreativeModeTab> hutoslibtab = CREATIVETABS.register("hutoslibtab",
() -> CreativeModeTab.builder().title(Component.translatable("item_group." + MOD_ID + ".hutoslibtab"))
.icon(() -> new ItemStack(HLItemInit.obsidian_flakes.get())).build());

public HutosLib(IEventBus modEventBus) {
if (FMLEnvironment.getDist().isClient()) {
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

public static Identifier rloc(String path) {
return Identifier.fromNamespaceAndPath(MOD_ID, path);
}
}
