package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class BookNotificationStateTest {
    private static final ResourceLocation A=ResourceLocation.parse("test:book/chapter/pages/a");
    private static final ResourceLocation B=ResourceLocation.parse("test:book/chapter/pages/b");

    @Test
    void firstSnapshotIsQuietAndExplicitDiscoveriesWinAfterBaseline() {
        var state=new BookNotificationState();
        assertTrue(state.knowledge(Set.of(),Set.of(A),Set.of(A),false).isEmpty());
        assertEquals(Set.of(A),state.knowledge(Set.of(A),Set.of(A,B),Set.of(A),false));
        assertEquals(Set.of(B),state.knowledge(Set.of(A),Set.of(A,B),Set.of(),false));
    }

    @Test
    void forgettingRequiresActualReadabilityLossAndReloadsResetTheBaselineQuietly() {
        var state=new BookNotificationState();
        assertTrue(state.readability(Set.of(A,B),1).isEmpty());
        assertEquals(Set.of(B),state.readability(Set.of(A),1));
        assertTrue(state.readability(Set.of(A),1).isEmpty());
        assertTrue(state.readability(Set.of(),2).isEmpty());
    }
}
