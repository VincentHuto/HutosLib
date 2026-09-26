package com.vincenthuto.hutoslib.common.data.book;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record PagePresentation(String layout, String margin, Optional<RecordBlock> record,
                               List<ResourceLocation> seeAlso, List<String> footnotes, boolean pinned,
                               int revealLevel, Optional<Either<Boolean, String>> decal) {
    public static final PagePresentation DEFAULT = new PagePresentation("text", "", Optional.empty(),
            List.of(), List.of(), false, 0, Optional.empty());

    private static final Codec<Boolean> NO_DECAL = Codec.BOOL.validate(value -> value
            ? DataResult.error(() -> "decal must be false or a decal name") : DataResult.success(false));

    public static final MapCodec<PagePresentation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("layout", "text").forGetter(PagePresentation::layout),
            Codec.STRING.optionalFieldOf("margin", "").forGetter(PagePresentation::margin),
            RecordBlock.CODEC.optionalFieldOf("record").forGetter(PagePresentation::record),
            ResourceLocation.CODEC.listOf().optionalFieldOf("seeAlso", List.of()).forGetter(PagePresentation::seeAlso),
            Codec.STRING.listOf().optionalFieldOf("footnotes", List.of()).forGetter(PagePresentation::footnotes),
            Codec.BOOL.optionalFieldOf("pinned", false).forGetter(PagePresentation::pinned),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("revealLevel", 0).forGetter(PagePresentation::revealLevel),
            Codec.either(NO_DECAL, Codec.STRING).optionalFieldOf("decal").forGetter(PagePresentation::decal)
    ).apply(instance, PagePresentation::new));

    public PagePresentation {
        seeAlso = List.copyOf(seeAlso);
        footnotes = List.copyOf(footnotes);
    }

    public record RecordBlock(String label, String text) {
        public static final Codec<RecordBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("label").forGetter(RecordBlock::label),
                Codec.STRING.fieldOf("text").forGetter(RecordBlock::text)
        ).apply(instance, RecordBlock::new));
    }
}
