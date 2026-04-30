package com.vincenthuto.hutoslib.common.event;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;

/**
 * Holds the entry-ID constant used by the HutosLib guide's test locked pages.
 *
 * <p>Three guide pages ({@code locked_test/pages/page1–3}) are gated behind
 * the entry {@code hutoslib:guide/locked_test}. The mapping that unlocks them
 * when the player picks up a diamond is registered in
 * {@link com.vincenthuto.hutoslib.common.book.knowledge.BookEntryRegistry}
 * during {@code FMLCommonSetupEvent}; the unlock itself is handled generically
 * by {@link com.vincenthuto.hutoslib.common.book.knowledge.BookDiscoveryEvents}.
 */
public final class GuideBookUnlockEvents {

    /**
     * The single entry ID that guards the three locked test pages in the
     * HutosLib guide's {@code locked_test} chapter.
     */
    public static final ResourceLocation LOCKED_TEST_ENTRY =
            HutosLib.rloc("guide/locked_test");

    private GuideBookUnlockEvents() {
    }
}
