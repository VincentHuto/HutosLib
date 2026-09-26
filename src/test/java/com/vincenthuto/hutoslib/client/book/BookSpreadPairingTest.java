package com.vincenthuto.hutoslib.client.book;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class BookSpreadPairingTest {
    @Test void eitherEntryOpensTheSamePairAndNextPairDoesNotOverlap() {
        var order=List.of(5,6,7,8,9);
        assertEquals(List.of(5,6),BookSpreadPairing.pair(order,5,id->true));
        assertEquals(List.of(5,6),BookSpreadPairing.pair(order,6,id->true));
        assertEquals(List.of(7,8),BookSpreadPairing.pair(order,7,id->true));
        assertTrue(BookSpreadPairing.pair(order,9,id->true).isEmpty());
    }
    @Test void HiddenAndMultiLeafEntriesBreakTheRun() {
        var order=List.of(1,2,3,4,5,6);
        var excluded=Set.of(2,5);
        assertTrue(BookSpreadPairing.pair(order,1,id->!excluded.contains(id)).isEmpty());
        assertEquals(List.of(3,4),BookSpreadPairing.pair(order,4,id->!excluded.contains(id)));
        assertTrue(BookSpreadPairing.pair(order,5,id->!excluded.contains(id)).isEmpty());
        assertTrue(BookSpreadPairing.pair(order,6,id->!excluded.contains(id)).isEmpty());
    }
}
