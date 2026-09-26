package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.common.data.shadow.PSerializer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class GlossaryTermTemplate extends BookDataTemplate {
    public static final Codec<GlossaryTermTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("book").forGetter(GlossaryTermTemplate::getBookId),
            Codec.STRING.fieldOf("term").forGetter(GlossaryTermTemplate::getTerm),
            ResourceLocation.CODEC.listOf().fieldOf("refs").forGetter(GlossaryTermTemplate::getReferences)
    ).apply(instance, GlossaryTermTemplate::new));
    public static final PSerializer<GlossaryTermTemplate> SERIALIZER = PSerializer.fromCodec("glossary_term", CODEC);

    private final ResourceLocation bookId;
    private final String term;
    private final List<ResourceLocation> references;

    public GlossaryTermTemplate(ResourceLocation bookId, String term, List<ResourceLocation> references) {
        super(0);
        this.bookId = bookId;
        this.term = term;
        this.references = List.copyOf(references);
    }

    public ResourceLocation getBookId() { return bookId; }
    public String getTerm() { return term; }
    public List<ResourceLocation> getReferences() { return references; }

    @Override
    public void setChapter(String chapterName) { }

    @Override
    public void getPageScreen(int pageNum, BookCodeModel book, ChapterTemplate chapter) {
        book.getTemplate().getPageScreen(pageNum, book, chapter);
    }

    @Override
    public PSerializer<? extends BookDataTemplate> getSerializer() { return SERIALIZER; }
}
