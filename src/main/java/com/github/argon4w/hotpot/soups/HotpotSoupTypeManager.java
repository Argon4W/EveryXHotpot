package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.types.HotpotEmptySoup;
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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HotpotSoupTypeManager extends SimpleJsonResourceReloadListener {
    public static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");
    public static final HotpotSoupTypeHolder<HotpotEmptySoup> EMPTY_SOUP_TYPE = new HotpotSoupTypeHolder<>(EMPTY_SOUP_LOCATION, new HotpotEmptySoup.Type());

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final StreamCodec<RegistryFriendlyByteBuf, IHotpotSoupType<?>> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(HotpotSoupTypeSerializers.SOUP_TYPE_SERIALIZER_REGISTRY_KEY).dispatch(IHotpotSoupType::getSerializer, holder -> holder.value().getStreamCodec()));
    public static final Codec<IHotpotSoupType<?>> CODEC = Codec.lazyInitialized(() -> HotpotSoupTypeSerializers.getSoupTypeSerializerRegistry().holderByNameCodec().dispatch(IHotpotSoupType::getSerializer, holder -> holder.value().getCodec()));
    public static final Codec<Optional<WithConditions<IHotpotSoupType<?>>>> CONDITIONAL_CODEC = Codec.lazyInitialized(() -> ConditionalOps.createConditionalCodecWithConditions(CODEC));

    private final Codec<HotpotSoupTypeHolder<?>> holderCodec = ResourceLocation.CODEC.xmap(this::getSoupType, HotpotSoupTypeHolder::key);
    private final StreamCodec<ByteBuf, HotpotSoupTypeHolder<?>> streamHolderCodec = ResourceLocation.STREAM_CODEC.map(this::getSoupType, HotpotSoupTypeHolder::key);

    private final Codec<IHotpotSoup> soupTypeCodec = holderCodec.dispatch(IHotpotSoup::getSoupTypeHolder, HotpotSoupTypeHolder::getCodec);
    private final HashMap<ResourceLocation, IHotpotSoupType<?>> soupTypes;

    public HotpotSoupTypeManager() {
        super(GSON, "soup");
        this.soupTypes = Maps.newHashMap();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElements, ResourceManager resourceManager, ProfilerFiller filler) {
        soupTypes.clear();
        RegistryOps<JsonElement> ops = this.makeConditionalOps();

        for (ResourceLocation resourceLocation : jsonElements.keySet()) {
            if (resourceLocation.getPath().startsWith("_")) {
                LOGGER.warn("Ignore \"{}\" beginning with \"_\" as it's used for metadata", resourceLocation);
                continue;
            }

            Optional<WithConditions<IHotpotSoupType<?>>> result = CONDITIONAL_CODEC.parse(ops, jsonElements.get(resourceLocation)).getOrThrow(JsonParseException::new);

            if (result.isEmpty()) {
                LOGGER.error("Error while loading soup config \"{}\" as it's conditions were not met", resourceLocation);
                continue;
            }

            soupTypes.put(resourceLocation, result.get().carrier());
        }
    }

    public void replaceSoupTypes(Map<ResourceLocation, IHotpotSoupType<?>> soupTypes) {
        this.soupTypes.clear();
        this.soupTypes.putAll(soupTypes);
    }

    public Tag saveSoup(IHotpotSoup soupType, HolderLookup.Provider registryAccess) {
        return soupTypeCodec.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), soupType).resultOrPartial().orElse(new CompoundTag());
    }

    public IHotpotSoup buildSoup(CompoundTag tag, HolderLookup.Provider registryAccess) {
        return soupTypeCodec.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag).resultOrPartial().orElse(buildEmptySoup());
    }

    public HotpotEmptySoup buildEmptySoup() {
        return EMPTY_SOUP_TYPE.getSoup();
    }

    public HotpotSoupTypeHolder<?> getSoupType(ResourceLocation resourceLocation) {
        return soupTypes.containsKey(resourceLocation) ? new HotpotSoupTypeHolder<>(resourceLocation, soupTypes.get(resourceLocation)) : EMPTY_SOUP_TYPE;
    }

    public HashMap<ResourceLocation, IHotpotSoupType<?>> getAllSoupTypes() {
        return soupTypes;
    }

    public Codec<HotpotSoupTypeHolder<?>> getHolderCodec() {
        return holderCodec;
    }

    public StreamCodec<ByteBuf, HotpotSoupTypeHolder<?>> getStreamHolderCodec() {
        return streamHolderCodec;
    }
}
