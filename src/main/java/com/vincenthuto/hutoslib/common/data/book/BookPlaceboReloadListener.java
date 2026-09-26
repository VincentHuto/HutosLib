package com.vincenthuto.hutoslib.common.data.book;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.shadow.PlaceboJsonReloadListener;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class BookPlaceboReloadListener extends PlaceboJsonReloadListener<BookDataTemplate> {

	public static final BookPlaceboReloadListener INSTANCE = new BookPlaceboReloadListener();
	private Map<ResourceLocation, BookDataTemplate> byType = ImmutableMap.of();
	public List<BookCodeModel> books = ImmutableList.of();
	private Map<ResourceLocation, Target> targets = Map.of();
	private long generation;

	public record Target(ResourceLocation bookId, ResourceLocation chapterId, BookDataTemplate template) {}
	public Optional<Target> findTarget(ResourceLocation id) { return Optional.ofNullable(targets.get(id)); }
	public long getGeneration() { return generation; }

	public Optional<ResourceLocation> resolveBookId(ResourceLocation exactId, String legacyPrefix) {
		if (exactId != null) return books.stream().map(BookCodeModel::getResourceLocation).filter(exactId::equals).findFirst();
		if (legacyPrefix == null || legacyPrefix.isBlank()) return Optional.empty();
		List<ResourceLocation> matches = books.stream().filter(book -> book.getEntryPrefix().equals(legacyPrefix))
				.map(BookCodeModel::getResourceLocation).toList();
		return matches.size() == 1 ? Optional.of(matches.getFirst()) : Optional.empty();
	}

	public BookPlaceboReloadListener() {
		super(org.apache.logging.log4j.LogManager.getLogger(BookPlaceboReloadListener.class), "books", true, true);
	}
	
	public BookCodeModel getBookByTitle(ResourceLocation rl) {
		Optional<BookCodeModel> optional = this.books.parallelStream().filter(p -> p.getResourceLocation().equals(rl))
				.findFirst();
		return optional.isPresent() ? optional.get() : null;
	}

	@Override
	protected void onReload() {
		super.onReload();
		ImmutableMap.Builder<ResourceLocation, BookDataTemplate> builder = ImmutableMap.builder();
		this.registry.values().forEach(a -> {
			builder.put(a.id, a);
		});
		this.byType = builder.build();
		this.bindBooks(this.byType);
	}

	@Override
	protected void registerBuiltinSerializers() {
		this.registerSerializer(ResourceLocation.fromNamespaceAndPath(HutosLib.MOD_ID, "book"), BookTemplate.SERIALIZER);
		this.registerSerializer(ResourceLocation.fromNamespaceAndPath(HutosLib.MOD_ID, "chapter"), ChapterTemplate.SERIALIZER);
		this.registerSerializer(ResourceLocation.fromNamespaceAndPath(HutosLib.MOD_ID, "page"), PageTemplate.SERIALIZER);
		this.registerSerializer(ResourceLocation.fromNamespaceAndPath(HutosLib.MOD_ID, "craftingrecipe"), CraftingRecipeTemplate.SERIALIZER);
		this.registerSerializer(ResourceLocation.fromNamespaceAndPath(HutosLib.MOD_ID, "glossary_term"), GlossaryTermTemplate.SERIALIZER);

	}

	public Map<ResourceLocation, BookDataTemplate> getTypeMap() {
		return this.byType;
	}

	public List<BookCodeModel> getBooks() {
		return this.books;
	}

	protected String resourceName() {
		return "books";
	}

    public void bindBooks(Map<ResourceLocation, BookDataTemplate> definitions) {
        Map<ResourceLocation, BookTemplate> bookTemplates = new TreeMap<>();
        Map<ResourceLocation, List<ChapterTemplate>> chaptersByBook = new HashMap<>();
        Map<ResourceLocation, List<BookDataTemplate>> pagesByChapter = new HashMap<>();
        Map<ResourceLocation, List<GlossaryTermTemplate>> glossaryByBook = new HashMap<>();

        definitions.forEach((id, template) -> {
            String[] path = id.getPath().split("/");
            ResourceLocation bookId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path[0]);
            if (template instanceof BookTemplate bookTemplate && path.length == 2 && path[1].equals("book")) {
                bookTemplates.put(bookId, bookTemplate);
            } else if (template instanceof ChapterTemplate chapter && path.length == 3 && path[2].equals("chapter")) {
                chaptersByBook.computeIfAbsent(bookId, ignored -> new ArrayList<>()).add(chapter);
            } else if (template instanceof PageTemplate && path.length == 4 && path[2].equals("pages")) {
                ResourceLocation chapterId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                        path[0] + "/" + path[1] + "/chapter");
                pagesByChapter.computeIfAbsent(chapterId, ignored -> new ArrayList<>()).add(template);
            } else if (template instanceof GlossaryTermTemplate term && path.length == 3
                    && path[1].equals("glossary") && term.getBookId().equals(bookId)) {
                glossaryByBook.computeIfAbsent(bookId, ignored -> new ArrayList<>()).add(term);
            } else {
                logger.warn("Ignoring book resource {}: type does not match its canonical path", id);
            }
        });

        Comparator<BookDataTemplate> order = Comparator.comparingInt(BookDataTemplate::getOrdinality)
                .thenComparing(BookDataTemplate::getId);
        List<BookCodeModel> bound = new ArrayList<>();
        bookTemplates.forEach((id, template) -> {
            BookCodeModel book = new BookCodeModel(id, template);
            List<ChapterTemplate> chapters = new ArrayList<>();
            for (ChapterTemplate chapter : chaptersByBook.getOrDefault(id, List.of())) {
                List<BookDataTemplate> pages = new ArrayList<>(pagesByChapter.getOrDefault(chapter.getId(), List.of()));
                pages.sort(order);
                chapters.add(chapter.copyWithPages(pages));
            }
            chapters.sort(order);
            book.setChapters(chapters);
            book.setSourceIndex(BookSourceIndex.create(chapters));
            book.setGlossary(glossaryByBook.getOrDefault(id, List.of()).stream()
                    .sorted(Comparator.comparing(GlossaryTermTemplate::getId)).toList());
            bound.add(book);
        });
        Map<ResourceLocation, Target> resolved = new HashMap<>();
        for (BookCodeModel book : bound) {
            for (ChapterTemplate chapter : book.getChapters()) {
                resolved.put(chapter.getId(), new Target(book.getResourceLocation(), chapter.getId(), chapter));
                for (BookDataTemplate page : chapter.getPages())
                    resolved.put(page.getId(), new Target(book.getResourceLocation(), chapter.getId(), page));
            }
        }
        this.targets = Map.copyOf(resolved);
        this.books = List.copyOf(bound);
        this.generation++;
        validateLinks(bound);
        logger.info("{} Books bound", books.size());
    }

    private void validateLinks(List<BookCodeModel> bound) {
        for (BookCodeModel book : bound) {
            validateText(book.getTemplate().getId(), "text", com.vincenthuto.hutoslib.common.book.BookText.parse(book.getTemplate().getText()));
            for (GlossaryTermTemplate term : book.getGlossary()) {
                term.getReferences().forEach(target -> validateLink(term.getId(), "refs", target));
            }
            for (ChapterTemplate chapter : book.getChapters()) {
                validateText(chapter.getId(), "text", com.vincenthuto.hutoslib.common.book.BookText.parse(chapter.getPresentation().text()));
                validateText(chapter.getId(), "margin", com.vincenthuto.hutoslib.common.book.BookText.parse(chapter.getPresentation().margin()));
                chapter.getPresentation().callout().ifPresent(callout -> validateText(chapter.getId(), "callout",
                        com.vincenthuto.hutoslib.common.book.BookText.parse(callout.text())));
                for (BookDataTemplate template : chapter.getPages()) {
                    if (template instanceof PageTemplate page) {
                        book.getSourceIndex().entry(page.getId()).orElseThrow().blocks()
                                .forEach((field, text) -> validateText(page.getId(), field, text));
                        page.getPresentation().seeAlso().forEach(target -> validateLink(page.getId(), "seeAlso", target));
                        if (page.getPresentation().revealLevel() < 0)
                            logger.warn("Invalid revealLevel in {}; this entry will remain concealed", page.getId());
                    }
                }
            }
        }
    }

    private void validateText(ResourceLocation source, String field, com.vincenthuto.hutoslib.common.book.BookText text) {
        text.diagnostics().forEach(message -> logger.warn("Book markup {} [{}]: {}", source, field, message));
        text.runs().stream().map(com.vincenthuto.hutoslib.common.book.BookText.Run::target).filter(Objects::nonNull)
                .distinct().forEach(target -> validateLink(source, field, target));
    }

    private void validateLink(ResourceLocation source, String field, ResourceLocation target) {
        if (!targets.containsKey(target)) logger.warn("Unresolved book link {} [{}] -> {}", source, field, target);
    }
}
