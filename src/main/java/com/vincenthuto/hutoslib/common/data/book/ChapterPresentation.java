package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/** Immutable presentation data shared by filtered chapter views. */
public record ChapterPresentation(String tabColor, String layout, String text, String margin,
                                  int rowLimit, Optional<Callout> callout) {
    public static final ChapterPresentation DEFAULT = new ChapterPresentation("", "chapter", "", "", 0, Optional.empty());

    public static final MapCodec<ChapterPresentation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("tabColor", "").forGetter(ChapterPresentation::tabColor),
            Codec.STRING.optionalFieldOf("layout", "chapter").forGetter(ChapterPresentation::layout),
            Codec.STRING.optionalFieldOf("text", "").forGetter(ChapterPresentation::text),
            Codec.STRING.optionalFieldOf("margin", "").forGetter(ChapterPresentation::margin),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("rowLimit", 0).forGetter(ChapterPresentation::rowLimit),
            Callout.CODEC.optionalFieldOf("callout").forGetter(ChapterPresentation::callout)
    ).apply(instance, ChapterPresentation::new));

    public record Callout(String icon, String text) {
        public static final Codec<Callout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("icon").forGetter(Callout::icon),
                Codec.STRING.fieldOf("text").forGetter(Callout::text)
        ).apply(instance, Callout::new));
    }
}
