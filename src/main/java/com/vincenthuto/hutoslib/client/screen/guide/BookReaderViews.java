package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.book.*;
import com.vincenthuto.hutoslib.common.book.BookText;
import com.vincenthuto.hutoslib.common.data.book.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.*;
import java.util.function.Consumer;
import static com.vincenthuto.hutoslib.client.screen.guide.BookReaderScreen.tr;

/** Composes the non-entry views from the same access-controlled session. */
final class BookReaderViews {
    private final BookReaderScreen screen;
    private final List<Consumer<GuiGraphics>> paints = new ArrayList<>();
    private int pageCount=1;
    private boolean filtersExpanded;
    private int filterChapterOffset;
    private ResourceLocation previewClick;
    private long previewClickAt;

    BookReaderViews(BookReaderScreen screen) {this.screen=screen;}
    private BookReaderSession session(){return screen.session;}
    private BookCanvas canvas(){return screen.canvas;}
    private BookVisualTheme theme(){return screen.visual;}
    private int textWidth(){return screen.geometry.textWidth();}

    void build() {
        paints.clear(); pageCount=1;
        switch(session().view().view()) {
            case COVER -> cover();
            case CONTENTS -> contents(0);
            case CHAPTER -> chapter();
            case SEARCH -> search();
            case GLOSSARY -> glossary();
            case BOOKMARKS -> bookmarks();
            case PREVIEW -> previewView();
            case ENTRY -> { }
        }
        if(pageCount>1) pageControls();
    }

    void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {paints.forEach(paint->paint.accept(graphics));}

    private void heading(String title,int side) {
        var leaf=screen.leaf(side);
        paints.add(graphics-> {
            String face=canvas().headingFace();
            canvas().text(graphics,canvas().ellipsis(title,face,textWidth()),face,"accent",
                    leaf.x()+14,leaf.y()+15+Math.max(0,canvas().headingLineHeight()-16));
            if(theme().layout().rule()) graphics.fill(leaf.x()+14,leaf.y()+37,leaf.x()+14+textWidth(),leaf.y()+38,theme().color("accent"));
        });
    }

    private void cover() {
        var leaf=screen.leaf(0);
        var book=session().book();
        int contraction=Math.max(0,224-leaf.height());
        int iconSize=contraction>0?32:48;
        int iconY=contraction>0?Math.max(66,78-contraction/2):86;
        int epigraphY=Math.max(iconY+iconSize+8,142-contraction);
        String title=I18n.get(book.getTemplate().getTitle());
        paints.add(graphics-> {
            String face=theme().font("title24")!=null?"title24":"title";
            if(theme().font("title28")!=null&&canvas().width(title,"title28")<=textWidth()) face="title28";
            int lineHeight=face.equals("title28")?28:24;
            int y=leaf.y()+19+lineHeight-24;
            for(String line:canvas().heading(title,face,textWidth())) {canvas().centered(graphics,line,face,"accent",leaf.x()+leaf.width()/2,y);y+=lineHeight;}
            canvas().centered(graphics,canvas().ellipsis(I18n.get(book.getTemplate().getSubtitle()),"label",textWidth()),"label","inkMuted",leaf.x()+leaf.width()/2,y+5);
            graphics.pose().pushPose();
            graphics.pose().translate(leaf.x()+leaf.width()/2-iconSize/2,leaf.y()+iconY,0);
            graphics.pose().scale(iconSize/16f,iconSize/16f,1);
            graphics.renderFakeItem(book.getTemplate().getIconItem(),0,0);
            graphics.pose().popPose();
            canvas().centered(graphics,canvas().ellipsis(book.getReaderHooks().owner().apply(session().player()).getString(),"label",textWidth()),"label","inkMuted",leaf.x()+leaf.width()/2,leaf.y()+leaf.height()-54);
            canvas().centered(graphics,canvas().ellipsis(book.getReaderHooks().status().apply(session().player()).getString(),"label",textWidth()),"label","inkMuted",leaf.x()+leaf.width()/2,leaf.y()+leaf.height()-43);
        });
        rich("epigraph",book.getTemplate().getText(),leaf.x()+14,leaf.y()+epigraphY,textWidth(),Math.max(9,leaf.height()-epigraphY-58),session().view().listPage());
        int buttonsY=leaf.y()+leaf.height()-29;
        screen.textButton(leaf.x()+14,buttonsY,textWidth()/2-2,tr("resume"),session()::resume);
        screen.textButton(leaf.x()+16+textWidth()/2,buttonsY,textWidth()/2-2,tr("contents"),()->session().show(BookReaderSession.View.CONTENTS,null));
        if(screen.geometry.spread()) contents(1);
    }

