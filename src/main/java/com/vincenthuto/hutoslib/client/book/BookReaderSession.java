package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.ReaderLocation;
import com.vincenthuto.hutoslib.common.data.book.*;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import java.util.*;
import java.util.function.IntPredicate;

/** One navigation and access lifetime across every reader view and cross-book link. */
public final class BookReaderSession {
    public enum View { COVER, CONTENTS, CHAPTER, ENTRY, PREVIEW, SEARCH, GLOSSARY, BOOKMARKS }
    public record Snapshot(ResourceLocation bookId, View view, ResourceLocation chapterId, ReaderLocation location,
                           String query, Set<ResourceLocation> chapterFilters, String initial,
                           ResourceLocation selection, int listPage) {
        public Snapshot { chapterFilters = Set.copyOf(chapterFilters); }
    }
    public record Target(BookAccessContext access, BookCodeModel book, ChapterTemplate chapter, PageTemplate page) {}

    private final Player player;
    private final BookReaderState saved;
    private final BookContentCache contentCache = new BookContentCache();
    private final Map<ResourceLocation, BookAccessContext> contexts = new HashMap<>();
    private final Deque<Snapshot> history = new ArrayDeque<>();
    private BookAccessContext access;
    private BookCodeModel book;
    private Snapshot view;
    private Map<ResourceLocation, PageTemplate> entries = Map.of();
    private Map<ResourceLocation, ChapterTemplate> chapters = Map.of();
    private String accessSignature = "";
    private long dataGeneration = -1, resourceGeneration = -1, revision;
    private int readTicks;
    private boolean available;
    private boolean lastViewRequested;

    public BookReaderSession(Player player, BookAccessContext access) {
        this.player = player;
        this.access = access;
        saved = BookReadTracker.state(player.getUUID());
        contexts.put(access.bookId(), access);
        view = new Snapshot(access.bookId(), View.COVER, null, null, "", Set.of(), "", null, 0);
        refresh();
    }

    public Player player() { return player; }
    public BookCodeModel book() { return book; }
    public Snapshot view() { return view; }
    public BookReaderState saved() { return saved; }
    public long revision() { return revision; }
    public boolean available() { return available; }
    public IntPredicate revealAccess() { return level -> book != null && book.canReveal(player, level); }
    public Optional<PageTemplate> entry(ResourceLocation id) { return Optional.ofNullable(entries.get(id)); }
    public Optional<ChapterTemplate> chapter(ResourceLocation id) { return Optional.ofNullable(chapters.get(id)); }

    public BookEntryContent content(PageTemplate page) {
        ResourceLocation chapter = book.getSourceIndex().entry(page.getId()).map(BookSourceIndex.Entry::chapterId)
                .orElseGet(() -> chapters.values().stream().filter(value -> value.getPages().contains(page)).findFirst().orElseThrow().getId());
        return contentCache.get(page, chapter);
    }

    public List<BookEntryContent> visibleContent() {
        return entries.values().stream().map(this::content).toList();
    }

    public BookReaderState.EntryStatus status(PageTemplate page) {
        return saved.status(page.getId(), version(page), content(page).revealedLevels(revealAccess()));
    }

    public String version(PageTemplate page) {
        return book.getSourceIndex().entry(page.getId()).map(BookSourceIndex.Entry::version).orElseGet(() -> BookSourceIndex.contentVersion(page));
    }

