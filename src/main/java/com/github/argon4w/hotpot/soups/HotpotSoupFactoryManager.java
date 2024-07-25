package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotpotSoupFactoryManager extends SimpleJsonResourceReloadListener {
    private static final IHotpotSoupFactory<HotpotEmptySoupType> EMPTY_SOUP_FACTORY = new HotpotEmptySoupType.Factory();
    private static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<IHotpotSoupFactory<?>> CODEC = Codec.lazyInitialized(() -> HotpotSoupTypes.getSoupTypeRegistry().byNameCodec().dispatch(IHotpotSoupFactory::getSerializer, IHotpotSoupFactorySerializer::getCodec));
    public static final Codec<Optional<WithConditions<IHotpotSoupFactory<?>>>> CONDITIONAL_CODEC = Codec.lazyInitialized(() -> ConditionalOps.createConditionalCodecWithConditions(CODEC));

    private final HashMap<ResourceLocation,IHotpotSoupFactory<?>> byName;

    public final Codec<HotpotWrappedSoupTypeFactory<?>> WRAPPED_CODEC = ResourceLocation.CODEC.xmap(this::getSoupFactory, HotpotWrappedSoupTypeFactory::resourceLocation);
    public final Codec<IHotpotSoupType> SOUP_TYPE_CODEC = WRAPPED_CODEC.dispatch(this::getSoupFactoryFromSoupType, HotpotWrappedSoupTypeFactory::buildFromCodec);

    public HotpotSoupFactoryManager() {
        super(HotpotSoupFactoryManager.GSON, "soups");
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

            Optional<WithConditions<IHotpotSoupFactory<?>>> result = CONDITIONAL_CODEC.parse(ops, jsonElements.get(resourceLocation)).getOrThrow(JsonParseException::new);

            if (result.isEmpty()) {
                LOGGER.error("Error while loading soup config \"{}\" as it's conditions were not met", resourceLocation);
                continue;
            }

            byName.put(resourceLocation, result.get().carrier());
        }
    }

    public void replaceFactories(Map<ResourceLocation, IHotpotSoupFactory<?>> byName) {
        this.byName.clear();
        this.byName.putAll(byName);
    }

    public Tag saveSoup(IHotpotSoupType soupType, HolderLookup.Provider registryAccess) {
        return SOUP_TYPE_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), soupType).resultOrPartial().orElse(new CompoundTag());
    }

    public IHotpotSoupType buildSoup(CompoundTag tag) {
        return SOUP_TYPE_CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial().orElse(buildEmptySoup());
    }

    public IHotpotSoupType buildSoup(ResourceLocation resourceLocation) {
        return getSoupFactory(resourceLocation).buildFromScratch(resourceLocation);
    }

    public IHotpotSoupType buildEmptySoup() {
        return HotpotSoupFactoryManager.EMPTY_SOUP_FACTORY.buildFromScratch(HotpotSoupFactoryManager.EMPTY_SOUP_LOCATION);
    }

    public HotpotWrappedSoupTypeFactory<?> getSoupFactoryFromSoupType(IHotpotSoupType soupType) {
        return new HotpotWrappedSoupTypeFactory<>(soupType.getResourceLocation(), byName.get(soupType.getResourceLocation()));
    }

    public HotpotWrappedSoupTypeFactory<?> getSoupFactory(ResourceLocation resourceLocation) {
        return new HotpotWrappedSoupTypeFactory<>(resourceLocation, byName.getOrDefault(resourceLocation, HotpotSoupFactoryManager.EMPTY_SOUP_FACTORY));
    }

    public HashMap<ResourceLocation, IHotpotSoupFactory<?>> getAllFactoriesByName() {
        return byName;
    }
}
