package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.book.BookReadTracker;
import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ItemGuideBook extends Item {
    private static final Random random = new Random();

    /**
     * Client-side animation state keyed by entity UUID.
     *
     * <p>Items are singletons shared across all players, so animation state must
     * <em>not</em> be stored as instance fields. One {@link BookAnimState} entry is
     * created on demand for each entity that holds this book, keeping their
     * animation independent.
     *
     * <p>Only populated on the logical client (guarded by
     * {@link Level#isClientSide} in {@link #inventoryTick}).
     *
     * <p>Entries are removed via {@link #clearState(UUID)} when a player
     * disconnects to prevent unbounded growth.
     */
    private static final Map<UUID, BookAnimState> ANIM_STATES = new ConcurrentHashMap<>();

    /**
     * Fallback state used when no local player is available (e.g. GUI preview).
     * Never mutated — represents a default closed-book pose.
     */
    private static final BookAnimState DEFAULT_STATE = new BookAnimState();

    private ResourceLocation texture;

    /**
     * Entry-path prefix shared by all entries belonging to this book, e.g.
     * {@code "sanctumsanguinium/"}. When non-null, HutosLib automatically
     * registers an item-stack decoration that shows a gold dot whenever the
     * player has at least one unread entry for this book.
     *
     * <p>Set via {@link #withBookPrefix(String)}.
     */
    private String bookPrefix;

    /**
     * Function that retrieves the player's {@link IBookKnowledge} for this book.
     * Defaults to HutosLib's own {@code BOOK_KNOWLEDGE} attachment via
     * {@link BookKnowledgeProvider}. Mods that store knowledge in a mod-specific
     * attachment should override this via {@link #withKnowledgeProvider}.
     *
     * <p>Set via {@link #withKnowledgeProvider(Function)}.
     */
    private Function<Player, Optional<IBookKnowledge>> knowledgeProvider =
            player -> Optional.of(BookKnowledgeProvider.get(player));

    public ItemGuideBook(Properties prop, ResourceLocation loc) {
        super(prop);
        this.texture = loc;
    }

    @OnlyIn(Dist.CLIENT)
    private static void appendUnreadLine(List<Component> tooltip) {


    }

    /**
     * Returns the animation state for the given entity UUID, creating a default
     * state if one does not yet exist. Returns {@link #DEFAULT_STATE} when
     * {@code entityUuid} is {@code null}.
     */
    public static BookAnimState getOrCreateState(UUID entityUuid) {
        if (entityUuid == null) {
            return DEFAULT_STATE;
        }
        return ANIM_STATES.computeIfAbsent(entityUuid, id -> new BookAnimState());
    }

    /**
     * Removes the cached animation state for the given entity UUID.
     * Call this when a player disconnects to prevent unbounded map growth.
     */
    public static void clearState(UUID entityUuid) {
        ANIM_STATES.remove(entityUuid);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        String prefix = this.getBookPrefix();
        if (prefix == null || prefix.isEmpty()) return;

        IBookKnowledge knowledge = this.getKnowledgeProvider().apply(mc.player).orElse(null);
        int unread = countUnreadForDisplay(mc.player, prefix, knowledge);
        if (unread > 0) {
            tooltip.add(Component.literal(ChatFormatting.GOLD + "⬤ " + unread
                    + " unread entr" + (unread == 1 ? "y" : "ies")));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static int countUnreadForDisplay(Player player, String prefix, IBookKnowledge knowledge) {
        Set<ResourceLocation> visiblePageIds = collectVisiblePageIds(player, prefix);
        if (!visiblePageIds.isEmpty()) {
            return BookReadTracker.countUnread(player.getUUID(), visiblePageIds);
        }
        return knowledge != null ? BookReadTracker.countUnread(player.getUUID(), knowledge, prefix) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    private static Set<ResourceLocation> collectVisiblePageIds(Player player, String prefix) {
        Set<ResourceLocation> ids = new HashSet<>();
        for (BookCodeModel loadedBook : BookPlaceboReloadListener.INSTANCE.getBooks()) {
            if (!loadedBook.getEntryPrefix().equals(prefix)) {
                continue;
            }
            // Match the same visibility rules used when opening the guide from the item.
            BookCodeModel filtered = loadedBook.getPageFilter().filter(loadedBook, player);
            filtered = EntryGatedBookFilter.INSTANCE.filter(filtered, player);
            if (filtered.getChapters() == null) {
                continue;
            }
            for (var chapter : filtered.getChapters()) {
                if (chapter.getPages() == null) {
                    continue;
                }
                for (var page : chapter.getPages()) {
                    if (page.getId() != null) {
                        ids.add(page.getId());
                    }
                }
            }
        }
        return ids;
    }

    /**
     * Sets the entry-path prefix used to scope unread-entry detection to this
     * book (e.g. {@code "sanctumsanguinium/"}). When non-null, HutosLib will
     * automatically register an item-stack decoration (gold dot) whenever the
     * player has at least one unread entry for this book.
     *
     * @param prefix path prefix shared by all entries belonging to this book
     * @return {@code this} for chaining
     */
    public ItemGuideBook withBookPrefix(String prefix) {
        this.bookPrefix = prefix;
        return this;
    }

    /**
     * Sets the function used to retrieve a player's {@link IBookKnowledge} for
     * this book. Defaults to HutosLib's own {@code BOOK_KNOWLEDGE} attachment via
     * {@link BookKnowledgeProvider}. Mods that store knowledge in a mod-specific
     * attachment should supply their own accessor here.
     *
     * @param provider function that returns knowledge for a given player
     * @return {@code this} for chaining
     */
    public ItemGuideBook withKnowledgeProvider(Function<Player, Optional<IBookKnowledge>> provider) {
        this.knowledgeProvider = provider;
        return this;
    }

    /**
     * Returns the entry-path prefix for this book, or {@code null} if not set.
     */
    public String getBookPrefix() {
        return bookPrefix;
    }

    /**
     * Returns the knowledge provider function for this book. If none was
     * explicitly set via {@link #withKnowledgeProvider}, falls back to
     * HutosLib's own {@code BOOK_KNOWLEDGE} attachment.
     */
    public Function<Player, Optional<IBookKnowledge>> getKnowledgeProvider() {
        return knowledgeProvider;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        // Animation is purely cosmetic — only update on the client.
        if (!worldIn.isClientSide) {
            return;
        }

        if (entityIn instanceof Player player) {
            BookAnimState s = getOrCreateState(player.getUUID());

            ItemStack oheld = player.getOffhandItem();
            boolean offHand = stack.getItem() == oheld.getItem();

            ItemStack mheld = player.getMainHandItem();
            boolean mainHand = stack.getItem() == mheld.getItem();

            if (mainHand || offHand) {
                s.pageTurningSpeed = s.nextPageTurningSpeed;
                s.pageAngle = s.nextPageAngle;
                s.nextPageTurningSpeed += 0.1F;
                if (s.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
                    float f1 = s.flipT;
                    do {
                        s.flipT += random.nextInt(4) - random.nextInt(4);
                    } while (f1 == s.flipT);
                }
                while (s.nextPageAngle >= (float) Math.PI) {
                    s.nextPageAngle -= ((float) Math.PI * 2F);
                }
                while (s.nextPageAngle < -(float) Math.PI) {
                    s.nextPageAngle += ((float) Math.PI * 2F);
                }
                while (s.tRot >= (float) Math.PI) {
                    s.tRot -= ((float) Math.PI * 2F);
                }
                while (s.tRot < -(float) Math.PI) {
                    s.tRot += ((float) Math.PI * 2F);
                }
                float f2;
                for (f2 = s.tRot - s.nextPageAngle; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
                }
                while (f2 < -(float) Math.PI) {
                    f2 += ((float) Math.PI * 2F);
                }
                s.nextPageAngle += f2 * 0.4F;
                s.nextPageTurningSpeed = Mth.clamp(s.nextPageTurningSpeed, 0.0F, 1.0F);
                s.oFlip = s.flip;
                float f = (s.flipT - s.flip) * 0.4F;
                f = Mth.clamp(f, -0.2F, 0.2F);
                s.flipA += (f - s.flipA) * 0.9F;
                s.flip += s.flipA;
                if (s.close < 1f) {
                    s.close += 0.015f;
                }
            } else {
                if (s.close > 0f) {
                    s.close -= 0.015f;
                }
            }
        }
    }
}
