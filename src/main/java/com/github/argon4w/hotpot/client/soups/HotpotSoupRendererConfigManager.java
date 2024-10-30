package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class HotpotSoupRendererConfigManager extends net.neoforged.neoforge.resource.ContextAwareReloadListener implements PreparableReloadListener {
    public static final HotpotSoupRendererConfig EMPTY_SOUP_RENDER_CONFIG = new HotpotSoupRendererConfig(Optional.empty(), false, Optional.empty(), List.of(), List.of());
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String DIRECTORY = "soups";

    private ImmutableMap<ResourceLocation, HotpotSoupRendererConfig> rendererConfigs;

    public HotpotSoupRendererConfigManager() {
        this.rendererConfigs = ImmutableMap.of();
    }

    @NotNull
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> this.prepare(resourceManager), backgroundExecutor).thenCompose(preparationBarrier::wait);
    }

    protected void prepare(ResourceManager resourceManager) {
        loadSoupRendererConfigs(this.makeConditionalOps(), Util.make(new HashMap<>(), map -> SimpleJsonResourceReloadListener.scanDirectory(resourceManager, DIRECTORY, GSON, map)));
    }

    private void loadSoupRendererConfigs(RegistryOps<JsonElement> ops, Map<ResourceLocation, JsonElement> jsonElements) {
        rendererConfigs = Util.make(ImmutableMap.<ResourceLocation, HotpotSoupRendererConfig>builder(), builder -> jsonElements.entrySet().forEach(EntryStreams.peekEntryValue((resourceLocation, jsonElement) -> HotpotSoupRendererConfig.CODEC.parse(ops, jsonElement).result().ifPresentOrElse(rendererConfig -> builder.put(resourceLocation, rendererConfig), () -> LOGGER.error("Error while loading soup renderer config \"{}\"", resourceLocation))))).build();
    }

    public static HotpotSoupRendererConfig getSoupRendererConfig(ResourceKey<HotpotComponentSoupType> key) {
        return key == null ? EMPTY_SOUP_RENDER_CONFIG : getSoupRendererConfig(key.location());
    }

    public static HotpotSoupRendererConfig getSoupRendererConfig(ResourceLocation resourceLocation) {
        return HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.rendererConfigs.getOrDefault(resourceLocation, EMPTY_SOUP_RENDER_CONFIG);
    }

    public static Collection<HotpotSoupRendererConfig> getAllSoupRendererConfigs() {
        return HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.rendererConfigs.values();
    }
}
