package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import java.util.*;

/** Connection-local event baselines; these never modify server knowledge or persistent pins. */
public final class BookNotificationState {
    private boolean knowledgeBaseline;
    private Map<ResourceLocation, Set<Integer>> readable;
    private long generation = -1;

    public Set<ResourceLocation> knowledge(Set<ResourceLocation> before, Set<ResourceLocation> after,
                                           Set<ResourceLocation> explicit, boolean initialSync) {
        if (!knowledgeBaseline || initialSync) { knowledgeBaseline = true; return Set.of(); }
        Set<ResourceLocation> added = new HashSet<>(explicit.isEmpty() ? after : explicit);
        if (explicit.isEmpty()) added.removeAll(before);
        added.retainAll(after);
        return Set.copyOf(added);
    }

    public Set<ResourceLocation> readability(Set<ResourceLocation> next, long generation) {
        Map<ResourceLocation, Set<Integer>> levels = new HashMap<>();
        next.forEach(id -> levels.put(id, Set.of(0)));
        return readability(levels, generation);
    }

    public Set<ResourceLocation> readability(Map<ResourceLocation, Set<Integer>> next, long generation) {
        Set<ResourceLocation> lost = new HashSet<>();
        if (readable != null && this.generation == generation) readable.forEach((id, levels) -> {
            if (!next.getOrDefault(id, Set.of()).containsAll(levels)) lost.add(id);
        });
        Map<ResourceLocation, Set<Integer>> snapshot = new HashMap<>();
        next.forEach((id, levels) -> snapshot.put(id, Set.copyOf(levels)));
        readable = Map.copyOf(snapshot);
        this.generation = generation;
        return Set.copyOf(lost);
    }
}
