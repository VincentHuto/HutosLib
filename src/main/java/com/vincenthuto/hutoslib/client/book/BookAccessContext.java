package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import java.util.Optional;
import java.util.function.Supplier;

/** Keeps every reader route tied to the same item policy and current knowledge provider. */
public final class BookAccessContext {
    private final ResourceLocation bookId;
    private final ItemGuideBook item;
    private final BookCodeModel legacyModel;
    private final Supplier<BookCodeModel> legacyRefresher;
    private final IBookKnowledge legacyKnowledge;
    private net.minecraft.core.BlockPos lecternPos;

    public BookAccessContext atLectern(net.minecraft.core.BlockPos pos) {
        lecternPos = pos.immutable();
        return this;
    }

    public BookAccessContext(BookCodeModel book, ItemGuideBook item, Supplier<BookCodeModel> refresher, IBookKnowledge knowledge) {
        bookId = book.getResourceLocation();
        this.item = item;
        legacyModel = book;
        legacyRefresher = refresher;
        legacyKnowledge = knowledge;
    }

    public ResourceLocation bookId() { return bookId; }
    public ItemGuideBook item() { return item; }

    public Optional<BookCodeModel> current(Player player) {
        if (player == null) return Optional.empty();
        BookCodeModel source = BookPlaceboReloadListener.INSTANCE.getBookByTitle(bookId);
        if (source == null) return Optional.empty();
        if (item != null) {
            if (lecternPos != null) {
                if (player.distanceToSqr(lecternPos.getCenter()) > 64
                        || !(player.level().getBlockEntity(lecternPos) instanceof com.vincenthuto.hutoslib.common.block.entity.DictationTableBlockEntity table)
                        || !table.getBook().is(item)) return Optional.empty();
            } else if (!carries(player, item)) return Optional.empty();
            return Optional.ofNullable(item.applyVisibilityFilters(source, player));
        }
        if (legacyRefresher != null) return Optional.ofNullable(legacyRefresher.get());
        // Older programmatic openers need no inventory item, but retain their supplied policy.
        BookCodeModel configured = source.copyWithChapters(source.getChapters());
        configured.setPageFilter(legacyModel.getPageFilter());
        configured.setTheme(legacyModel.getTheme());
        configured.setRedactionPredicate(legacyModel.getReaderHooks().reveal());
        configured.setRevealRequirementLabel(legacyModel.getReaderHooks().requirement());
        configured.setOwnerLine(legacyModel.getReaderHooks().owner());
        configured.setStatusLine(legacyModel.getReaderHooks().status());
        BookCodeModel filtered = configured.getPageFilter().filter(configured, player);
        {
            // A caller can further restrict an already configured model. Reapplying its callback
            // must not restore entries it removed; dynamic legacy callers supply a refresher.
            java.util.Set<ResourceLocation> supplied = new java.util.HashSet<>();
            legacyModel.getChapters().forEach(chapter -> chapter.getPages().forEach(page -> supplied.add(page.getId())));
            filtered = filtered.copyWithChapters(filtered.getChapters().stream()
                    .map(chapter -> chapter.copyWithPages(chapter.getPages().stream().filter(page -> supplied.contains(page.getId())).toList()))
                    .filter(chapter -> !chapter.getPages().isEmpty()).toList());
        }
        return Optional.ofNullable(legacyKnowledge == null ? EntryGatedBookFilter.INSTANCE.filter(filtered, player)
                : EntryGatedBookFilter.INSTANCE.filter(filtered, legacyKnowledge));
    }

    public static Optional<ItemGuideBook> carriedItem(Player player, ResourceLocation bookId) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).getItem() instanceof ItemGuideBook item
                    && item.resolveBookId().filter(bookId::equals).isPresent()) return Optional.of(item);
        }
        return Optional.empty();
    }

    private static boolean carries(Player player, ItemGuideBook item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
            if (player.getInventory().getItem(slot).is(item)) return true;
        return false;
    }
}
