package com.vincenthuto.hutoslib.common.book.knowledge;

import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.world.entity.player.Player;

/**
 * Convenience accessor for the {@link BookKnowledge} player attachment
 * registered as {@link HLAttachmentTypes#BOOK_KNOWLEDGE}.
 *
 * <p>Usage:
 * <pre>{@code
 * BookKnowledge knowledge = BookKnowledgeProvider.get(player);
 * knowledge.unlockEntry(someId, CommonDiscoverySource.ADVANCEMENT);
 * }</pre>
 *
 * <p>Mods that extend {@link BookKnowledge} with their own subclass should
 * register a separate {@code AttachmentType} and provide their own provider
 * rather than calling this class.
 */
public final class BookKnowledgeProvider {

    private BookKnowledgeProvider() {
    }

    /**
     * Returns the {@link BookKnowledge} attachment for {@code player}, creating
     * a fresh default instance if one has not yet been attached (NeoForge
     * attachment behaviour).
     *
     * @param player the player whose knowledge to retrieve; must not be null
     * @return the player's {@link BookKnowledge}; never null
     */
    public static BookKnowledge get(Player player) {
        return player.getData(HLAttachmentTypes.BOOK_KNOWLEDGE.get());
    }
}
