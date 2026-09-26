package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonParser;
import com.vincenthuto.hutoslib.common.book.ReaderLocation;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class BookReaderStateTest {
    private static final ResourceLocation BOOK = ResourceLocation.parse("test:book");
    private static final ResourceLocation ENTRY = ResourceLocation.parse("test:book/intro/pages/entry");

    @Test
    void oldAcknowledgementsMigrateAsListedAndUnknownVersionReads() {
        var state = BookReaderState.fromJson(JsonParser.parseString("[\"" + ENTRY + "\"]"));
        assertTrue(state.isListed(ENTRY));
        assertEquals(BookReaderState.UNKNOWN_VERSION, state.readStamp(ENTRY).version());
        assertEquals(BookReaderState.EntryStatus.READ, state.status(ENTRY, "rewritten", Set.of()));
        state.markRead(ENTRY, "current", Set.of(1));
        assertEquals(BookReaderState.EntryStatus.UPDATED, state.status(ENTRY, "next", Set.of(1)));
    }

    @Test
    void discoveryListingRevealsAndRewritesRemainDistinct() {
        var state = new BookReaderState();
        assertEquals(BookReaderState.EntryStatus.NEW, state.status(ENTRY, "v1", Set.of()));
        state.markListed(ENTRY);
        assertEquals(BookReaderState.EntryStatus.UNREAD, state.status(ENTRY, "v1", Set.of()));
        state.markRead(ENTRY, "v1", Set.of(1));
        assertEquals(BookReaderState.EntryStatus.READ, state.status(ENTRY, "v1", Set.of(1)));
        assertEquals(BookReaderState.EntryStatus.UNREAD, state.status(ENTRY, "v1", Set.of(1, 2)));
        assertEquals(BookReaderState.EntryStatus.UPDATED, state.status(ENTRY, "v2", Set.of(1, 2)));
        state.unacknowledge(Set.of(ENTRY));
        assertEquals(BookReaderState.EntryStatus.NEW, state.status(ENTRY, "v2", Set.of(1, 2)));
    }

    @Test
    void bookmarksLocationsAndFirstViewRevealRecordsSurvivePersistence() {
        var state = new BookReaderState();
        var anchor = new ReaderLocation(BOOK, ENTRY, "body", 95);
        state.resume(anchor);
        state.toggleBookmark(anchor);
        assertTrue(state.markRevealViewed(ENTRY, "v1", 2));
        var reloaded = BookReaderState.fromJson(state.toJson());
        assertEquals(anchor, reloaded.resume(BOOK).orElseThrow());
        assertEquals(anchor, reloaded.bookmarks().get(ENTRY));
        assertFalse(reloaded.markRevealViewed(ENTRY, "v1", 2));
        assertTrue(reloaded.markRevealViewed(ENTRY, "v2", 2));
        reloaded.unacknowledge(Set.of(ENTRY));
        assertEquals(anchor, reloaded.bookmarks().get(ENTRY), "Discovery changes must not remove pins");
    }
}
