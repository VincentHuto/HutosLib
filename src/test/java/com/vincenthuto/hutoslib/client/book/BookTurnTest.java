package com.vincenthuto.hutoslib.client.book;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class BookTurnTest {
    @Test
    void contentSwapsOnceAtHalfwayAndInputStaysLockedUntilTheFinalTick() {
        var turn = new BookTurn();
        var swaps = new AtomicInteger();
        turn.start(12, swaps::incrementAndGet);
        for (int i=0; i<5; i++) turn.tick();
        assertEquals(0, swaps.get());
        turn.tick();
        assertEquals(1, swaps.get());
        assertTrue(turn.active());
        for (int i=0; i<6; i++) turn.tick();
        assertFalse(turn.active());
        assertEquals(1, swaps.get());
    }

    @Test
    void zeroDurationSwapsImmediatelyAndCancellationNeverOpensThePendingPage() {
        var turn = new BookTurn();
        var swaps = new AtomicInteger();
        turn.start(0, swaps::incrementAndGet);
        assertEquals(1, swaps.get());
        assertFalse(turn.active());
        turn.start(12, swaps::incrementAndGet);
        turn.cancel();
        for (int i=0; i<20; i++) turn.tick();
        assertEquals(1, swaps.get());
    }
}
