package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.BookText;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookPaginationTest {
    private static final BookPaginator.Measurer MONO = run -> run.text().codePointCount(0, run.text().length()) * 6;

    @Test
    void paragraphsHaveFourPixelGapsAndSmallNotesSitAtTheFoot() {
        var body = block("body", "First{n}Second");
        var note = new BookPaginator.Block("footnote/0", BookText.parse("A note"), BookPaginator.Kind.FOOTNOTE, 9, "Note 1", null);
        var leaves = BookPaginator.paginate(List.of(body), List.of(note), false, false, 142, 120, MONO, "continued");
        assertEquals(1, leaves.size());
        var lines = leaves.getFirst().lines();
        assertEquals(13, lines.get(1).y());
        assertEquals(120, lines.getLast().y() + lines.getLast().height());
        assertEquals("A note", content(leaves, "footnote/0"));
    }

    @Test
    void LongNotesContinueWithoutConsumingTheReservedRecordCompanion() {
        var note = new BookPaginator.Block("footnote/0", BookText.parse("note ".repeat(200)), BookPaginator.Kind.FOOTNOTE, 9, "Note 1", null);
        var leaves = BookPaginator.paginate(List.of(block("body", "A body")), List.of(block("margin", "A margin"), note), true, true, 142, 120, MONO, "continued");
        assertEquals("margin", leaves.get(1).lines().getFirst().blockId());
        assertEquals(note.text().plainText(), content(leaves,"footnote/0"));
        assertTrue(leaves.size() > 2);
    }

    @Test
    void longTextAndUnbrokenUnicodeTokensLoseNoSourceCharacters() {
        String source = "A paragraph with spaces. ".repeat(80) + "𝄞".repeat(80);
        var block = new BookPaginator.Block("body", BookText.parse(source), BookPaginator.Kind.BODY, 9, "", null);
        var leaves = BookPaginator.paginate(List.of(block), List.of(), false, false, 142, 120, MONO, "continued");
        assertTrue(leaves.size() > 2);
        String rebuilt = leaves.stream().flatMap(leaf -> leaf.lines().stream()).filter(line -> !line.label())
                .map(BookPaginator.Line::sourceText).reduce("", String::concat);
        assertEquals(source, rebuilt);
        for (var leaf : leaves) for (var line : leaf.lines()) {
            assertTrue(line.y() + line.height() <= 120);
            assertTrue(line.width() <= 142);
        }
    }

    @Test
    void recordCompanionKeepsTheOpeningRightLeafAndBothSidesCanContinue() {
        var body = block("body", "body ".repeat(150));
        var note = block("margin", "note ".repeat(150));
        var leaves = BookPaginator.paginate(List.of(body), List.of(note), true, true, 142, 120, MONO, "continued");
        assertTrue(leaves.size() > 2);
        assertEquals("body", leaves.getFirst().lines().getFirst().blockId());
        assertEquals("margin", leaves.get(1).lines().getFirst().blockId());
        assertEquals(body.text().plainText(), content(leaves, "body"));
        assertEquals(note.text().plainText(), content(leaves, "margin"));
        var single = BookPaginator.paginate(List.of(body), List.of(note), true, false, 146, 120, MONO, "continued");
        assertEquals("body", single.getFirst().lines().getFirst().blockId());
        assertEquals("margin", single.getLast().lines().getLast().blockId());
    }

    @Test
    void reflowRemapsTheSameCodePointAnchorAndClampsChangedContent() {
        var body = block("body", "word ".repeat(200));
        var wide = BookPaginator.paginate(List.of(body), List.of(), false, false, 142, 120, MONO, "continued");
        var narrow = BookPaginator.paginate(List.of(body), List.of(), false, false, 80, 120, MONO, "continued");
        int wideLeaf = BookPaginator.locate(wide, "body", 450);
        int narrowLeaf = BookPaginator.locate(narrow, "body", 450);
        assertTrue(narrowLeaf > wideLeaf);
        assertEquals(narrow.size() - 1, BookPaginator.locate(narrow, "body", 10000));
        assertEquals(0, BookPaginator.locate(narrow, "missing", 450));
    }

    @Test
    void spreadFitIncludesTabsAndFrameAndSmallScreensKeepNativeUnits() {
        var theme = BookThemeResolver.resolve(java.util.Map.of(), ignored -> true, ignored -> {}).get(BookThemeResolver.DEFAULT_ID);
        var fits = BookGeometry.fit(410, 270, theme.layout(), 9);
        assertTrue(fits.spread());
        var noFit = BookGeometry.fit(360, 240, theme.layout(), 9);
        assertFalse(noFit.spread());
        var compact = BookGeometry.fit(160, 180, theme.layout(), 9);
        assertTrue(compact.compact());
        assertEquals(174, compact.leafWidth());
    }

    private static BookPaginator.Block block(String id, String text) {
        return new BookPaginator.Block(id, BookText.parse(text), BookPaginator.Kind.BODY, 9, "", null);
    }

    @Test
    void recordCompanionContinuationsReserveTheirRepeatedHeader() {
        var margin=new BookPaginator.Block("margin",BookText.parse("long margin ".repeat(100)),BookPaginator.Kind.MARGIN,9,"",null);
        var note=new BookPaginator.Block("footnote/0",BookText.parse("long note ".repeat(100)),BookPaginator.Kind.FOOTNOTE,9,"Note 1",null);
        var leaves=BookPaginator.paginate(List.of(block("body","short")),List.of(margin,note),true,true,142,100,180,MONO,"continued");
        for(int i=2;i<leaves.size();i++) for(var line:leaves.get(i).lines())assertTrue(line.y()+line.height()<=100,"Continuation must fit below its header");
        assertEquals(margin.text().plainText(),content(leaves,"margin"));
        assertEquals(note.text().plainText(),content(leaves,"footnote/0"));
    }
    private static String content(List<BookPaginator.Leaf> leaves, String id) {
        return leaves.stream().flatMap(leaf -> leaf.lines().stream()).filter(line -> line.blockId().equals(id) && !line.label())
                .map(BookPaginator.Line::sourceText).reduce("", String::concat);
    }
}
