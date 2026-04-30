package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.advancements.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Server-side event listeners that automatically unlock book entries
 * registered in {@link BookEntryRegistry} when a player:
 * <ul>
 *   <li>picks up an item ({@link ItemEntityPickupEvent.Post})</li>
 *   <li>earns an advancement ({@link AdvancementEarnEvent})</li>
 *   <li>kills an entity ({@link LivingDeathEvent})</li>
 *   <li>enters a new biome ({@link PlayerTickEvent.Post}, polled every 20 ticks)</li>
 *   <li>enters a registered structure ({@link PlayerTickEvent.Post}, polled every 20 ticks)</li>
 * </ul>
 *
 * <p>Any mod that registers mappings in {@link BookEntryRegistry} during setup
 * will have its entries unlocked automatically — no additional event code is
 * required in the consuming mod.
 */
@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class BookDiscoveryEvents {

    /**
     * Tracks the last-known biome for each online player so we can detect
     * biome transitions without firing on every single tick.
     * Keyed by player UUID; entries are removed on logout.
     */
    private static final Map<UUID, ResourceLocation> LAST_KNOWN_BIOME = new ConcurrentHashMap<>();

    /**
     * Tracks which registered structures each online player is currently
     * standing inside so we only fire the unlock once per entry, not every
     * tick. Keyed by player UUID; entries are removed on logout.
     */
    private static final Map<UUID, Set<ResourceLocation>> CURRENT_STRUCTURES =
            new ConcurrentHashMap<>();

    private BookDiscoveryEvents() {
    }

    // -------------------------------------------------------------------------
    // Item pickup
    // -------------------------------------------------------------------------

    /**
     * Fires when a player picks up an item entity. Looks up the item's
     * registry ID in {@link BookEntryRegistry} and unlocks any registered
     * entries via {@link BookKnowledgeHelper}.
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getOriginalStack().getItem());
        if (itemId == null) {
            return;
        }
        BookKnowledgeHelper.unlockForItemPickup(player, itemId);
    }

    // -------------------------------------------------------------------------
    // Advancement
    // -------------------------------------------------------------------------

    /**
     * Fires when a player earns an advancement. Looks up the advancement ID in
     * {@link BookEntryRegistry} and unlocks any registered entries via
     * {@link BookKnowledgeHelper}.
     */
    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation advancementId = event.getAdvancement().id();
        BookKnowledgeHelper.unlockForAdvancement(player, advancementId);
    }

    // -------------------------------------------------------------------------
    // Entity kill
    // -------------------------------------------------------------------------

    /**
     * Fires when a living entity dies. If the killing blow was delivered by a
     * {@link ServerPlayer}, looks up the entity type's registry ID in
     * {@link BookEntryRegistry} and unlocks any registered entries via
     * {@link BookKnowledgeHelper}.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation entityTypeId =
                BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
        if (entityTypeId == null) {
            return;
        }
        BookKnowledgeHelper.unlockForEntityKill(player, entityTypeId);
    }

    // -------------------------------------------------------------------------
    // Biome entry + structure discovery (polled every 20 ticks)
    // -------------------------------------------------------------------------

    /**
     * Fires on every server-side player tick. Checks biome changes and
     * structure presence every 20 ticks (once per second) to avoid the
     * per-tick overhead of full registry lookups.
     *
     * <p>Biome unlocks trigger once per unique biome transition (last known
     * biome is tracked per player). Structure unlocks trigger once the first
     * time a player stands inside a registered structure (tracked per player
     * session; the underlying {@link BookKnowledge} ensures no duplicate
     * knowledge entries persist across sessions).
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }

        checkBiome(player);
        checkStructures(player);
    }

    // -------------------------------------------------------------------------
    // Cleanup on logout
    // -------------------------------------------------------------------------

    /**
     * Removes per-player tracking data when a player disconnects to prevent
     * memory leaks in long-running server sessions.
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        LAST_KNOWN_BIOME.remove(id);
        CURRENT_STRUCTURES.remove(id);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static void checkBiome(ServerPlayer player) {
        if (BookEntryRegistry.getBiomeUnlocks().isEmpty()) {
            return;
        }
        Optional<ResourceKey<Biome>> biomeKey =
                player.serverLevel().getBiome(player.blockPosition()).unwrapKey();
        biomeKey.ifPresent(key -> {
            ResourceLocation biomeId = key.location();
            ResourceLocation previous = LAST_KNOWN_BIOME.put(player.getUUID(), biomeId);
            if (!biomeId.equals(previous)) {
                BookKnowledgeHelper.unlockForBiome(player, biomeId);
            }
        });
    }

    private static void checkStructures(ServerPlayer player) {
        if (BookEntryRegistry.getStructureUnlocks().isEmpty()) {
            return;
        }
        ServerLevel level = player.serverLevel();
        Registry<Structure> structRegistry =
                level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        Set<ResourceLocation> nowInside = new HashSet<>();

        for (ResourceLocation structureId : BookEntryRegistry.getStructureUnlocks().keySet()) {
            Optional<Holder.Reference<Structure>> holderOpt =
                    structRegistry.get(ResourceKey.create(Registries.STRUCTURE, structureId));
            if (holderOpt.isEmpty()) {
                continue;
            }
            StructureStart start = level.structureManager()
                    .getStructureWithPieceAt(player.blockPosition(), holderOpt.get().value());
            if (start.isValid()) {
                nowInside.add(structureId);
            }
        }

        Set<ResourceLocation> known = CURRENT_STRUCTURES
                .computeIfAbsent(player.getUUID(), id -> new HashSet<>());

        for (ResourceLocation structureId : nowInside) {
            if (known.add(structureId)) {
                // Player just entered this structure for the first time this session;
                // BookKnowledge.unlockEntry is idempotent so no duplicate unlock occurs.
                BookKnowledgeHelper.unlockForStructure(player, structureId);
            }
        }
        // Remove structures the player has left so they can re-enter-and-fire next time.
        known.retainAll(nowInside);
    }
}