    public boolean refresh() {
        Optional<BookCodeModel> current = access.current(player);
        if (current.isEmpty()) {
            boolean changed = available;
            available = false;
            readTicks = 0;
            if (changed) revision++;
            return changed;
        }
        available = true;
        book = current.get();
        Map<ResourceLocation, ChapterTemplate> newChapters = new LinkedHashMap<>();
        Map<ResourceLocation, PageTemplate> newEntries = new LinkedHashMap<>();
        for (ChapterTemplate chapter : book.getChapters()) {
            newChapters.put(chapter.getId(), chapter);
            for (BookDataTemplate page : chapter.getPages()) if (page instanceof PageTemplate entry) newEntries.put(entry.getId(), entry);
        }
        chapters = Collections.unmodifiableMap(newChapters);
        entries = Collections.unmodifiableMap(newEntries);
        StringBuilder signature = new StringBuilder(book.getResourceLocation().toString());
        chapters.keySet().forEach(signature::append);
        for (PageTemplate page : entries.values()) {
            BookEntryContent content = content(page);
            signature.append(page.getId()).append(content.readable(revealAccess()))
                    .append(new TreeSet<>(content.revealedLevels(revealAccess())));
        }
        Set<Integer> surfaceLevels = new TreeSet<>();
        List<String> surfaces = new ArrayList<>();
        surfaces.add(book.getTemplate().getText());
        for (ChapterTemplate chapter : chapters.values()) {
            var presentation = chapter.getPresentation();
            surfaces.add(presentation.text());
            surfaces.add(presentation.margin());
            presentation.callout().ifPresent(callout -> surfaces.add(callout.text()));
        }
        for (String surface : surfaces) contentCache.text(surface).runs().forEach(run -> surfaceLevels.addAll(run.levels()));
        for (int level : surfaceLevels) signature.append('/').append(level).append(revealAccess().test(level));
        signature.append(book.getReaderHooks().owner().apply(player).getString())
                .append(book.getReaderHooks().status().apply(player).getString());
        long data = BookPlaceboReloadListener.INSTANCE.getGeneration(), resources = BookThemeManager.INSTANCE.generation();
        boolean changed = !signature.toString().equals(accessSignature) || data != dataGeneration || resources != resourceGeneration;
        accessSignature = signature.toString();
        dataGeneration = data;
        resourceGeneration = resources;
        if (view.location() != null && !entries.containsKey(view.location().entryId())) {
            view = snapshot(chapters.containsKey(view.chapterId()) ? View.CHAPTER : View.COVER, view.chapterId(), null, null);
            changed = true;
        } else if (view.chapterId() != null && !chapters.containsKey(view.chapterId())) {
            view = snapshot(View.COVER, null, null, null);
            changed = true;
        }
        if (changed) { revision++; readTicks = 0; }
        return changed;
    }

    public void show(View next, ResourceLocation chapter) {
        if (chapter != null && !chapters.containsKey(chapter)) return;
        navigate(snapshot(next, chapter, null, null), true);
    }

    public void search(String query) {
        Snapshot next = new Snapshot(book.getResourceLocation(), View.SEARCH, null, null, query,
                view.view() == View.SEARCH ? view.chapterFilters() : Set.of(), view.view() == View.SEARCH ? view.initial() : "", null, 0);
        navigate(next, view.view() != View.SEARCH);
    }

    public void searchFilters(Set<ResourceLocation> chapterFilters, String initial) {
        navigate(new Snapshot(view.bookId(), view.view(), null, null, view.query(), chapterFilters, initial, null, 0), false);
    }

    public void listPage(int page) {
        navigate(new Snapshot(view.bookId(), view.view(), view.chapterId(), view.location(), view.query(), view.chapterFilters(),
                view.initial(), view.selection(), Math.max(0, page)), false);
    }

    public void select(ResourceLocation entry) {
        if (!entries.containsKey(entry)) return;
        navigate(new Snapshot(view.bookId(), view.view(), view.chapterId(), view.location(), view.query(), view.chapterFilters(),
                view.initial(), entry, view.listPage()), false);
    }

    public List<BookSearch.Result> searchResults() {
        return BookSearch.query(visibleContent().stream()
                .filter(content -> view.chapterFilters().isEmpty() || view.chapterFilters().contains(content.chapterId())).toList(),
                view.query(), revealAccess()).stream()
                .filter(result -> view.initial().isEmpty() || (!result.concealed() && BookSearch.normalize(result.title()).startsWith(view.initial())))
                .toList();
    }

    public Optional<Target> target(ResourceLocation id) {
        Optional<BookPlaceboReloadListener.Target> definition = BookPlaceboReloadListener.INSTANCE.findTarget(id);
        if (definition.isEmpty()) return Optional.empty();
        ResourceLocation destinationId = definition.get().bookId();
        BookAccessContext destination = contexts.get(destinationId);
        if (!destinationId.equals(view.bookId())) {
            Optional<ItemGuideBook> carried = BookAccessContext.carriedItem(player, destinationId);
            if (carried.isEmpty()) return Optional.empty();
            BookCodeModel raw = BookPlaceboReloadListener.INSTANCE.getBookByTitle(destinationId);
            destination = new BookAccessContext(raw, carried.get(), null, null);
        }
        if (destination == null) return Optional.empty();
        Optional<BookCodeModel> model = destination.current(player);
        if (model.isEmpty()) return Optional.empty();
        for (ChapterTemplate chapter : model.get().getChapters()) {
            if (chapter.getId().equals(id)) return Optional.of(new Target(destination, model.get(), chapter, null));
            for (BookDataTemplate page : chapter.getPages())
                if (page.getId().equals(id) && page instanceof PageTemplate entry)
                    return Optional.of(new Target(destination, model.get(), chapter, entry));
        }
        return Optional.empty();
    }

