package com.vincenthuto.hutoslib.common.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Opt-in discovery integration. Mods retain ownership of knowledge, costs, and synchronization. */
public final class FieldNotes {
    private FieldNotes() {}
    public interface Provider {
        boolean available(Player player);
        int pending(Player player);
        List<Component> tooltip(Player player);
        boolean accepts(ItemStack book);
        default boolean acceptsDesk(net.minecraft.world.level.block.Block desk) { return true; }
        boolean canDictate(Player player, ItemStack book);
        void dictate(ServerPlayer player, ItemStack book);
    }
    private static final Map<ResourceLocation, Provider> PROVIDERS = new LinkedHashMap<>();
    public static void register(ResourceLocation id, Provider provider) {
        if (PROVIDERS.putIfAbsent(id, provider) != null) throw new IllegalArgumentException("Duplicate field notes provider " + id);
    }
    public static List<Provider> available(Player player) {
        return player == null ? List.of() : PROVIDERS.values().stream().filter(provider -> provider.available(player)).toList();
    }
    public static boolean hasPending(Player player, ItemStack book, net.minecraft.world.level.block.Block desk) {
        return available(player).stream().anyMatch(provider -> provider.acceptsDesk(desk) && provider.accepts(book) && provider.canDictate(player, book));
    }
    public static void dictate(ServerPlayer player, ItemStack book, net.minecraft.world.level.block.Block desk) {
        available(player).stream().filter(provider -> provider.acceptsDesk(desk) && provider.accepts(book) && provider.canDictate(player, book))
                .findFirst().ifPresent(provider -> provider.dictate(player, book));
    }
}
