package com.vincenthuto.hutoslib.client.screen.guide;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

final class BookSearchBox extends EditBox {
    private String lastQuery;

    BookSearchBox(Font font, int x, int y, int width, int height, Component label,
                  String query, Consumer<String> updateQuery) {
        super(font, x, y, width, height, label);
        setMaxLength(256);
        setValue(query);
        lastQuery = getValue();
        setResponder(value -> {
            if (!value.equals(lastQuery)) {
                lastQuery = value;
                updateQuery.accept(value);
            }
        });
    }
}
