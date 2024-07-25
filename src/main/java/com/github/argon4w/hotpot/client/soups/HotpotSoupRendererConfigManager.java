package com.github.argon4w.hotpot.client.soups;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;

public class HotpotSoupRendererConfigManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final HotpotSoupRendererConfig EMPTY_SOUP_RENDER_CONFIG = new HotpotSoupRendererConfig(null, false, List.of(), null);
    private static final Logger LOGGER = LogUtils.getLogger();

    private final HashMap<ResourceLocation, HotpotSoupRendererConfig> soupRendererConfigs;

    public HotpotSoupRendererConfigManager() {
        super(HotpotSoupRendererConfigManager.GSON, "byName");
        this.soupRendererConfigs = Maps.newHashMap();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller filler) {
        soupRendererConfigs.clear();
        for (ResourceLocation resourceLocation : jsonElements.keySet()) {
            JsonElement jsonElement = jsonElements.get(resourceLocation);

            if (resourceLocation.getPath().startsWith("_")) {
                LOGGER.warn("Ignore \"{}\" beginning with \"_\" as it's used for metadata", resourceLocation);
                continue;
            }

            if (!jsonElement.isJsonObject()) {
                LOGGER.error("Error while loading soup renderer config \"{}\". Soup renderer config must be a JSON object", resourceLocation);
                continue;
            }

            try {
                HotpotSoupRendererConfig soupRendererConfig = HotpotSoupRendererConfig.SERIALIZER.fromJson(jsonElement.getAsJsonObject());
                soupRendererConfigs.computeIfAbsent(resourceLocation, location -> soupRendererConfig);
            } catch (Exception e) {
                LOGGER.error("Error while loading soup renderer config \"{}\"", resourceLocation);
                e.printStackTrace();
            }
        }
    }

    public HotpotSoupRendererConfig getSoupRendererConfig(ResourceLocation resourceLocation) {
        return soupRendererConfigs.getOrDefault(resourceLocation, HotpotSoupRendererConfigManager.EMPTY_SOUP_RENDER_CONFIG);
    }
}
