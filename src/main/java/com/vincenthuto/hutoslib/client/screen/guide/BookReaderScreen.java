package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.book.*;
import com.vincenthuto.hutoslib.common.book.BookText;
import com.vincenthuto.hutoslib.common.book.ReaderLocation;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.*;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.function.Supplier;

/** The screen stays open while its session changes views, entries and books. */
public final class BookReaderScreen extends HLGuiGuidePage {
    final BookReaderSession session;
    BookGeometry geometry;
    BookVisualTheme visual;
    BookCanvas canvas;
    final BookReaderViews views;
    final Map<ResourceLocation, BookReaderState.EntryStatus> initialBadges = new HashMap<>();
    private record EntryPane(PageTemplate page, List<BookPaginator.Leaf> leaves, BookBodyRenderer renderer,
                             int offset, int height, int side) {}
    private List<EntryPane> pairedEntries = List.of();
    private final Map<ResourceLocation,Integer> pairedScroll = new HashMap<>();
    private final Map<ResourceLocation,double[]> pairedAngles = new HashMap<>();
    private int pairedReadTicks;
    private final BookTurn turn = new BookTurn();
    private final Map<ResourceLocation, BookPageNumbers> pageNumbers = new HashMap<>();
    private List<BookPaginator.Leaf> leaves = List.of();
    private int leafIndex, bodyOffset, bodyHeight, customScroll, compactScroll, tabOffset;
    private long builtRevision = -1;
    private BookBodyRenderer bodyRenderer;
    private EditBox searchBox;
    private List<Component> hoveredTooltip = List.of();
    private ResourceLocation hoveredTarget, pinnedTarget;
    private final Map<String, Integer> revealAges = new HashMap<>();
    private final Map<Integer, Boolean> lastAccess = new HashMap<>();
    private ResourceLocation builtEntry;
    private boolean opened;
    private char consumedShortcut;
    private ResourceLocation revealSource;
    private String revealVersion;

    public BookReaderScreen(BookReaderSession session) {
        super(session.book());
        this.session = session;
        this.views = new BookReaderViews(this);
    }

