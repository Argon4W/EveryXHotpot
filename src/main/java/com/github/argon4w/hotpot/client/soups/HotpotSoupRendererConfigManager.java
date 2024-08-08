package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HotpotSoupRendererConfigManager extends SimpleJsonResourceReloadListener {
    public static final HotpotSoupRendererConfig EMPTY_SOUP_RENDER_CONFIG = new HotpotSoupRendererConfig(Optional.empty(), false, List.of(), Optional.empty());

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final HashMap<ResourceLocation, HotpotSoupRendererConfig> byName;

    public HotpotSoupRendererConfigManager() {
        super(HotpotSoupRendererConfigManager.GSON, "soups");
        this.byName = Maps.newHashMap();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller filler) {
        byName.clear();
        RegistryOps<JsonElement> ops = this.makeConditionalOps();

        for (ResourceLocation resourceLocation : jsonElements.keySet()) {
            if (resourceLocation.getPath().startsWith("_")) {
                LOGGER.warn("Ignore \"{}\" beginning with \"_\" as it's used for metadata", resourceLocation);
                continue;
            }

            Optional<HotpotSoupRendererConfig> result = HotpotSoupRendererConfig.CODEC.parse(ops, jsonElements.get(resourceLocation)).result();

            if (result.isEmpty()) {
                LOGGER.error("Error while loading soup render config \"{}\"", resourceLocation);
                continue;
            }

            byName.put(resourceLocation, result.get());
        }
    }

    public static HotpotSoupRendererConfig getSoupRendererConfig(ResourceLocation resourceLocation) {
        return HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.byName.getOrDefault(resourceLocation, HotpotSoupRendererConfigManager.EMPTY_SOUP_RENDER_CONFIG);
    }

    public static HotpotSoupRendererConfig getSoupRendererConfig(HotpotSoupTypeHolder<?> soupTypeHolder) {
        return getSoupRendererConfig(soupTypeHolder.key());
    }

    public static HotpotSoupRendererConfig getSoupRendererConfig(IHotpotSoup soupType) {
        return getSoupRendererConfig(soupType.getSoupTypeHolder());
    }
}
