package com.github.argon4w.hotpot.soups.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.machinezoo.noexception.Exceptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.Map;

public class SoupRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final ICondition.IContext context;

    public SoupRecipeManager(ICondition.IContext context) {
        super(GSON, "soup_recipes");

        this.context = context;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        resourceMap.forEach(Exceptions.ignore().fromBiConsumer((location, jsonElement) -> {

        }));
    }
}
