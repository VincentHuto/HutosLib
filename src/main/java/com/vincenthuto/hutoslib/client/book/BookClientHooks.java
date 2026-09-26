package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.client.screen.guide.BookReaderScreen;
import com.vincenthuto.hutoslib.common.book.BookNoticeStyle;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.*;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import java.util.*;

/** Shared client hook for default and mod-owned knowledge packets. */
public final class BookClientHooks {
    private record Key(UUID player,ResourceLocation book) {}
    private enum Event { DISCOVERY, FORGOTTEN, REVEAL }
    private record NoticeKey(ResourceLocation book,Event event) {}
    private static final class Pending {
        final Set<ResourceLocation> entries=new HashSet<>();
        int ticks=5;
    }
    private record Attention(long tick,long data,long resources,int count) {}
    private static final Map<Key,BookNotificationState> BASELINES=new HashMap<>();
    private static final Set<IBookKnowledge> KNOWLEDGE_BASELINES=Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Map<NoticeKey,Pending> PENDING=new LinkedHashMap<>();
    private static final Map<ItemGuideBook,Attention> ATTENTION=new IdentityHashMap<>();
    private static final BookContentCache CONTENT=new BookContentCache();
    private BookClientHooks() {}

    public static void knowledgeSnapshot(Player player,IBookKnowledge knowledge,Set<ResourceLocation> before,
                                         Set<ResourceLocation> after,Set<ResourceLocation> explicit) {
        boolean initial=KNOWLEDGE_BASELINES.add(knowledge);
        Set<ResourceLocation> books=new HashSet<>();
        for(var item:BuiltInRegistries.ITEM) if(item instanceof ItemGuideBook guide
                && guide.getKnowledgeProvider().apply(player).orElse(null)==knowledge) guide.resolveBookId().ifPresent(books::add);
        for(ResourceLocation entry:after) {
            if(!entry.getPath().contains("/pages/"))continue;
            int slash=entry.getPath().indexOf('/');
            books.add(ResourceLocation.fromNamespaceAndPath(entry.getNamespace(),entry.getPath().substring(0,slash)));
        }
        for(ResourceLocation id:books) knowledgeChanged(player.getUUID(),id,before,after,explicit,initial);
        BookReaderScreen.refreshIfOpen();
    }

    public static void knowledgeChanged(UUID playerId,ResourceLocation bookId,Set<ResourceLocation> before,
                                       Set<ResourceLocation> after,Set<ResourceLocation> explicit,boolean initialSync) {
        BookNotificationState state=BASELINES.computeIfAbsent(new Key(playerId,bookId),ignored->new BookNotificationState());
        Set<ResourceLocation> discoveries=state.knowledge(scoped(bookId,before),scoped(bookId,after),scoped(bookId,explicit),initialSync);
        BookReadTracker.unacknowledge(playerId,discoveries);
        ATTENTION.clear();
        Player player=Minecraft.getInstance().player;
        if(player!=null&&player.getUUID().equals(playerId)) {
            Map<ResourceLocation,Set<Integer>> readable=readable(player,bookId);
            Set<ResourceLocation> shown=new HashSet<>(discoveries);shown.retainAll(readable.keySet());
            queue(bookId,Event.DISCOVERY,shown);
            Set<ResourceLocation> forgotten=state.readability(readable,BookPlaceboReloadListener.INSTANCE.getGeneration());
            if(!initialSync)queue(bookId,Event.FORGOTTEN,forgotten);
        }
        BookReaderScreen.refreshIfOpen();
    }

    public static void playerStateChanged(Player player) {
        ATTENTION.clear();
        for(var entry:BASELINES.entrySet()) if(entry.getKey().player().equals(player.getUUID())) {
            Set<ResourceLocation> lost=entry.getValue().readability(readable(player,entry.getKey().book()),BookPlaceboReloadListener.INSTANCE.getGeneration());
            queue(entry.getKey().book(),Event.FORGOTTEN,lost);
        }
        BookReaderScreen.refreshIfOpen();
    }

    public static void revealed(ResourceLocation bookId,ResourceLocation entryId) {queue(bookId,Event.REVEAL,Set.of(entryId));}

