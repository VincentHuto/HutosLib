package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeEvents;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.CommonDiscoverySource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

/**
 * Server-side event listener that unlocks the HutosLib guide's test locked
 * entries when the player picks up a diamond.
 *
 * <p>This class exists solely to facilitate testing the entry-unlock /
 * {@link com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter}
 * system without requiring an external mod (e.g. Hemomancy) to be present.
 * Three guide pages (locked_test/pages/page1–3) are gated behind the entry
 * {@code hutoslib:guide/locked_test}; picking up any diamond unlocks them all.
 */
@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class GuideBookUnlockEvents {

    /**
     * The single entry ID that guards the three locked test pages in the
     * HutosLib guide's {@code locked_test} chapter.
     */
    public static final ResourceLocation LOCKED_TEST_ENTRY =
            HutosLib.rloc("guide/locked_test");

    private GuideBookUnlockEvents() {
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!event.getOriginalStack().is(Items.DIAMOND)) {
            return;
        }

        BookKnowledge knowledge = BookKnowledgeProvider.get(serverPlayer);
        if (!knowledge.hasEntry(LOCKED_TEST_ENTRY)) {
            knowledge.unlockEntry(LOCKED_TEST_ENTRY, CommonDiscoverySource.ITEM_PICKUP);
            BookKnowledgeEvents.sync(serverPlayer);
        }
    }
}
