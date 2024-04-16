package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HotpotSoupFactoryManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IHotpotSoupFactory<?> EMPTY_SOUP_FACTORY = HotpotSoupTypes.getEmptySoupTypeSerializer().fromJson(new ResourceLocation(HotpotModEntry.MODID, "empty_soup"), null);

    private final ICondition.IContext context;
    private final HashMap<ResourceLocation,IHotpotSoupFactory<?>> factories;

    public HotpotSoupFactoryManager() {
        this(ICondition.IContext.EMPTY);
    }

    public HotpotSoupFactoryManager(ICondition.IContext context) {
        super(HotpotSoupFactoryManager.GSON, "soups");
        this.context = context;
        this.factories = Maps.newHashMap();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller filler) {
        factories.clear();
        for (ResourceLocation resourceLocation : jsonElements.keySet()) {
            JsonElement jsonElement = jsonElements.get(resourceLocation);

            if (resourceLocation.getPath().startsWith("_")) {
                LOGGER.warn("Ignore \"{}\" beginning with \"_\" as it's used for metadata", resourceLocation);
                continue;
            }

            if (!jsonElement.isJsonObject()) {
                LOGGER.error("Error while loading soup config \"{}\". Soup config must be a JSON object", resourceLocation);
                continue;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (!jsonObject.has("type")) {
                LOGGER.error("Error while loading soup config \"{}\". Soup config must have a \"type\"", resourceLocation);
                continue;
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "type"))) {
                LOGGER.error("Error while loading soup config \"{}\". \"type\" in the soup config must be a valid resource location", resourceLocation);
                continue;
            }

            if (!CraftingHelper.processConditions(jsonObject, "conditions", this.context)) {
                LOGGER.error("Skipping loading recipe {} as it's conditions were not met", resourceLocation);
                continue;
            }

            try {
                ResourceLocation serializerResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "type"));
                IHotpotSoupTypeSerializer<?> soupSerializer = HotpotSoupTypes.getSoupTypeSerializer(serializerResourceLocation);
                IHotpotSoupFactory<?> factory = soupSerializer.fromJson(resourceLocation, jsonObject);

                factories.computeIfAbsent(resourceLocation, location -> factory);
            } catch (Exception e) {
                LOGGER.error("Error while loading soup renderer config \"{}\"", resourceLocation);
                e.printStackTrace();
            }
        }
    }

    public Map<ResourceLocation, IHotpotSoupFactory<?>> getAllFactories() {
        return factories;
    }

    public void replaceFactories(Map<ResourceLocation, IHotpotSoupFactory<?>> soups) {
        this.factories.clear();
        this.factories.putAll(soups);
    }

    public IHotpotSoupFactory<?> getSoupFactory(ResourceLocation resourceLocation) {
        return factories.getOrDefault(resourceLocation, HotpotSoupFactoryManager.EMPTY_SOUP_FACTORY);
    }

    public IHotpotSoupType buildSoup(ResourceLocation resourceLocation) {
        return getSoupFactory(resourceLocation).build();
    }

    public IHotpotSoupType buildEmptySoup() {
        return HotpotSoupFactoryManager.EMPTY_SOUP_FACTORY.build();
    }
}