    public static void open(BookCodeModel book, ItemGuideBook item, Supplier<BookCodeModel> refresher, IBookKnowledge knowledge) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        BookReaderSession session = new BookReaderSession(mc.player, new BookAccessContext(book, item, refresher, knowledge));
        if (session.available()) mc.setScreen(new BookReaderScreen(session));
    }

    public static void openEntry(BookCodeModel book, ChapterTemplate chapter, int page) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof BookReaderScreen)) open(book,null,null,null);
        if (mc.screen instanceof BookReaderScreen reader && page >= 0 && page < chapter.getPages().size()) {
            reader.session.open(chapter.getPages().get(page).getId());
            reader.rebuild();
        }
    }

    public static void openChapter(BookCodeModel book, ChapterTemplate chapter) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof BookReaderScreen)) open(book,null,null,null);
        if (mc.screen instanceof BookReaderScreen reader) {
            reader.session.open(chapter.getId());
            reader.rebuild();
        }
    }

    public static void refreshIfOpen() {
        if (Minecraft.getInstance().screen instanceof BookReaderScreen reader) {
            reader.session.refresh();
            reader.rebuild();
        }
    }

    @Override protected void init() { rebuild(); if (!opened) { sound("open"); opened=true; } }
    Font bookFont() { return font; }
    EditBox searchBox() { return searchBox; }
    void searchBox(EditBox box) { searchBox = addRenderableWidget(box); }
    int footerY() { return geometry.top() + geometry.viewportHeight() - (geometry.compact()?30:16); }

    void rebuild() {
        if (!session.available()) { onClose(); return; }
        book = session.book();
        pageTemplate = session.view().location() == null ? null : session.entry(session.view().location().entryId()).orElse(null);
        ResourceLocation currentEntry = pageTemplate == null ? null : pageTemplate.getId();
        if (!Objects.equals(builtEntry,currentEntry)) { customScroll=0; compactScroll=0; lastAccess.clear(); builtEntry=currentEntry; }
        chapter = session.chapter(session.view().chapterId()).orElse(null);
        BookDataTemplate surface=pageTemplate!=null?pageTemplate:chapter!=null?chapter:book.getTemplate();
        revealSource=surface.getId();
        revealVersion=BookSourceIndex.surfaceVersion(surface);
        visual = BookThemeManager.INSTANCE.resolve(book, pageTemplate instanceof PageTemplate page ? page : null, chapter);
        canvas = new BookCanvas(font,visual);
        geometry = BookGeometry.fit(width,height,visual.layout(),book.getChapters().size());
        pageNumbers.clear();
        left = geometry.left(); top = geometry.top(); guiWidth = geometry.leafWidth(); guiHeight = geometry.leafHeight();
        int searchCursor = searchBox != null ? searchBox.getCursorPosition() : -1;
        clearWidgets();
        initialBadges.clear();
        searchBox = null;
        bodyRenderer = null;
        leaves = List.of();
        pairedEntries = List.of(); pairedReadTicks=0; pairedScroll.clear(); pairedAngles.clear();
        hoveredTarget = null;
        hoveredTooltip = List.of();
        if (pageTemplate instanceof PageTemplate page && session.view().view() == BookReaderSession.View.ENTRY) buildEntry(page);
        else views.build();
        if (searchBox != null && searchCursor >= 0) searchBox.moveCursorTo(Math.min(searchCursor,searchBox.getValue().length()),false);
        buildCommonControls();
        buildTabs();
        builtRevision = session.revision();
    }

    BookPageRenderContext.Bounds leaf(int side) {
        int x = geometry.left() + (geometry.spread() ? 4 + side*170 : 0);
        int y = geometry.top() + (geometry.spread() ? 4 : 0);
        int width = geometry.compact() ? geometry.viewportWidth() : geometry.leafWidth();
        int height = geometry.compact() ? geometry.viewportHeight() : geometry.leafHeight();
        return new BookPageRenderContext.Bounds(x,y,width,height);
    }

    public static void openPlaced(net.minecraft.core.BlockPos pos, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(stack.getItem() instanceof ItemGuideBook item)) return;
        var id = item.getBookId();
        var model = id == null ? null : BookPlaceboReloadListener.INSTANCE.getBookByTitle(id);
        if (model == null) return;
        var session = new BookReaderSession(mc.player, new BookAccessContext(model, item, null, null).atLectern(pos));
        if (session.available()) mc.setScreen(new BookReaderScreen(session));
    }

    BookPageNumbers.Range pageRange(BookCodeModel model, ResourceLocation entry) {
        BookPageNumbers numbers = pageNumbers.computeIfAbsent(model.getResourceLocation(), ignored -> {
            BookPageNumbers result = new BookPageNumbers();
            var source = model.getSourceIndex().sourceChapters();
            if (source.isEmpty()) source = model.getChapters();
            for (ChapterTemplate sourceChapter : source) for (BookDataTemplate template : sourceChapter.getPages()) {
                if (!(template instanceof PageTemplate page)) continue;
                var theme = BookThemeManager.INSTANCE.resolve(model, page, sourceChapter);
                var bounds = BookGeometry.fit(width, height, theme.layout(), source.size());
                var content = BookEntryContent.localize(page, sourceChapter.getId(), value -> I18n.get(value));
                boolean custom = page.getPageRenderer() != null || page.getBodyRenderer() != null
                        || LayoutArchetypes.resolve(page.getPresentation().layout(), BookLayout.Kind.TEXT).renderer() != null;
                int count = custom ? 1 : (int) BookEntryLayout.paginate(page, content,
                        new BookCanvas(font, theme), theme, bounds).stream().filter(leaf -> !leaf.blank()).count();
                result.append(page.getId(), count);
            }
            return result;
        });
        return numbers.range(entry);
    }

    String pageLabel(BookCodeModel model, ResourceLocation entry) {
        var range = pageRange(model, entry);
        return range == null ? "?" : range.label();
    }

    BookActionButton button(int x,int y,int width,int height,Component label,Runnable action,BookActionButton.Painter painter) {
        BookActionButton button = new BookActionButton(x,y,width,height,label,() -> { if (!turn.active()) { action.run(); rebuild(); } },painter);
        return addRenderableWidget(button);
    }

    void textButton(int x,int y,int width,String label,Runnable action) {
        button(x,y,width,14,Component.literal(label),action,(graphics,button,mx,my) -> {
            canvas.panel(graphics,BookAtlas.P,button.getX(),button.getY(),button.getWidth(),14,3,visual.color("accent"));
            canvas.centered(graphics,canvas.ellipsis(label,"label",width-6),"label","accent",x+width/2,y+3);
        });
    }

    void tooltip(List<Component> lines) { hoveredTooltip = lines; }

    void linkButton(ResourceLocation target, String label, int x,int y,int width,int height) {
        if(BookPlaceboReloadListener.INSTANCE.findTarget(target).isEmpty())return;
        var link=button(x,y,Math.max(1,width),height,Component.literal(label),() -> session.open(target),(graphics,button,mx,my) -> {
            if (button.isHoveredOrFocused()) { hoveredTarget=target; hoveredTooltip=views.previewTooltip(target); }
        });
        var resolved=session.target(target);
        link.active=resolved.isPresent()&&(resolved.get().page()==null||resolved.get().book().canReveal(session.player(),resolved.get().page().getPresentation().revealLevel()));
    }

    private void buildCommonControls() {
        String[] labels={tr("back"),tr("home"),tr("search"),tr("close")};
        Runnable[] actions={session::back,()->session.show(BookReaderSession.View.COVER,null),()->session.search(""),this::onClose};
        for (int i=0;i<4;i++) {
            int index=i;
            int x=geometry.compact()?left+18+i*14:left-16;
            int y=geometry.compact()?footerY()+14:top+12+i*14;
            button(x,y,12,12,Component.literal(labels[i]),actions[i],(graphics,b,mx,my)-> {
                boolean hovered=b.isHoveredOrFocused();
                graphics.fill(x+1,y+1,x+13,y+13,0x99000000);
                graphics.fill(x,y,x+12,y+12,visual.color(hovered?"accentHi":"accent")|0xFF000000);
                graphics.fill(x+1,y+1,x+11,y+11,visual.color(hovered?"paperEdge":"paper")|0xFF000000);
                canvas.sprite(graphics,BookAtlas.B,x+1,y+1,index*10,hovered?10:0,10,10,visual.color("accent"));
                if(hovered)tooltip(List.of(Component.literal(labels[index])));
            });
        }
        if (session.view().view()==BookReaderSession.View.ENTRY) {
            button(left+14,footerY(),12,10,Component.literal(tr("previous")),()->startTurn(-1),(graphics,b,mx,my)->canvas.sprite(graphics,BookAtlas.N,b.getX(),b.getY(),b.isHoveredOrFocused()?12:0,0,12,10,visual.color("accent")));
            int x=left+geometry.viewportWidth()-26;
            button(x,footerY(),12,10,Component.literal(tr("next")),()->startTurn(1),(graphics,b,mx,my)->canvas.sprite(graphics,BookAtlas.N,b.getX(),b.getY(),b.isHoveredOrFocused()?48:36,0,12,10,visual.color("accent")));
            button(left+geometry.viewportWidth()-12,top+8,7,28,Component.literal(tr("bookmark")),this::toggleBookmark,(graphics,b,mx,my)-> {
                if (pageTemplate==null) return;
                boolean pinned=session.saved().bookmarks().containsKey(pageTemplate.getId());
                if (pinned || b.isHoveredOrFocused()) canvas.ribbon(graphics,b.getX(),b.getY(),28,visual.color("ribbon"));
            });
        }
    }

    private void buildTabs() {
        if (geometry.compact() || visual.layout().tabStyle().equals("none")) return;
        boolean head=visual.layout().tabStyle().equals("head");
        int capacity=head?(geometry.spread()?8:4):9;
        List<ChapterTemplate> chapters=book.getChapters();
        tabOffset=Math.clamp(tabOffset,0,Math.max(0,chapters.size()-capacity));
        for (int i=0;i<Math.min(capacity,chapters.size()-tabOffset);i++) {
            ChapterTemplate target=chapters.get(tabOffset+i);
            int number=book.getSourceIndex().chapters().getOrDefault(target.getId(),tabOffset+i+1);
            int index=i;
            int x=head?left+(geometry.spread() && i<4?174:4)+(i%4)*34+6:visual.layout().tabStyle().equals("spine")?left-24:left+geometry.outerWidth();
            int y=head?top-22:top+12+i*21;
            button(x,y,head?32:24,head?22:20,Component.literal(I18n.get(target.getTitle())),()->session.show(BookReaderSession.View.CHAPTER,target.getId()),(graphics,b,mx,my)-> {
                boolean active=target.getId().equals(session.view().chapterId());
                int state=active?2:b.isHoveredOrFocused()?1:0;
                int color=tabColor(target);
                int extent=active?6:Math.round(3*b.hoverProgress(visual.motion().tabTicks()));
                if(visual.legacyTabs()!=null) BookCanvas.blit(graphics,visual.legacyTabs(),x,y,24,16,0,state==0?192:208,24,16,256,256,color);
                else canvas.sprite(graphics,head?BookAtlas.Th:BookAtlas.Tf,x,y,head?state*32:state*24,0,head?32:18+extent,head?16+extent:20,color);
                graphics.drawString(font,BookCanvas.roman(number).toUpperCase(Locale.ROOT),x+4,y+5,contrastLabel(color),false);
                int unread=(int)target.getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast)
                        .filter(page->session.content(page).readable(session.revealAccess()) && session.status(page)!=BookReaderState.EntryStatus.READ).count();
                if(unread>0) {
                    String count=Integer.toString(Math.min(9,unread));
                    int badgeWidth=Math.max(5,canvas.width(count,"badge")+2);
                    int badgeHeight=font.lineHeight+2;
                    int tabWidth=head?32:visual.legacyTabs()!=null?24:18+extent;
                    int bx=x+tabWidth-badgeWidth+3,by=y-3;
                    var badge=visual.sprite(BookAtlas.Bd);
                    BookCanvas.blit(graphics,badge.texture(),bx,by,badgeWidth,badgeHeight,
                            badge.u(),badge.v(),5,5,badge.textureWidth(),badge.textureHeight(),visual.color("notice"));
                    canvas.text(graphics,count,"badge","ink",bx+1,by+1);
                }
                if(b.isHoveredOrFocused()) tooltip(List.of(Component.literal(I18n.get(target.getTitle()))));
            });
        }
        if(chapters.size()>capacity) {
            int x=head?left+geometry.outerWidth()-10:left+geometry.outerWidth();
            int y=head?top-10:top+204;
            button(x,y,10,10,Component.literal(tr("previous")),()->tabOffset=Math.max(0,tabOffset-capacity),(g,b,mx,my)->canvas.text(g,"<","label","accent",x+2,y+1));
            button(x+12,y,10,10,Component.literal(tr("more_chapters")),()->tabOffset=Math.min(chapters.size()-capacity,tabOffset+capacity),(g,b,mx,my)->canvas.text(g,">","label","accent",x+14,y+1));
        }
    }

    private boolean buildPairedEntries(PageTemplate selected) {
        if (!geometry.spread() || chapter == null) return false;
        var source = book.getSourceIndex().sourceChapters().stream()
                .filter(value -> value.getId().equals(chapter.getId())).findFirst().orElse(chapter);
        List<ResourceLocation> order = source.getPages().stream().filter(PageTemplate.class::isInstance)
                .map(BookDataTemplate::getId).toList();
        var ids = BookSpreadPairing.pair(order, selected.getId(), id -> {
            var page = session.entry(id).orElse(null);
            if (page == null || page.getPageRenderer() != null || page.getPresentation().layout().equals("record")
                    || !session.content(page).readable(session.revealAccess())) return false;
            var range = pageRange(book,id);
            return range != null && range.count() == 1;
        });
        if (ids.isEmpty()) return false;
        List<EntryPane> panes = new ArrayList<>();
        for (int side=0;side<ids.size();side++) {
            var page=session.entry(ids.get(side)).orElseThrow();
            var content=session.content(page);
            int offset=BookEntryLayout.bodyOffset(content,canvas,geometry);
            int height=BookEntryLayout.bodyHeight(offset,geometry);
            var renderer=page.getBodyRenderer();
            if(renderer==null)renderer=LayoutArchetypes.resolve(page.getPresentation().layout(),BookLayout.Kind.TEXT).renderer();
            var flow=renderer==null?BookEntryLayout.paginate(page,content,canvas,visual,geometry):List.<BookPaginator.Leaf>of();
            var pane=new EntryPane(page,flow,renderer,offset,height,side);
            panes.add(pane);
            if(!flow.isEmpty())buildLeafLinks(flow.getFirst(),side,offset,height,0);
            var bounds=leaf(side);
            button(bounds.x()+14,bounds.y()+13,geometry.textWidth(),offset-13,Component.literal(content.title()),
                    ()->session.open(page.getId()),(g,b,mx,my)->{});
        }
        pairedEntries=List.copyOf(panes);
        leafIndex=0;
        session.consumeLastViewRequest();
        session.anchor(session.view().location());
        return true;
    }

    private void buildEntry(PageTemplate page) {
        if (buildPairedEntries(page)) return;
        BookEntryContent content=session.content(page);
        bodyOffset=BookEntryLayout.bodyOffset(content,canvas,geometry);
        bodyHeight=BookEntryLayout.bodyHeight(bodyOffset,geometry);
        bodyRenderer=page.getBodyRenderer();
        if(bodyRenderer==null) bodyRenderer=LayoutArchetypes.resolve(page.getPresentation().layout(),BookLayout.Kind.TEXT).renderer();
        boolean lastView=session.consumeLastViewRequest();
        if(!content.readable(session.revealAccess())) return;
        if(bodyRenderer!=null || page.getPageRenderer()!=null) {
            session.anchor(session.view().location());
            leafIndex=0;
            return;
        }
        leaves=BookEntryLayout.paginate(page,content,canvas,visual,geometry);
        ReaderLocation anchor=session.view().location();
        var source=content.blocks().get(anchor.blockId());
        if(source!=null) {
            String plain=source.plainText();
            anchor=new ReaderLocation(anchor.bookId(),anchor.entryId(),anchor.blockId(),Math.min(anchor.codePointOffset(),plain.codePointCount(0,plain.length())));
        } else if(leaves.stream().flatMap(leaf->leaf.lines().stream()).noneMatch(line->line.blockId().equals(session.view().location().blockId()))) {
            anchor=new ReaderLocation(anchor.bookId(),anchor.entryId(),content.blocks().keySet().iterator().next(),0);
        }
        leafIndex=lastView?leaves.size()-1:BookPaginator.locate(leaves,anchor.blockId(),anchor.codePointOffset());
        if(geometry.spread()) leafIndex-=leafIndex%2;
        buildEntryLinks();
        buildNoteMarkers(page);
        if(lastView)rememberAnchor();
        else session.anchor(anchor);
    }

    private void buildNoteMarkers(PageTemplate page) {
        List<String> noteIds=new ArrayList<>();
        if (!page.getPresentation().margin().isEmpty()&&visual.font("hand")==null) noteIds.add("margin");
        for(int i=0;i<page.getPresentation().footnotes().size();i++) noteIds.add("footnote/"+i);
        if(noteIds.isEmpty())return;
        int x=left+44;
        for(int i=0;i<noteIds.size();i++) {
            String id=noteIds.get(i),label="["+(i+1)+"]"; int markerX=x;
            button(x,footerY()-12,canvas.width(label,"label")+3,10,Component.literal(tr("note")+" "+(i+1)),()-> {
                session.anchor(new ReaderLocation(book.getResourceLocation(),page.getId(),id,0)); compactScroll=0;
            },(g,b,mx,my)->canvas.text(g,label,"label","link",markerX,footerY()-10));
            x+=canvas.width(label,"label")+5;
        }
    }

    private void buildEntryLinks() {
        for(int side=0;side<(geometry.spread()?2:1);side++) {
            int index=leafIndex+side;
            if(index<leaves.size())buildLeafLinks(leaves.get(index),side,contentOffset(index),bodyOffset+bodyHeight-contentOffset(index),compactScroll);
        }
    }

    private void buildLeafLinks(BookPaginator.Leaf flow,int side,int offset,int height,int scroll) {
        var bounds=leaf(side);
        for(var line:flow.lines()) {
            int x=bounds.x()+14,y=bounds.y()+offset+line.y()-scroll;
            if(line.kind()==BookPaginator.Kind.MARGIN)x+=26;
            if(y<bounds.y()+offset||y+line.height()>Math.min(bounds.y()+offset+height,footerY()-4))continue;
            if(line.kind()==BookPaginator.Kind.SEE_ALSO){linkButton(line.target(),tr("see_also"),x,y,geometry.textWidth(),18);continue;}
            String face=line.kind()==BookPaginator.Kind.MARGIN&&visual.font("hand")!=null?"hand":"body";
            for(var run:line.runs()) {
                int width=canvas.width(run,face);
                if(run.target()!=null&&run.readable(session.revealAccess()))linkButton(run.target(),run.text(),x,y,width,line.height());
                x+=width;
            }
        }
    }

    private int contentOffset(int index) {
        return geometry.spread()&&index==1&&pageTemplate instanceof PageTemplate page&&page.getPresentation().layout().equals("record")?13:bodyOffset;
    }

    private void rememberAnchor() {
        if(leaves.isEmpty() || pageTemplate==null) return;
        leaves.get(leafIndex).lines().stream().filter(line->!line.label()).findFirst().ifPresent(line->session.anchor(new ReaderLocation(book.getResourceLocation(),pageTemplate.getId(),line.blockId(),line.start())));
    }

    private void startTurn(int direction) {
        if(session.view().view()!=BookReaderSession.View.ENTRY) {views.scroll(direction>0?-1:1);rebuild();return;}
        sound("turn");
        turn.start(visual.motion().turnTicks(),()-> {
            if(!pairedEntries.isEmpty()) {
                var edge=direction>0?pairedEntries.getLast():pairedEntries.getFirst();
                session.adjacent(direction,edge.page().getId());rebuild();return;
            }
            int step=geometry.spread()?2:1;
            int next=leafIndex+direction*step;
            if(!leaves.isEmpty() && next>=0 && next<leaves.size()) {leafIndex=next;rememberAnchor();rebuild();}
            else {session.adjacent(direction);rebuild();}
        });
    }

    @Override public void tick() {
        if(session.refresh() || builtRevision!=session.revision()) rebuild();
        if(!session.available()) return;
        turn.tick();
        revealAges.replaceAll((key,age)->Math.min(1200,age+1));
        if(pageTemplate instanceof PageTemplate page) {
            Set<Integer> levels=new HashSet<>(); levels.add(page.getPresentation().revealLevel());
            session.content(page).blocks().values().forEach(text->text.runs().forEach(run->levels.addAll(run.levels())));
            for(int level:levels) if(level>0) {
                boolean readable=session.revealAccess().test(level);
                Boolean previous=lastAccess.put(level,readable);
                if(previous!=null&&!previous&&readable) revealAges.put(revealKey(page,level),0);
            }
        }
        session.readingTick(!turn.active() && session.view().view()==BookReaderSession.View.ENTRY);
        if(!pairedEntries.isEmpty()&&!turn.active()) {
            if(++pairedReadTicks==20) for(var pane:pairedEntries)
                session.saved().markRead(pane.page().getId(),session.version(pane.page()),session.content(pane.page()).revealedLevels(session.revealAccess()));
        } else pairedReadTicks=0;
        if(pinnedTarget!=null && session.target(pinnedTarget).isEmpty()) pinnedTarget=null;
    }

    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        if(!session.available()) return;
        // Custom renderers and packet callbacks can navigate between the screen tick and this frame.
        if(builtRevision!=session.revision()) rebuild();
        renderBlurredBackground(partialTick);
        graphics.fill(0,0,width,height,0x88000000);
        canvas.shell(graphics,geometry,session.view().view()==BookReaderSession.View.COVER);
        hoveredTooltip=List.of(); hoveredTarget=null;
        if(session.view().view()==BookReaderSession.View.ENTRY && pageTemplate instanceof PageTemplate page) renderEntry(graphics,page,mouseX,mouseY,partialTick);
        else views.render(graphics,mouseX,mouseY,partialTick);
        for(var widget:renderables) widget.render(graphics,mouseX,mouseY,partialTick);
        if(turn.active()) {
            float p=turn.progress(partialTick);
            int fold=Math.round(Math.abs(2*p-1)*geometry.leafWidth());
            if(fold>0) {
                int edge=geometry.spread()?left+174:left;
                int foldX=geometry.spread()?(p<0.5f?edge:edge-fold):(p<0.5f?left+geometry.viewportWidth()-fold:left);
                ResourceLocation texture=visual.texture("leaf");
                if(texture!=null) BookCanvas.blit(graphics,texture,foldX,top+4,fold,geometry.viewportHeight()-8,0,0,174,1,174,1,visual.color("paper"));
                else graphics.fillGradient(foldX,top+4,foldX+fold,top+geometry.viewportHeight()-4,visual.color("paper"),visual.color("paperEdge"));
                graphics.fill(foldX+fold,top+4,foldX+fold+2,top+geometry.viewportHeight()-4,0x44000000);
            }
        }
        if(pinnedTarget!=null) hoveredTooltip=views.previewTooltip(pinnedTarget);
        if(!hoveredTooltip.isEmpty()) graphics.renderComponentTooltip(font,hoveredTooltip,mouseX,mouseY);
    }

    private void renderEntry(GuiGraphics graphics,PageTemplate page,int mouseX,int mouseY,float partialTick) {
        if(!pairedEntries.isEmpty()) {
            for(var pane:pairedEntries) {
                revealSource=pane.page().getId();revealVersion=session.version(pane.page());
                renderEntryLeaf(graphics,pane.page(),pane.leaves(),0,pane.renderer(),pane.offset(),pane.height(),pane.side(),
                        pairedScroll.getOrDefault(pane.page().getId(),0),mouseX,mouseY,partialTick);
            }
            String label=pairedEntries.stream().map(pane->"p."+pageLabel(book,pane.page().getId())).collect(java.util.stream.Collectors.joining(" · "));
            canvas.centered(graphics,label,"label","inkMuted",geometry.left()+geometry.viewportWidth()/2,footerY()+1);
            return;
        }
        var content=session.content(page);
        if(!content.readable(session.revealAccess())) { views.concealed(graphics,leaf(0).x()+14,leaf(0).y()+35,page.getPresentation().revealLevel()); return; }
        int sides=geometry.spread()?2:1;
        for(int side=0;side<sides;side++) renderEntryLeaf(graphics,page,leaves,leafIndex+side,bodyRenderer,
                bodyOffset,bodyHeight,side,compactScroll,mouseX,mouseY,partialTick);
        int total=(int)leaves.stream().filter(leaf->!leaf.blank()).count();
        if(total>0) {
            var range=pageRange(book,page.getId());
            if(range!=null) {
                int first=range.leafNumber(leaves,leafIndex);
                String label="p."+first;
                if(geometry.spread()&&leafIndex+1<leaves.size()&&!leaves.get(leafIndex+1).blank()) label+=" · p."+(first+1);
                canvas.centered(graphics,label,"label","inkMuted",geometry.left()+geometry.viewportWidth()/2,footerY()+1);
            }
        }
    }

    private void renderEntryLeaf(GuiGraphics graphics,PageTemplate page,List<BookPaginator.Leaf> leaves,int index,
            BookBodyRenderer bodyRenderer,int bodyOffset,int bodyHeight,int side,int scroll,int mouseX,int mouseY,float partialTick) {
            var content=session.content(page);
            float wholeWash=revealWash(page,page.getPresentation().revealLevel());
            var bounds=leaf(side);
            boolean companion=(geometry.spread() && index==1 && page.getPresentation().layout().equals("record")?13:bodyOffset)==13;
            if(index>0 && !companion && (leaves.size()<=index||leaves.get(index).blank())) return;
            int x=bounds.x()+14,y=bounds.y()+13;
            if((!pairedEntries.isEmpty()||leafIndex==0 && side==(geometry.spread()&&page.getPresentation().layout().equals("record")?1:0))) canvas.decal(graphics,page,bounds.x()+bounds.width()-72,bounds.y()+bounds.height()-94);
            if((side==0||!pairedEntries.isEmpty())&&page.getPresentation().pinned()) canvas.ribbon(graphics,bounds.x()+6,bounds.y(),40,visual.color("seal"));
            if(page.getPageRenderer()==null&&!companion) {
                graphics.renderFakeItem(page.getIconItem(),x,y);
                var range=pageRange(book,page.getId());
                int number=range==null?0:range.leafNumber(leaves,index);
                int chapterNumber=book.getSourceIndex().chapters().getOrDefault(chapter.getId(),1);
                String reference="p."+number;
                int refWidth=canvas.width(reference,"label");
                canvas.text(graphics,canvas.ellipsis(BookCanvas.roman(chapterNumber).toUpperCase(Locale.ROOT)+" · "+I18n.get(chapter.getTitle()),"label",geometry.textWidth()-25-refWidth),"label","inkMuted",x+20,y+4);
                canvas.text(graphics,reference,"label","inkMuted",x+geometry.textWidth()-refWidth,y+4);
                y+=19;
                if(mouseX>=x&&mouseX<x+geometry.textWidth()&&mouseY>=y&&mouseY<bounds.y()+bodyOffset)
                    tooltip(content.subtitle().isBlank()?List.of(Component.literal(content.title())):List.of(Component.literal(content.title()),Component.literal(content.subtitle())));
                for(String line:canvas.heading(content.title(),"title",geometry.textWidth())) { canvas.text(graphics,line,"title","accent",x,y);y+=16; }
                if(!content.subtitle().isBlank()) canvas.text(graphics,canvas.ellipsis(content.subtitle(),"body",geometry.textWidth()),"body","inkMuted",x,y+1);
                if(visual.layout().rule()) graphics.fill(x,bounds.y()+bodyOffset-5,x+geometry.textWidth(),bounds.y()+bodyOffset-4,visual.color("accent"));
            }
            int contentTop=bounds.y()+(geometry.spread() && index==1 && page.getPresentation().layout().equals("record")?13:bodyOffset);
            int clipTop=page.getPageRenderer()!=null?bounds.y()+13:contentTop-3;
            int clipBottom=Math.min(bounds.y()+bodyOffset+bodyHeight,footerY()-4);
            graphics.enableScissor(x-4,clipTop,x+geometry.textWidth()+4,clipBottom);
            if(page.getPageRenderer()!=null) {
                left=bounds.x();top=bounds.y();
                page.getPageRenderer().render(graphics,page,this,mouseX,mouseY,partialTick);
                left=geometry.left();top=geometry.top();
            } else if(bodyRenderer!=null) bodyRenderer.render(customContext(graphics,page,mouseX,mouseY,partialTick,side,bodyOffset,bodyHeight,scroll));
            else if(index<leaves.size()) {
                var recordLines=leaves.get(index).lines().stream().filter(line->line.kind()==BookPaginator.Kind.RECORD).toList();
                if(!recordLines.isEmpty()) {
                    int ry=recordLines.getFirst().y(),bottom=recordLines.getLast().y()+recordLines.getLast().height();
                    canvas.panel(graphics,BookAtlas.P,x-3,contentTop+ry-3-scroll,geometry.textWidth()+6,bottom-ry+6,3,visual.color("inkMuted"));
                }
                for(var line:leaves.get(index).lines()) {
                    int lineY=contentTop+line.y()-scroll;
                    if(lineY+line.height()>clipTop&&lineY<clipBottom)renderLine(graphics,line,x,lineY);
                }
            }
            graphics.disableScissor();
            if(wholeWash>0) canvas.wash(graphics,x,bounds.y()+13,geometry.textWidth(),Math.max(8,clipBottom-bounds.y()-13),wholeWash);
    }

    void renderLine(GuiGraphics graphics,BookPaginator.Line line,int x,int y) {
        if(line.kind()==BookPaginator.Kind.SEE_ALSO) { views.seeAlso(graphics,line.target(),x,y);return; }
        String face=line.kind()==BookPaginator.Kind.MARGIN&&visual.font("hand")!=null?"hand":"body";
        if(line.kind()==BookPaginator.Kind.MARGIN)x+=26;
        if(line.label()) { canvas.text(graphics,canvas.ellipsis(line.sourceText(),"label",geometry.textWidth()),"label","inkMuted",x,y);return; }
        for(var run:line.runs()) {
            int width=canvas.width(run,face);
            if(!run.readable(session.revealAccess())) canvas.wash(graphics,x,y,width,8,1);
            else {
                boolean link=run.target()!=null&&BookPlaceboReloadListener.INSTANCE.findTarget(run.target()).isPresent();
                int color=visual.color(link?"link":face.equals("hand")?"hand":"ink");
                graphics.drawString(font,canvas.run(run,face),x,y,color,false);
                if(link) graphics.fill(x,y+8,x+width,y+9,visual.color("linkLine"));
                if(Set.of(BookReaderSession.View.ENTRY,BookReaderSession.View.COVER,BookReaderSession.View.CHAPTER).contains(session.view().view())) {
                    float wash=0;
                    for(int level:run.levels())wash=Math.max(wash,revealWash(revealSource,revealVersion,level));
                    canvas.wash(graphics,x,y,width,8,wash);
                }
            }
            x+=width;
        }
    }

    private String revealKey(PageTemplate page,int level) {return page.getId()+"/"+session.version(page)+"/"+level;}
    private float revealWash(PageTemplate page,int level) {
        return revealWash(page.getId(),session.version(page),level);
    }
    private float revealWash(ResourceLocation source,String version,int level) {
        if(level<=0||turn.active())return 0;
        String key=source+"/"+version+"/"+level;
        if(session.saved().markRevealViewed(source,version,level)) {
            revealAges.put(key,0); BookClientHooks.revealed(book.getResourceLocation(),source); sound("unlock");
        }
        int duration=visual.motion().revealTicks();
        return duration==0?0:Math.max(0,1-revealAges.getOrDefault(key,duration)/(float)duration);
    }
    private void toggleBookmark() {session.toggleBookmark();sound("bookmark");}
    private void sound(String role) {
        if(visual==null)return;
        var sound=visual.sounds().get(role);
        if(sound!=null) Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvent.createVariableRangeEvent(sound.id()),sound.pitch()));
    }

    private BookPageRenderContext customContext(GuiGraphics graphics,PageTemplate page,int mouseX,int mouseY,float partialTick) {
        return customContext(graphics,page,mouseX,mouseY,partialTick,0,bodyOffset,bodyHeight,customScroll);
    }
    private BookPageRenderContext customContext(GuiGraphics graphics,PageTemplate page,int mouseX,int mouseY,float partialTick,
                                                int side,int offset,int height,int scroll) {
        var first=leaf(side);
        var angles=pairedAngles.get(page.getId());
        return new BookPageRenderContext(graphics,font,page,book,visual,new BookPageRenderContext.Bounds(first.x()+14,first.y()+offset,geometry.textWidth(),height),
                mouseX,mouseY,partialTick,angles==null?dragLeftRight:angles[0],angles==null?dragUpDown:angles[1],scroll,session::open);
    }

    @Override public boolean keyPressed(int key,int scan,int modifiers) {
        consumedShortcut=0;
        if(key==GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        if(turn.active()) return true;
        if(searchBox!=null && searchBox.isFocused() && key!=GLFW.GLFW_KEY_TAB) {searchBox.keyPressed(key,scan,modifiers);return true;}
        if(key==GLFW.GLFW_KEY_A||key==GLFW.GLFW_KEY_LEFT) { if(key==GLFW.GLFW_KEY_A)consumedShortcut='a';startTurn(-1);return true; }
        if(key==GLFW.GLFW_KEY_D||key==GLFW.GLFW_KEY_RIGHT) { if(key==GLFW.GLFW_KEY_D)consumedShortcut='d';startTurn(1);return true; }
        if(key==GLFW.GLFW_KEY_B) {consumedShortcut='b';toggleBookmark();rebuild();return true;}
        if(key==GLFW.GLFW_KEY_BACKSPACE) {session.back();rebuild();return true;}
        if(key==GLFW.GLFW_KEY_SLASH) {session.search("");rebuild();if(searchBox!=null)setFocused(searchBox);return true;}
        return super.keyPressed(key,scan,modifiers);
    }
    @Override public boolean charTyped(char character,int modifiers) {
        if(Character.toLowerCase(character)==consumedShortcut) {consumedShortcut=0;return true;}
        if(turn.active()) return true;
        if(searchBox!=null && searchBox.isFocused()) return searchBox.charTyped(character,modifiers);
        if(!Character.isISOControl(character) && character!='/') {session.search(String.valueOf(character));rebuild();if(searchBox!=null)setFocused(searchBox);return true;}
        return false;
    }
    @Override public boolean mouseClicked(double x,double y,int button) {
        if(turn.active()) return true;
        if(hasShiftDown()&&hoveredTarget!=null) {pinnedTarget=hoveredTarget;return true;}
        pinnedTarget=null;
        for(var pane:pairedEntries) if(pane.renderer()!=null) {
            var context=paneContext(pane,(int)x,(int)y);
            if(context.body().contains(x,y)&&pane.renderer().mouseClicked(context,x,y,button))return true;
        }
        if(bodyRenderer!=null && pageTemplate instanceof PageTemplate page && session.content(page).readable(session.revealAccess())) {
            var context=customContext(null,page,(int)x,(int)y,0);
            if(context.body().contains(x,y)&&bodyRenderer.mouseClicked(context,x,y,button)) return true;
        }
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical) {
        if(turn.active()) return true;
        for(var pane:pairedEntries) if(pane.renderer()!=null) {
            var context=paneContext(pane,(int)x,(int)y);
            if(context.body().contains(x,y)) {
                int scroll=pairedScroll.getOrDefault(pane.page().getId(),0);
                pairedScroll.put(pane.page().getId(),Math.clamp(scroll-(int)(vertical*12),0,Math.max(0,pane.renderer().contentHeight(context)-pane.height())));
                return true;
            }
        }
        if(bodyRenderer!=null && pageTemplate instanceof PageTemplate page) {
            var context=customContext(null,page,(int)x,(int)y,0);
            customScroll=Math.clamp(customScroll-(int)(vertical*12),0,Math.max(0,bodyRenderer.contentHeight(context)-bodyHeight));
            return true;
        }
        if(geometry.compact() && session.view().view()==BookReaderSession.View.ENTRY) {
            compactScroll=Math.clamp(compactScroll-(int)(vertical*12),0,Math.max(0,geometry.leafHeight()-geometry.viewportHeight()));
            rebuild();return true;
        }
        views.scroll(vertical);return true;
    }
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy) {
        if(turn.active()) return true;
        for(var pane:pairedEntries) if(pane.renderer()!=null) {
            var context=paneContext(pane,(int)x,(int)y);
            if(context.body().contains(x,y)) {
                if(!pane.renderer().mouseDragged(context,x,y,button,dx,dy)) {
                    var angles=pairedAngles.computeIfAbsent(pane.page().getId(),id->new double[2]);
                    angles[0]+=dx/2;angles[1]-=dy/2;
                }
                return true;
            }
        }
        if(bodyRenderer!=null && pageTemplate instanceof PageTemplate page) {
            var context=customContext(null,page,(int)x,(int)y,0);
            if(context.body().contains(x,y)) {
                if(!bodyRenderer.mouseDragged(context,x,y,button,dx,dy)) {dragLeftRight+=dx/2;dragUpDown-=dy/2;}
                return true;
            }
        }
        return false;
    }
    @Override public boolean isPauseScreen() {return false;}
    private BookPageRenderContext paneContext(EntryPane pane,int x,int y) {
        return customContext(null,pane.page(),x,y,0,pane.side(),pane.offset(),pane.height(),pairedScroll.getOrDefault(pane.page().getId(),0));
    }
    @Override public void onClose() {sound("close");turn.cancel();BookReadTracker.flush();Minecraft.getInstance().setScreen(null);}
    @Override public void removed() {BookReadTracker.flush();}

    static String tr(String key) {return I18n.get("hutoslib.book."+key);}
    private int tabColor(ChapterTemplate chapter) {
        try {
            String hex=chapter.getPresentation().tabColor();
            if(!hex.isBlank()) return 0xFF000000|Integer.parseInt(hex.replace("#",""),16);
            String[] color=chapter.getColor().split(",");
            return 0xFF000000|Math.clamp(Math.round(Float.parseFloat(color[0])*255),0,255)<<16
                    |Math.clamp(Math.round(Float.parseFloat(color[1])*255),0,255)<<8|Math.clamp(Math.round(Float.parseFloat(color[2])*255),0,255);
        } catch(RuntimeException ignored) {return visual.color("accent");}
    }
    private int contrastLabel(int color) {return BookColors.contrast(color,visual.color("paper"))>BookColors.contrast(color,visual.color("ink"))?visual.color("paper"):visual.color("ink");}
}