    public static int attention(Player player,ItemGuideBook item) {
        long tick=player.level().getGameTime(),data=BookPlaceboReloadListener.INSTANCE.getGeneration(),resources=BookThemeManager.INSTANCE.generation();
        Attention cached=ATTENTION.get(item);
        if(cached!=null&&cached.tick()==tick&&cached.data()==data&&cached.resources()==resources)return cached.count();
        var id=item.resolveBookId();
        int count=0;
        if(id.isPresent()) {
            var raw=BookPlaceboReloadListener.INSTANCE.getBookByTitle(id.get());
            if(raw==null)return 0;
            var filtered=item.applyVisibilityFilters(raw,player);
            for(var chapter:filtered.getChapters())for(var template:chapter.getPages())if(template instanceof PageTemplate page) {
                var content=CONTENT.get(page,chapter.getId());
                if(!content.readable(level->filtered.canReveal(player,level)))continue;
                String version=filtered.getSourceIndex().entry(page.getId()).map(BookSourceIndex.Entry::version).orElseGet(()->BookSourceIndex.contentVersion(page));
                if(BookReadTracker.state(player.getUUID()).status(page.getId(),version,content.revealedLevels(level->filtered.canReveal(player,level)))!=BookReaderState.EntryStatus.READ)count++;
            }
        }
        ATTENTION.put(item,new Attention(tick,data,resources,count));
        return count;
    }

    public static void tick() {
        var mc=Minecraft.getInstance();
        if(mc.player==null)return;
        Iterator<Map.Entry<NoticeKey,Pending>> iterator=PENDING.entrySet().iterator();
        while(iterator.hasNext()) {
            var entry=iterator.next();
            if(--entry.getValue().ticks>0)continue;
            var book=BookPlaceboReloadListener.INSTANCE.getBookByTitle(entry.getKey().book());
            if(book!=null) {
                BookNoticeStyle style=guide(entry.getKey().book()).map(ItemGuideBook::getNoticeStyle).orElse(BookNoticeStyle.DEFAULT);
                String key=switch(entry.getKey().event()){case DISCOVERY->style.discovery();case FORGOTTEN->style.forgotten();case REVEAL->style.revealed();};
                mc.getToasts().addToast(new BookNoticeToast(book,Component.translatable(key),entry.getValue().entries.size()));
            }
            iterator.remove();
        }
    }

    public static void disconnect() {BASELINES.clear();KNOWLEDGE_BASELINES.clear();PENDING.clear();ATTENTION.clear();}

    private static void queue(ResourceLocation book,Event event,Set<ResourceLocation> entries) {
        if(entries.isEmpty())return;
        PENDING.computeIfAbsent(new NoticeKey(book,event),ignored->new Pending()).entries.addAll(entries);
    }
    private static Set<ResourceLocation> scoped(ResourceLocation book,Set<ResourceLocation> entries) {
        Set<ResourceLocation> result=new HashSet<>();
        for(var id:entries)if(id.getNamespace().equals(book.getNamespace())&&id.getPath().startsWith(book.getPath()+"/"))result.add(id);
        return result;
    }
    private static Optional<ItemGuideBook> guide(ResourceLocation book) {
        for(var item:BuiltInRegistries.ITEM)if(item instanceof ItemGuideBook guide&&guide.resolveBookId().filter(book::equals).isPresent())return Optional.of(guide);
        return Optional.empty();
    }
    private static Map<ResourceLocation,Set<Integer>> readable(Player player,ResourceLocation bookId) {
        Map<ResourceLocation,Set<Integer>> result=new HashMap<>();
        var item=guide(bookId);var raw=BookPlaceboReloadListener.INSTANCE.getBookByTitle(bookId);
        if(item.isEmpty()||raw==null)return result;
        var book=item.get().applyVisibilityFilters(raw,player);
        for(var chapter:book.getChapters())for(var template:chapter.getPages())if(template instanceof PageTemplate page) {
            var content=CONTENT.get(page,chapter.getId());
            if(!content.readable(level->book.canReveal(player,level)))continue;
            Set<Integer> levels=new HashSet<>(content.revealedLevels(level->book.canReveal(player,level)));levels.add(0);
            result.put(page.getId(),Set.copyOf(levels));
        }
        return result;
    }
}
