package com.vincenthuto.hutoslib.client.book;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookGeometryTest {
    private final BookVisualTheme.Layout layout = new BookVisualTheme.Layout(
            "fore_edge", "auto", 174, 228, "none", 0, true, "hide");

    @Test
    void shortWideWindowsKeepTwoBoundedLeavesAndRoomForTabs() {
        var geometry = BookGeometry.fit(428, 240, layout, 4);
        assertTrue(geometry.spread());
        assertFalse(geometry.compact());
        assertEquals(348, geometry.outerWidth());
        assertEquals(170, geometry.leafWidth());
        assertEquals(142, geometry.textWidth());
        assertTrue(geometry.left() >= 3);
        assertTrue(geometry.left() + geometry.outerWidth() + 24 < 428);
        assertTrue(geometry.top() >= 3);
        assertTrue(geometry.top() + geometry.outerHeight() + 4 <= 240);
        assertEquals(geometry.outerHeight() - 8, geometry.leafHeight());
    }

    @Test
    void shortWindowsNeverExpandOneLeafAcrossTheScreen() {
        for (int width : new int[]{360, 900}) {
            var geometry = BookGeometry.fit(width, 180, layout, 4);
            assertFalse(geometry.spread());
            assertTrue(geometry.compact());
            assertEquals(174, geometry.viewportWidth());
            assertEquals((width - 174) / 2, geometry.left());
            assertTrue(geometry.leafHeight() <= geometry.viewportHeight());
        }
    }

    @Test
    void narrowWindowsKeepAllContentInsideTheViewport() {
        var geometry = BookGeometry.fit(160, 180, layout, 4);
        assertTrue(geometry.compact());
        assertEquals(152, geometry.viewportWidth());
        assertEquals(124, geometry.textWidth());
        assertEquals(4, geometry.left());
        assertTrue(geometry.top() + geometry.viewportHeight() <= 180);
    }
}
