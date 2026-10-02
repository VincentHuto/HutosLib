package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.BookText;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.client.resources.language.I18n;
import java.util.ArrayList;
import java.util.List;

/** Shared measurements for displayed leaves and the unfiltered page-number index. */
public final class BookEntryLayout {
    public static final int SUBTITLE_RISE = 6;
    private BookEntryLayout() {}
    public static int bodyOffset(BookEntryContent content, BookCanvas canvas, BookGeometry geometry) {
        return 13+16+Math.max(0,canvas.headingLineHeight()-16)
                +canvas.headingLineHeight()
                +(content.subtitle().isBlank()?0:11-SUBTITLE_RISE)+9;
    }
    public static int bodyHeight(int offset, BookGeometry geometry) {
        return Math.max(18,geometry.leafHeight()-offset-(geometry.compact()?44:24));
    }
    public static List<BookPaginator.Leaf> paginate(PageTemplate page, BookEntryContent content,
            BookCanvas canvas, BookVisualTheme visual, BookGeometry geometry) {
        int bodyOffset=bodyOffset(content,canvas,geometry);
        int bodyHeight=bodyHeight(bodyOffset,geometry);
        List<BookPaginator.Block> main=new ArrayList<>(),companion=new ArrayList<>();
        content.blocks().forEach((id,text)-> {
            BookPaginator.Kind kind=id.equals("record")?BookPaginator.Kind.RECORD:id.equals("margin")?(visual.font("hand")==null?BookPaginator.Kind.FOOTNOTE:BookPaginator.Kind.MARGIN):id.startsWith("footnote/")?BookPaginator.Kind.FOOTNOTE:BookPaginator.Kind.BODY;
            String label=id.equals("record")?content.recordLabel():id.startsWith("footnote/")?tr("note")+" "+(Integer.parseInt(id.substring(9))+1):kind==BookPaginator.Kind.FOOTNOTE?tr("margin_note"):"";
            var block=new BookPaginator.Block(id,text,kind,9,label,null);
            (kind==BookPaginator.Kind.MARGIN||kind==BookPaginator.Kind.FOOTNOTE?companion:main).add(block);
        });
        for(int i=0;i<page.getPresentation().seeAlso().size();i++) companion.add(new BookPaginator.Block("seeAlso/"+i,BookText.parse(""),BookPaginator.Kind.SEE_ALSO,18,"",page.getPresentation().seeAlso().get(i)));
        int continuationHeight=bodyHeight+bodyOffset-13;
        return BookPaginator.paginate(main,companion,page.getPresentation().layout().equals("record"),geometry.spread(),geometry.textWidth(),bodyHeight,continuationHeight,continuationHeight,
                new BookPaginator.Measurer() {
                    @Override public int width(BookText.Run run) { return canvas.width(run,"body"); }
                    @Override public int width(BookText.Run run,BookPaginator.Kind kind) {
                        return canvas.width(run,kind==BookPaginator.Kind.MARGIN&&visual.font("hand")!=null?"hand":"body");
                    }
                },tr("continued"));
    }
    private static String tr(String key) { return I18n.get("hutoslib.book."+key); }
}
