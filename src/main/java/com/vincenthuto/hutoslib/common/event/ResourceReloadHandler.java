package com.vincenthuto.hutoslib.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Unit;
import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class ResourceReloadHandler implements  PreparableReloadListener {

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");

            new RuneManager().reload(resourceManager);

            HutosLib.LOGGER.info("Reloading from HutosLib");
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }
}
