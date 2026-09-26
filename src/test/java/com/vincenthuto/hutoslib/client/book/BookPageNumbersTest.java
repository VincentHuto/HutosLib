package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.BookText;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookPageNumbersTest {
    private static ResourceLocation id(String path) { return ResourceLocation.parse("test:"+path); }
    private static List<BookPaginator.Leaf> paginate(boolean record, String text) {
        return BookPaginator.paginate(List.of(new BookPaginator.Block("body", BookText.parse(text),
                BookPaginator.Kind.BODY, 9, "", null)), List.of(), record, true, 10, 9,
                run -> run.text().length(), "continued");
    }
    @Test void overflowConsumesNumbersAndHiddenEntriesLeaveTheirFullGap() {
        var leaves=paginate(false,"one\ntwo");
        assertEquals(2,leaves.size());
        var index=new BookPageNumbers();
        index.append(id("first"),leaves.size());
        index.append(id("hidden"),3);
        index.append(id("last"),1);
        assertEquals("1–2",index.range(id("first")).label());
        assertEquals(1,index.range(id("first")).leafNumber(leaves,0));
        assertEquals(2,index.range(id("first")).leafNumber(leaves,1));
        assertEquals("6",index.range(id("last")).label());
        assertEquals("3–5",index.range(id("hidden")).label());
    }
    @Test void blankRecordCompanionDoesNotConsumeAPageNumber() {
        var leaves=paginate(true,"one\ntwo");
        assertTrue(leaves.get(1).blank());
        var index=new BookPageNumbers();
        index.append(id("record"),(int)leaves.stream().filter(leaf->!leaf.blank()).count());
        index.append(id("next"),1);
        assertEquals(2,index.range(id("record")).leafNumber(leaves,2));
        assertEquals("3",index.range(id("next")).label());
    }
}
