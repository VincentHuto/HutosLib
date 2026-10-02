package com.vincenthuto.hutoslib.client.screen.guide;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BookSearchBoxTest {
    @Test
    void selectingAllDoesNotRebuildTheSearchBeforeCopyingOrPasting() {
        var queries = new ArrayList<String>();
        var field = new BookSearchBox(null, 0, 0, 140, 14, Component.empty(), "blood", queries::add);

        // The same EditBox operations used by Minecraft's Ctrl+A handler.
        field.moveCursorToEnd(false);
        field.setHighlightPos(0);
        assertTrue(queries.isEmpty(), "Selection must not trigger a query update and rebuild");
        assertEquals("blood", field.getHighlighted());

        // Ctrl+V replaces the selected query through EditBox.insertText.
        field.insertText("memory");
        assertEquals("memory", field.getValue());
        assertEquals(java.util.List.of("memory"), queries);
        assertEquals("", field.getHighlighted());
    }

    @Test
    void movingTheCursorDoesNotResetResultsAndEditingStillUpdatesThem() {
        var queries = new ArrayList<String>();
        var field = new BookSearchBox(null, 0, 0, 140, 14, Component.empty(), "blood", queries::add);
        field.moveCursorTo(2, false);
        assertTrue(queries.isEmpty());
        field.insertText("a");
        assertEquals("blaood", field.getValue());
        assertEquals(java.util.List.of("blaood"), queries);
    }
}
