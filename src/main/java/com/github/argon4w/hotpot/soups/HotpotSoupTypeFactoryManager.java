package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.types.HotpotEmptySoupType;
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
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotpotSoupTypeFactoryManager extends SimpleJsonResourceReloadListener {
    public static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");
    public static final HotpotSoupTypeFactoryHolder<HotpotEmptySoupType> EMPTY_SOUP_FACTORY = new HotpotSoupTypeFactoryHolder<>(EMPTY_SOUP_LOCATION, new HotpotEmptySoupType.Factory());

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<IHotpotSoupTypeFactory<?>> CODEC = Codec.lazyInitialized(() -> HotpotSoupTypes.getSoupTypeRegistry().byNameCodec().dispatch(IHotpotSoupTypeFactory::getSerializer, IHotpotSoupFactorySerializer::getCodec));
    public static final Codec<Optional<WithConditions<IHotpotSoupTypeFactory<?>>>> CONDITIONAL_CODEC = Codec.lazyInitialized(() -> ConditionalOps.createConditionalCodecWithConditions(CODEC));

    private final Codec<HotpotSoupTypeFactoryHolder<?>> holderCodec = ResourceLocation.CODEC.xmap(this::getSoupFactory, HotpotSoupTypeFactoryHolder::key);
    private final StreamCodec<ByteBuf, HotpotSoupTypeFactoryHolder<?>> streamHolderCodec = ResourceLocation.STREAM_CODEC.map(this::getSoupFactory, HotpotSoupTypeFactoryHolder::key);

    private final Codec<IHotpotSoupType> soupTypeCodec = holderCodec.dispatch(IHotpotSoupType::getSoupTypeFactoryHolder, HotpotSoupTypeFactoryHolder::buildFromCodec);
    private final HashMap<ResourceLocation, IHotpotSoupTypeFactory<?>> byName;

    public HotpotSoupTypeFactoryManager() {
        super(GSON, "soup");
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

    public IHotpotSoupType buildSoup(CompoundTag tag, HolderLookup.Provider registryAccess) {
        return soupTypeCodec.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag).resultOrPartial().orElse(buildEmptySoup());
    }

    public HotpotEmptySoupType buildEmptySoup() {
        return EMPTY_SOUP_FACTORY.buildFromScratch();
    }

    public HotpotSoupTypeFactoryHolder<?> getSoupFactory(ResourceLocation resourceLocation) {
        return byName.containsKey(resourceLocation) ? new HotpotSoupTypeFactoryHolder<>(resourceLocation, byName.get(resourceLocation)) : EMPTY_SOUP_FACTORY;
    }

    public HashMap<ResourceLocation, IHotpotSoupTypeFactory<?>> getAllFactoriesByName() {
        return byName;
    }

    public Codec<HotpotSoupTypeFactoryHolder<?>> getHolderCodec() {
        return holderCodec;
    }

    public StreamCodec<ByteBuf, HotpotSoupTypeFactoryHolder<?>> getStreamHolderCodec() {
        return streamHolderCodec;
    }
}
