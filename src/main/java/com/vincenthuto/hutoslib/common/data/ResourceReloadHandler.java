package com.vincenthuto.hutoslib.common.data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;

import com.mojang.datafixers.util.Unit;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.book.BookManager;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class ResourceReloadHandler implements  PreparableReloadListener {

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");
            new BookManager().reload(resourceManager);
            
            HutosLib.LOGGER.info("Reloading from HutosLib");
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }
}