    public boolean open(ResourceLocation id) { return open(id, null, false); }

    public boolean open(ResourceLocation id, ReaderLocation location, boolean lastLeaf) {
        Optional<Target> resolved = target(id);
        if (resolved.isEmpty()) return false;
        Target target = resolved.get();
        if (target.page() != null && !target.book().canReveal(player, target.page().getPresentation().revealLevel())) return false;
        Snapshot previous = view;
        access = target.access();
        contexts.put(access.bookId(), access);
        view = new Snapshot(access.bookId(), View.COVER, null, null, "", Set.of(), "", null, 0);
        refresh();
        ReaderLocation anchor = location;
        if (target.page() != null && anchor == null) {
            var blocks = content(target.page()).blocks();
            String block = blocks.keySet().stream().findFirst().orElse("body");
            int offset = 0;
            anchor = new ReaderLocation(access.bookId(), id, block, offset);
        }
        history.push(previous);
        navigate(snapshot(target.page() == null ? View.CHAPTER : View.ENTRY, target.chapter().getId(), anchor, null), false);
        lastViewRequested = lastLeaf && target.page() != null;
        return true;
    }

    public boolean consumeLastViewRequest() {
        boolean requested = lastViewRequested;
        lastViewRequested = false;
        return requested;
    }

    public void anchor(ReaderLocation location) {
        if (view.view() != View.ENTRY || !entries.containsKey(location.entryId())) return;
        view = new Snapshot(view.bookId(), view.view(), view.chapterId(), location, view.query(), view.chapterFilters(), view.initial(), view.selection(), view.listPage());
        saved.resume(location);
    }

    public boolean resume() {
        return saved.resume(view.bookId()).map(location -> open(location.entryId(), location, false)).orElse(false);
    }

    public void adjacent(int direction) {
        adjacent(direction, view.location() == null ? null : view.location().entryId());
    }

    public void adjacent(int direction, ResourceLocation fromEntry) {
        if (view.view() != View.ENTRY || view.location() == null) return;
        ChapterTemplate chapter = chapters.get(view.chapterId());
        List<PageTemplate> readable = chapter.getPages().stream().filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast)
                .filter(page -> content(page).readable(revealAccess())).toList();
        int current = -1;
        for (int i = 0; i < readable.size(); i++) if (readable.get(i).getId().equals(fromEntry)) current = i;
        int next = current + direction;
        if (current >= 0 && next >= 0 && next < readable.size()) open(readable.get(next).getId(), null, direction < 0);
        else show(View.CHAPTER, chapter.getId());
    }

    public boolean back() {
        while (!history.isEmpty()) {
            Snapshot previous = history.pop();
            BookAccessContext context = contexts.get(previous.bookId());
            if (context == null) continue;
            Optional<BookCodeModel> model = context.current(player);
            if (model.isEmpty()) continue;
            boolean valid = previous.location() == null || model.get().getChapters().stream()
                    .flatMap(chapter -> chapter.getPages().stream()).filter(PageTemplate.class::isInstance).map(PageTemplate.class::cast)
                    .anyMatch(page -> page.getId().equals(previous.location().entryId()) && model.get().canReveal(player, page.getPresentation().revealLevel()));
            if (!valid) continue;
            access = context;
            view = previous;
            refresh();
            revision++;
            readTicks = 0;
            return true;
        }
        return false;
    }

    public void readingTick(boolean actualReadableLeaf) {
        if (!actualReadableLeaf || view.view() != View.ENTRY || view.location() == null) { readTicks = 0; return; }
        PageTemplate page = entries.get(view.location().entryId());
        if (page == null || !content(page).readable(revealAccess())) { readTicks = 0; return; }
        if (++readTicks == 20) saved.markRead(page.getId(), version(page), content(page).revealedLevels(revealAccess()));
    }

    public boolean toggleBookmark() {
        if (view.location() == null) return false;
        PageTemplate page = entries.get(view.location().entryId());
        if (page == null || !content(page).readable(revealAccess())) return false;
        boolean pinned = saved.toggleBookmark(view.location());
        revision++;
        return pinned;
    }

    private Snapshot snapshot(View next, ResourceLocation chapter, ReaderLocation location, ResourceLocation selection) {
        return new Snapshot(access.bookId(), next, chapter, location, "", Set.of(), "", selection, 0);
    }

    private void navigate(Snapshot next, boolean remember) {
        lastViewRequested = false;
        if (remember && !next.equals(view)) history.push(view);
        view = next;
        revision++;
        readTicks = 0;
    }
}