    private void contents(int side) {
        heading(tr("contents"),side);
        var leaf=screen.leaf(side);
        int capacity=Math.max(1,(leaf.height()-86)/16);
        List<ChapterTemplate> chapters=session().book().getChapters();
        int contentsPages=Math.max(1,(chapters.size()+capacity-1)/capacity);
        pageCount=Math.max(pageCount,contentsPages);
        int from=Math.min(session().view().listPage(),contentsPages-1)*capacity;
        for(int i=from;i<Math.min(chapters.size(),from+capacity);i++) {
            var chapter=chapters.get(i); int y=leaf.y()+45+(i-from)*16;
            int number=session().book().getSourceIndex().chapters().getOrDefault(chapter.getId(),i+1);
            screen.button(leaf.x()+14,y,textWidth(),16,Component.literal(I18n.get(chapter.getTitle())),()->session().show(BookReaderSession.View.CHAPTER,chapter.getId()),(graphics,b,mx,my)-> {
                canvas().text(graphics,BookCanvas.roman(number).toUpperCase(Locale.ROOT),"label","accent",b.getX(),y+3);
                canvas().text(graphics,canvas().ellipsis(I18n.get(chapter.getTitle()),"body",textWidth()-49),"body","ink",b.getX()+17,y+3);
                long read=chapter.getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast).filter(page->session().content(page).readable(session().revealAccess())&&session().status(page)==BookReaderState.EntryStatus.READ).count();
                String count=read+"/"+chapter.getPages().size();
                canvas().text(graphics,count,"label","inkMuted",b.getX()+textWidth()-canvas().width(count,"label"),y+3);
                if(b.isHoveredOrFocused()) screen.tooltip(List.of(Component.literal(I18n.get(chapter.getTitle()))));
            });
        }
        screen.textButton(leaf.x()+14,leaf.y()+leaf.height()-36,textWidth()/2-2,tr("glossary"),()->session().show(BookReaderSession.View.GLOSSARY,null));
        screen.textButton(leaf.x()+16+textWidth()/2,leaf.y()+leaf.height()-36,textWidth()/2-2,tr("bookmarks"),()->session().show(BookReaderSession.View.BOOKMARKS,null));
    }

    private void chapter() {
        var chapter=session().chapter(session().view().chapterId()).orElse(null);
        if(chapter==null)return;
        BookLayout.Kind kind=LayoutArchetypes.resolve(chapter.getPresentation().layout(),BookLayout.Kind.CHAPTER).kind();
        if(kind==BookLayout.Kind.SLOTS){slots(chapter);return;}
        heading(I18n.get(chapter.getTitle()),0);
        var leaf=screen.leaf(0);
        List<PageTemplate> pages=chapter.getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast).toList();
        int y=leaf.y()+46;
        var intro=chapterFlow(chapter,Math.max(18,leaf.height()-72));
        int introHeight=intro.getFirst().lines().stream().mapToInt(line->line.y()+line.height()).max().orElse(0);
        boolean longIntro=introHeight>45||intro.size()>1;
        int introPages=longIntro?intro.size():0;
        if(introHeight>0&&!longIntro) {paintFlow(intro.getFirst(),leaf.x()+14,y);y+=introHeight+4;}
        int cap=Math.max(1,(leaf.y()+leaf.height()-24-y)/18);
        int authored=chapter.getPresentation().rowLimit();
        cap=Math.min(cap,authored>0?authored:7);
        boolean preview=kind==BookLayout.Kind.PREVIEW;
        int columns=screen.geometry.spread()&&!preview?2:1;
        int capacity=cap*columns;
        pageCount=introPages+Math.max(1,(pages.size()+capacity-1)/capacity);
        if(session().view().listPage()<introPages) {paintFlow(intro.get(session().view().listPage()),leaf.x()+14,leaf.y()+46);return;}
        int from=Math.min(Math.max(0,session().view().listPage()-introPages),pageCount-introPages-1)*capacity;
        for(int i=from;i<Math.min(pages.size(),from+capacity);i++) {
            int side=(i-from)/cap;var target=screen.leaf(side);
            int rowY=(side==0?y:target.y()+46)+((i-from)%cap)*18;
            PageTemplate page=pages.get(i);
            Runnable action=preview?()->selectPreview(page,chapter):()->session().open(page.getId());
            row(page,target.x()+14,rowY,textWidth(),action);
        }
        if(preview&&screen.geometry.spread()) {
            ResourceLocation selected=session().view().selection();
            if(selected==null&&!pages.isEmpty()) selected=pages.getFirst().getId();
            preview(selected,1);
        }
    }

    private void selectPreview(PageTemplate page,ChapterTemplate chapter) {
        long now=net.minecraft.Util.getMillis();
        if(page.getId().equals(previewClick)&&now-previewClickAt<300) {session().open(page.getId());previewClick=null;return;}
        previewClick=page.getId();previewClickAt=now;
        session().select(page.getId());
        if(!screen.geometry.spread()){session().show(BookReaderSession.View.PREVIEW,chapter.getId());session().select(page.getId());}
    }

    private void row(PageTemplate page,int x,int y,int width,Runnable action) {
        row(page,x,y,width,action,false);
    }

    private void row(PageTemplate page,int x,int y,int width,Runnable action,boolean washedMatch) {
        BookEntryContent content=session().content(page);
        boolean readable=content.readable(session().revealAccess());
        BookReaderState.EntryStatus badge=screen.initialBadges.computeIfAbsent(page.getId(),ignored->session().status(page));
        String title=readable?content.title():session().book().getReaderHooks().requirement().apply(page.getPresentation().revealLevel()).getString();
        screen.button(x,y,width,18,Component.literal(title),action,(graphics,b,mx,my)-> {
            if(!readable) {concealed(graphics,x,y,page.getPresentation().revealLevel());return;}
            session().saved().markListed(page.getId());
            int state=badge.ordinal();
            canvas().sprite(graphics,BookAtlas.Mc,x,y+4,state*8,0,8,8,theme().color(badge==BookReaderState.EntryStatus.UPDATED?"notice":"seal"));
            canvas().sprite(graphics,BookAtlas.Mr,x,y+4,state*8,0,8,8,theme().color("accent"));
            graphics.renderFakeItem(page.getIconItem(),x+10,y+1);
            var currentStatus=session().status(page);
            boolean unread=currentStatus==BookReaderState.EntryStatus.NEW||currentStatus==BookReaderState.EntryStatus.UNREAD;
            int color=theme().entryTitleColor(b.isHoveredOrFocused(),unread,net.minecraft.Util.getMillis());
            String label=screen.pageLabel(session().book(),page.getId());
            int referenceWidth=canvas().width(label,"badge");
            int titleWidth=Math.max(0,width-42-referenceWidth);
            graphics.drawString(screen.bookFont(),canvas().text(canvas().ellipsis(content.title(),"body",titleWidth),"body"),
                    x+30,y+(washedMatch?0:4),color,false);
            if(washedMatch)canvas().text(graphics,canvas().ellipsis(tr("washed_match"),"label",titleWidth),"label","inkMuted",x+30,y+9);
            canvas().text(graphics,label,"badge","inkMuted",x+width-canvas().width(label,"badge"),y+5);
            if(page.getPresentation().pinned()||session().saved().bookmarks().containsKey(page.getId())) graphics.fill(x+width-referenceWidth-7,y+4,x+width-referenceWidth-4,y+11,theme().color("ribbon"));
            if(b.isHoveredOrFocused()) screen.tooltip(washedMatch?List.of(Component.literal(content.title()),Component.literal(tr("washed_match"))):previewTooltip(page.getId()));
        });
    }

    private void slots(ChapterTemplate chapter) {
        heading(I18n.get(chapter.getTitle()),0);
        var left=screen.leaf(0);
        boolean spread=screen.geometry.spread();
        List<PageTemplate> pages=chapter.getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast).toList();
        var intro=chapterFlow(chapter,Math.max(18,left.height()-91));
        var grid=screen.leaf(spread?1:0);
        int rows=Math.max(1,(grid.height()-72)/30),capacity=rows*4;
        int gridPages=Math.max(1,(pages.size()+capacity-1)/capacity);
        pageCount=spread?Math.max(intro.size(),gridPages):intro.size()+gridPages;
        int current=Math.min(session().view().listPage(),pageCount-1);
        if(spread||current<intro.size()) {
            paintFlow(intro.get(Math.min(current,intro.size()-1)),left.x()+14,left.y()+48);
            paints.add(g->{
                int read=(int)pages.stream().filter(page->session().status(page)==BookReaderState.EntryStatus.READ).count();
                for(int i=0;i<Math.min(10,pages.size());i++)canvas().sprite(g,BookAtlas.Pp,left.x()+14+i*9,left.y()+left.height()-33,
                        i<read*Math.min(10,pages.size())/Math.max(1,pages.size())?0:8,0,8,10,theme().color("accent"));
                canvas().text(g,read+"/"+pages.size(),"label","inkMuted",left.x()+107,left.y()+left.height()-33);
            });
        }
        if(!spread&&current<intro.size())return;
        int page=Math.min(gridPages-1,Math.max(0,current-(spread?0:intro.size())));
        int from=page*capacity;
        for(int i=from;i<Math.min(pages.size(),from+capacity);i++) {
            var entry=pages.get(i);int x=grid.x()+14+(i-from)%4*30,y=grid.y()+46+(i-from)/4*30;
            boolean readable=session().content(entry).readable(session().revealAccess());
            screen.button(x,y,26,26,Component.literal(readable?session().content(entry).title():tr("concealed")),()->session().open(entry.getId()),(g,b,mx,my)-> {
                canvas().sprite(g,BookAtlas.S,x,y,b.isHoveredOrFocused()?26:0,0,26,26,0xFFFFFFFF);
                g.renderFakeItem(readable?entry.getIconItem():new ItemStack(Items.BOOK),x+5,y+5);
                if(readable)session().saved().markListed(entry.getId());
                if(b.isHoveredOrFocused())screen.tooltip(previewTooltip(entry.getId()));
            });
        }
    }

    private void previewView(){heading(tr("preview"),0);preview(session().view().selection(),0);}
    private void preview(ResourceLocation id,int side) {
        var leaf=screen.leaf(side);
        var page=session().entry(id).orElse(null);
        if(page==null)return;
        var content=session().content(page);
        boolean readable=content.readable(session().revealAccess());
        paints.add(g->{
            if(!readable){concealed(g,leaf.x()+14,leaf.y()+49,page.getPresentation().revealLevel());return;}
            g.renderFakeItem(page.getIconItem(),leaf.x()+14,leaf.y()+48);
            canvas().text(g,canvas().ellipsis(content.title(),"title",textWidth()-20),"title","accent",leaf.x()+34,leaf.y()+48);
            drawParagraph(g,content.sanitizedText(session().revealAccess()),leaf.x()+14,leaf.y()+75,textWidth(),leaf.height()-113);
        });
        if(readable)screen.textButton(leaf.x()+14,leaf.y()+leaf.height()-31,textWidth(),tr("open"),()->session().open(id));
    }

    private void search() {
        heading(tr("search"),0);
        var first=screen.leaf(0);
        EditBox field=new BookSearchBox(screen.bookFont(),first.x()+14,first.y()+44,textWidth(),14,
                Component.literal(tr("search")),session().view().query(),value->session().search(value));
        field.setBordered(false);field.setTextColor(theme().color("ink"));
        field.setTextShadow(false);
        field.setFormatter((value,offset)->canvas().text(value,"body").getVisualOrderText());
        paints.add(g->canvas().panel(g,BookAtlas.F,first.x()+12,first.y()+41,textWidth()+4,16,2,0xFFFFFFFF));
        screen.searchBox(field);screen.setFocused(field);
        int resultsY=first.y()+65;
        if(screen.geometry.spread()||filtersExpanded) {
            for(int i=0;i<26;i++) {
                String letter=Character.toString((char)('A'+i));int x=first.x()+14+(i%11)*12,y=first.y()+65+(i/11)*12;
                screen.button(x,y,10,10,Component.literal(letter),()->session().searchFilters(session().view().chapterFilters(),session().view().initial().equals(letter.toLowerCase(Locale.ROOT))?"":letter.toLowerCase(Locale.ROOT)),(g,b,mx,my)->canvas().text(g,letter,"label","accent",x+2,y+1));
            }
            int y=first.y()+106;
            var filterChapters=session().book().getChapters();
            int filterCapacity=Math.max(1,(first.height()-139)/14);
            filterChapterOffset=Math.clamp(filterChapterOffset,0,Math.max(0,filterChapters.size()-filterCapacity));
            for(var chapter:filterChapters.subList(filterChapterOffset,Math.min(filterChapters.size(),filterChapterOffset+filterCapacity))) {
                int rowY=y;
                screen.button(first.x()+14,y,textWidth(),12,Component.literal(I18n.get(chapter.getTitle())),()-> {
                    Set<ResourceLocation> filters=new HashSet<>(session().view().chapterFilters());
                    if(!filters.remove(chapter.getId()))filters.add(chapter.getId());
                    session().searchFilters(filters,session().view().initial());
                },(g,b,mx,my)-> {
                    canvas().sprite(g,BookAtlas.Ch,b.getX(),rowY,0,0,12,12,theme().color("accent"));
                    canvas().text(g,canvas().ellipsis(I18n.get(chapter.getTitle()),"label",textWidth()-16),"label","inkMuted",b.getX()+16,rowY+2);
                });
                y+=14;
            }
            if(filterChapters.size()>filterCapacity) {
                int fy=y;
                screen.textButton(first.x()+14,fy,20,"<",()->filterChapterOffset=Math.max(0,filterChapterOffset-filterCapacity));
                screen.textButton(first.x()+39,fy,20,">",()->filterChapterOffset=Math.min(filterChapters.size()-filterCapacity,filterChapterOffset+filterCapacity));
                y+=17;
            }
            resultsY=y+5;
            if(!screen.geometry.spread()) {
                screen.textButton(first.x()+14,first.y()+first.height()-30,textWidth(),tr("results"),()->filtersExpanded=false);
                return;
            }
        } else screen.textButton(first.x()+14,first.y()+62,47,tr("filters"),()->filtersExpanded=true);
        List<BookSearch.Result> results=session().searchResults();
        int side=screen.geometry.spread()?1:0;var leaf=screen.leaf(side);
        if(side==1) {heading(tr("results"),1);resultsY=leaf.y()+48;}
        else resultsY=Math.max(resultsY,first.y()+83);
        int found=(int)results.stream().filter(result->!result.washedMatch()).count(),washed=results.size()-found;
        int summaryY=resultsY;
        paints.add(g->canvas().text(g,found+" "+tr("found")+" · "+washed+" "+tr("washed"),"label","inkMuted",leaf.x()+14,summaryY));
        resultsY+=14;
        int capacity=Math.max(1,(leaf.y()+leaf.height()-24-resultsY)/18);
        pageCount=Math.max(1,(results.size()+capacity-1)/capacity);
        int from=Math.min(session().view().listPage(),pageCount-1)*capacity;
        for(int i=from;i<Math.min(results.size(),from+capacity);i++) {
            var result=results.get(i);var page=session().entry(result.entryId()).orElse(null);
            if(page!=null)row(page,leaf.x()+14,resultsY+(i-from)*18,textWidth(),()->session().open(page.getId()),result.washedMatch()&&!result.concealed());
        }
    }

    private void glossary() {
        heading(tr("glossary"),0);
        var leaf=screen.leaf(0);
        record Term(String label,int requirement,List<ResourceLocation> references) {}
        List<Term> terms=new ArrayList<>();
        for(var term:session().book().getGlossary()) {
            var targets=term.getReferences().stream().map(session()::target).flatMap(Optional::stream).toList();
            if(targets.isEmpty())continue;
            var readable=targets.stream().filter(this::targetReadable).toList();
            int level=readable.isEmpty()&&targets.getFirst().page()!=null?targets.getFirst().page().getPresentation().revealLevel():0;
            terms.add(new Term(readable.isEmpty()?"":I18n.get(term.getTerm()),level,targets.stream().map(target->target.page()==null?target.chapter().getId():target.page().getId()).toList()));
        }
        terms.sort(Comparator.comparing(Term::label,String.CASE_INSENSITIVE_ORDER));
        List<BookPaginator.Block> blocks=new ArrayList<>();
        Map<String,Integer> concealed=new HashMap<>();
        String initial="";
        for(int i=0;i<terms.size();i++) {
            Term term=terms.get(i);String id="term/"+i;
            if(term.label().isBlank()) {
                concealed.put(id,term.requirement());
                blocks.add(new BookPaginator.Block(id,BookText.parse(" "),BookPaginator.Kind.BODY,24,"",null));
                continue;
            }
            String next=term.label().substring(0,term.label().offsetByCodePoints(0,1)).toUpperCase(Locale.ROOT);
            String group=initial.equals(next)?"":next;initial=next;
            StringBuilder text=new StringBuilder(term.label().replace("{","{{")).append("\n");
            for(var ref:term.references()) {
                var target=session().target(ref).orElse(null);if(target==null)continue;
                String label=targetReadable(target)?(target.page()==null?tr("chapter"):target.book().getSourceIndex().entry(ref)
                        .map(entry->BookCanvas.roman(entry.chapterNumber()).toUpperCase(Locale.ROOT)+"."+screen.pageLabel(target.book(),ref)).orElse("?")):"?";
                text.append("{link:").append(ref).append('|').append(label).append("}  ");
            }
            blocks.add(new BookPaginator.Block(id,BookText.parse(text.toString()),BookPaginator.Kind.BODY,10,group,null));
        }
        var leaves=BookPaginator.paginate(blocks,List.of(),false,false,textWidth(),Math.max(18,leaf.height()-72),run->canvas().width(run,"body"),tr("continued"));
        int columns=screen.geometry.spread()?2:1;
        pageCount=Math.max(1,(leaves.size()+columns-1)/columns);
        int from=Math.min(session().view().listPage(),pageCount-1)*columns;
        for(int side=0;side<columns&&from+side<leaves.size();side++) {
            var bounds=screen.leaf(side);var content=leaves.get(from+side);
            List<BookPaginator.Line> publicLines=new ArrayList<>();
            for(var line:content.lines()) {
                if(concealed.containsKey(line.blockId())) {
                    int y=bounds.y()+47+line.y(),level=concealed.get(line.blockId());
                    paints.add(g->concealed(g,bounds.x()+14,y,level));
                } else publicLines.add(line);
            }
            paintFlow(new BookPaginator.Leaf(publicLines),bounds.x()+14,bounds.y()+47);
        }
    }

    private void bookmarks() {
        heading(tr("bookmarks"),0);var leaf=screen.leaf(0);
        var pins=session().saved().bookmarks().values().stream().filter(pin->pin.bookId().equals(session().book().getResourceLocation())).toList();
        int capacity=Math.max(1,(leaf.height()-70)/18);pageCount=Math.max(1,(pins.size()+capacity-1)/capacity);
        int from=Math.min(session().view().listPage(),pageCount-1)*capacity;
        for(int i=from;i<Math.min(pins.size(),from+capacity);i++) {
            var pin=pins.get(i);int y=leaf.y()+47+(i-from)*18;
            var page=session().entry(pin.entryId()).orElse(null);
            if(page!=null)row(page,leaf.x()+14,y,textWidth(),()->session().open(pin.entryId(),pin,false));
            else paints.add(g->canvas().text(g,tr("unavailable"),"body","inkRead",leaf.x()+14,y+4));
        }
    }

    void concealed(GuiGraphics graphics,int x,int y,int requirement) {
        graphics.renderFakeItem(new ItemStack(Items.BOOK),x,y);
        canvas().wash(graphics,x+20,y+1,Math.min(80,textWidth()-20),7,1);
        String label=session().book().getReaderHooks().requirement().apply(requirement).getString();
        canvas().text(graphics,canvas().ellipsis(label,"label",textWidth()-20),"label","inkMuted",x+20,y+9);
    }

    void seeAlso(GuiGraphics graphics,ResourceLocation id,int x,int y) {
        var target=session().target(id).orElse(null);
        if(target==null) {canvas().text(graphics,tr("unavailable"),"body","inkRead",x,y+4);return;}
        if(target.page()!=null&&!target.book().canReveal(session().player(),target.page().getPresentation().revealLevel())) {
            concealed(graphics,x,y,target.page().getPresentation().revealLevel());return;
        }
        ItemStack icon=target.page()==null?target.chapter().getIconItem():target.page().getIconItem();
        String title=I18n.get(target.page()==null?target.chapter().getTitle():target.page().getTitle());
        canvas().panel(graphics,BookAtlas.P,x,y,textWidth(),18,3,theme().color("inkMuted"));
        graphics.renderFakeItem(icon,x+2,y+1);
        canvas().text(graphics,canvas().ellipsis(title,"body",textWidth()-28),"body","link",x+21,y+4);
        canvas().text(graphics,">","label","accent",x+textWidth()-7,y+4);
    }

    List<Component> previewTooltip(ResourceLocation id) {
        var target=session().target(id).orElse(null);
        if(target==null)return List.of(Component.literal(tr("unavailable")));
        if(target.page()==null)return List.of(Component.literal(I18n.get(target.chapter().getTitle())));
        if(!target.book().canReveal(session().player(),target.page().getPresentation().revealLevel())) return List.of(target.book().getReaderHooks().requirement().apply(target.page().getPresentation().revealLevel()));
        BookEntryContent content=BookEntryContent.localize(target.page(),target.chapter().getId(),value->I18n.get(value));
        String text=content.sanitizedText(level->target.book().canReveal(session().player(),level)).replace('\n',' ');
        if(text.codePointCount(0,text.length())>96)text=text.substring(0,text.offsetByCodePoints(0,95))+"…";
        return List.of(Component.literal(content.title()),Component.literal(text),Component.literal(tr("click_open")));
    }

    private boolean targetReadable(BookReaderSession.Target target) {
        if(target.page()!=null)return target.book().canReveal(session().player(),target.page().getPresentation().revealLevel());
        return target.chapter().getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast)
                .anyMatch(page->target.book().canReveal(session().player(),page.getPresentation().revealLevel()));
    }

    private void pageControls() {
        int y=screen.footerY();
        screen.textButton(screen.geometry.left()+14,y-2,20,"<",()->changePage(-1));
        screen.textButton(screen.geometry.left()+screen.geometry.viewportWidth()-34,y-2,20,">",()->changePage(1));
        paints.add(g->canvas().centered(g,(session().view().listPage()+1)+" / "+pageCount,"label","inkMuted",screen.geometry.left()+screen.geometry.viewportWidth()/2,y+1));
    }
    private void changePage(int direction) {session().listPage(Math.clamp(session().view().listPage()+direction,0,pageCount-1));}
    void scroll(double amount) {if(pageCount>1)changePage(amount<0?1:-1);}
    private void drawParagraph(GuiGraphics graphics,String text,int x,int y,int width,int height) {
        for(var line:screen.bookFont().split(Component.literal(text),width)) {
            if(height<9)break;
            graphics.drawString(screen.bookFont(),line,x,y,theme().color("ink"),false);y+=9;height-=9;
        }
    }

    private List<BookPaginator.Leaf> flow(String id,String text,int width,int height) {
        return BookPaginator.paginate(List.of(new BookPaginator.Block(id,BookText.parse(I18n.get(text)),BookPaginator.Kind.BODY,9,"",null)),List.of(),false,false,
                width,Math.max(9,height),run->canvas().width(run,"body"),tr("continued"));
    }

    private void rich(String id,String text,int x,int y,int width,int height,int page) {
        var leaves=flow(id,text,width,height);
        pageCount=Math.max(pageCount,leaves.size());
        paintFlow(leaves.get(Math.clamp(page,0,leaves.size()-1)),x,y);
    }

    private List<BookPaginator.Leaf> chapterFlow(ChapterTemplate chapter,int height) {
        var presentation=chapter.getPresentation();
        List<BookPaginator.Block> blocks=new ArrayList<>();
        if(!presentation.text().isBlank())blocks.add(new BookPaginator.Block("introduction",BookText.parse(I18n.get(presentation.text())),BookPaginator.Kind.BODY,9,"",null));
        if(!presentation.margin().isBlank())blocks.add(new BookPaginator.Block("margin",BookText.parse(I18n.get(presentation.margin())),
                theme().font("hand")==null?BookPaginator.Kind.FOOTNOTE:BookPaginator.Kind.MARGIN,9,"",null));
        presentation.callout().ifPresent(callout->blocks.add(new BookPaginator.Block("callout",BookText.parse(I18n.get(callout.text())),BookPaginator.Kind.RECORD,9,"",null)));
        return BookPaginator.paginate(blocks,List.of(),false,false,textWidth(),height,new BookPaginator.Measurer() {
            public int width(BookText.Run run){return canvas().width(run,"body");}
            public int width(BookText.Run run,BookPaginator.Kind kind){return canvas().width(run,kind==BookPaginator.Kind.MARGIN&&theme().font("hand")!=null?"hand":"body");}
        },tr("continued"));
    }

    private void paintFlow(BookPaginator.Leaf leaf,int x,int y) {
        paints.add(graphics->leaf.lines().forEach(line->screen.renderLine(graphics,line,x,y+line.y())));
        for(var line:leaf.lines()) {
            int currentX=x+(line.kind()==BookPaginator.Kind.MARGIN?26:0);
            String face=line.kind()==BookPaginator.Kind.MARGIN&&theme().font("hand")!=null?"hand":"body";
            for(var run:line.runs()) {
                int runWidth=canvas().width(run,face);
                if(run.target()!=null&&run.readable(session().revealAccess()))screen.linkButton(run.target(),run.text(),currentX,y+line.y(),runWidth,line.height());
                currentX+=runWidth;
            }
        }
    }
}
