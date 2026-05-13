package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.common.banner.BannerFinderBannerSlot;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.book.knowledge.BookEntryRegistry;
import com.vincenthuto.hutoslib.common.container.HlContainerInit;
import com.vincenthuto.hutoslib.common.data.HLDataGeneration;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.skilltree.SkillTreePlaceboReloadListener;
import com.vincenthuto.hutoslib.common.event.GuideBookUnlockEvents;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hutoslib")
public class HutosLib {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "hutoslib";
    public static final DeferredRegister<CreativeModeTab> CREATIVETABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, HutosLib.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> hutoslibtab = CREATIVETABS.register("hutoslibtab",
            () -> CreativeModeTab.builder().title(Component.translatable("item_group." + MOD_ID + ".hutoslibtab"))
                    .icon(() -> new ItemStack(HLItemInit.obsidian_flakes.get())).build());
    public static IProxy proxy = new IProxy() {
    };

    public HutosLib(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::buildContents);
        modEventBus.addListener(HLPacketHandler::registerPayloads);
        modEventBus.addListener(HLDataGeneration::gatherClientData);
        modEventBus.addListener(HLDataGeneration::gatherServerData);
        HLItemInit.ITEMS.register(modEventBus);
        HLItemInit.HANDHELDITEMS.register(modEventBus);
        HLItemInit.SPECIALITEMS.register(modEventBus);
        HLItemInit.BANNERPATTERNS.register(modEventBus);
        HLBlockInit.BLOCKS.register(modEventBus);
        HLBlockInit.MODELEDBLOCKS.register(modEventBus);
        CREATIVETABS.register(modEventBus);
        HLBlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        HlContainerInit.CONTAINERS.register(modEventBus);
        HlContainerInit.RECIPESERIALIZERS.register(modEventBus);
        HLParticleInit.PARTICLE_TYPES.register(modEventBus);
        HLAttachmentTypes.register(modEventBus);
    }

    public static Identifier rloc(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BannerFinderBannerSlot.initFinder();
        BookPlaceboReloadListener.INSTANCE.registerToBus();
        SkillTreePlaceboReloadListener.INSTANCE.registerToBus();
        // Register test unlock: picking up a diamond unlocks the guide's locked_test chapter
        BookEntryRegistry.registerItemUnlock(
                BuiltInRegistries.ITEM.getKey(Items.DRAGON_EGG),
                GuideBookUnlockEvents.LOCKED_TEST_ENTRY);
    }

    public void buildContents(BuildCreativeModeTabContentsEvent output) {
        if (output.getTabKey() == hutoslibtab.getKey()) {
            HLItemInit.ITEMS.getEntries().forEach(i -> output.accept(i.get()));
            HLItemInit.HANDHELDITEMS.getEntries().forEach(i -> output.accept(i.get()));
            HLItemInit.SPECIALITEMS.getEntries().forEach(i -> output.accept(i.get()));
        }
    }
}
