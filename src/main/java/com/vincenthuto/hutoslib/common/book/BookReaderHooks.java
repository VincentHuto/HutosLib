package com.vincenthuto.hutoslib.common.book;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;

/** Shared by filtered models, so changing membership never discards a mod's reader policy. */
public record BookReaderHooks(BiPredicate<Player, Integer> reveal, IntFunction<Component> requirement,
                              Function<Player, Component> owner, Function<Player, Component> status) {
    public static final BookReaderHooks DEFAULT = new BookReaderHooks(null,
            level -> Component.translatable("hutoslib.book.requirement", level), player -> Component.empty(), player -> Component.empty());
}
