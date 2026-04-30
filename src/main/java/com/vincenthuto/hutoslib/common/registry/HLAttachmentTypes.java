package com.vincenthuto.hutoslib.common.registry;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.karma.Karma;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class HLAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HutosLib.MOD_ID);

    public static final Supplier<AttachmentType<Karma>> KARMA =
            ATTACHMENT_TYPES.register("karma", () -> AttachmentType.serializable(Karma::new).build());

    public static final Supplier<AttachmentType<BannerExtensionSlot>> BANNER_SLOT =
            ATTACHMENT_TYPES.register("banner_slot", () -> AttachmentType.serializable(() -> new BannerExtensionSlot(null)).build());

    public static final Supplier<AttachmentType<BookKnowledge>> BOOK_KNOWLEDGE =
            ATTACHMENT_TYPES.register("book_knowledge", () -> AttachmentType.serializable(BookKnowledge::new).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
