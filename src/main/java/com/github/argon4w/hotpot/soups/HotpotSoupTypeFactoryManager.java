package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotpotSoupTypeFactoryManager extends SimpleJsonResourceReloadListener {
    public static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");
    public static final HotpotWrappedSoupTypeTypeFactory<HotpotEmptySoupType> EMPTY_SOUP_FACTORY = new HotpotWrappedSoupTypeTypeFactory<>(EMPTY_SOUP_LOCATION, new HotpotEmptySoupType.Factory());

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<IHotpotSoupTypeFactory<?>> CODEC = Codec.lazyInitialized(() -> HotpotSoupTypes.getSoupTypeRegistry().byNameCodec().dispatch(IHotpotSoupTypeFactory::getSerializer, IHotpotSoupFactorySerializer::getCodec));
    public static final Codec<Optional<WithConditions<IHotpotSoupTypeFactory<?>>>> CONDITIONAL_CODEC = Codec.lazyInitialized(() -> ConditionalOps.createConditionalCodecWithConditions(CODEC));

    private final HashMap<ResourceLocation, IHotpotSoupTypeFactory<?>> byName;

    public final Codec<HotpotWrappedSoupTypeTypeFactory<?>> wrappedCodec = ResourceLocation.CODEC.xmap(this::getSoupFactory, HotpotWrappedSoupTypeTypeFactory::resourceLocation);
    public final Codec<IHotpotSoupType> soupTypeCodec = wrappedCodec.dispatch(this::getSoupFactoryFromSoupType, HotpotWrappedSoupTypeTypeFactory::buildFromCodec);

    public final StreamCodec<ByteBuf, HotpotWrappedSoupTypeTypeFactory<?>> streamWrappedCodec = ResourceLocation.STREAM_CODEC.map(this::getSoupFactory, HotpotWrappedSoupTypeTypeFactory::resourceLocation);

    public HotpotSoupTypeFactoryManager() {
        super(HotpotSoupTypeFactoryManager.GSON, "soup");
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

            Optional<WithConditions<IHotpotSoupTypeFactory<?>>> result = CONDITIONAL_CODEC.parse(ops, jsonElements.get(resourceLocation)).getOrThrow(JsonParseException::new);

            if (result.isEmpty()) {
                LOGGER.error("Error while loading soup config \"{}\" as it's conditions were not met", resourceLocation);
                continue;
            }

            byName.put(resourceLocation, result.get().carrier());
        }
    }

    public void replaceFactories(Map<ResourceLocation, IHotpotSoupTypeFactory<?>> byName) {
        this.byName.clear();
        this.byName.putAll(byName);
    }

    public Tag saveSoup(IHotpotSoupType soupType, HolderLookup.Provider registryAccess) {
        return soupTypeCodec.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), soupType).resultOrPartial().orElse(new CompoundTag());
    }

    public IHotpotSoupType buildSoup(CompoundTag tag) {
        return soupTypeCodec.parse(NbtOps.INSTANCE, tag).resultOrPartial().orElse(buildEmptySoup());
    }

    public IHotpotSoupType buildSoup(ResourceLocation resourceLocation) {
        return getSoupFactory(resourceLocation).buildFromScratch(resourceLocation);
    }

    public HotpotEmptySoupType buildEmptySoup() {
        return HotpotSoupTypeFactoryManager.EMPTY_SOUP_FACTORY.buildFromScratch();
    }

    public HotpotWrappedSoupTypeTypeFactory<?> getSoupFactoryFromSoupType(IHotpotSoupType soupType) {
        return new HotpotWrappedSoupTypeTypeFactory<>(soupType.getResourceLocation(), byName.get(soupType.getResourceLocation()));
    }

    public HotpotWrappedSoupTypeTypeFactory<?> getSoupFactory(ResourceLocation resourceLocation) {
        return byName.containsKey(resourceLocation) ? new HotpotWrappedSoupTypeTypeFactory<>(resourceLocation, byName.get(resourceLocation)) : EMPTY_SOUP_FACTORY;
    }

    public HashMap<ResourceLocation, IHotpotSoupTypeFactory<?>> getAllFactoriesByName() {
        return byName;
    }
}
