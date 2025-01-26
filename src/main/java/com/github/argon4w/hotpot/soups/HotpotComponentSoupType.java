package com.github.argon4w.hotpot.soups;

import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.fancytoys.codecs.Sorted;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.soups.components.HotpotCompoundSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

@SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
public class HotpotComponentSoupType {

    public static final ResourceKey<Registry<HotpotComponentSoupType>> COMPONENT_SOUP_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup"));
    public static final ResourceKey<HotpotComponentSoupType> EMPTY_SOUP_TYPE_KEY = ResourceKey.create(COMPONENT_SOUP_TYPE_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup"));

    public static final Codec<ResourceKey<HotpotComponentSoupType>> KEY_CODEC = Codec.lazyInitialized(() -> ResourceKey.codec(COMPONENT_SOUP_TYPE_REGISTRY_KEY));
    public static final StreamCodec<ByteBuf, ResourceKey<HotpotComponentSoupType>> KEY_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ResourceKey.streamCodec(COMPONENT_SOUP_TYPE_REGISTRY_KEY));

    public static final Codec<Holder<HotpotComponentSoupType>> TYPE_HOLDER_CODEC = Codec.lazyInitialized(() -> RegistryFixedCodec.create(COMPONENT_SOUP_TYPE_REGISTRY_KEY));
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<HotpotComponentSoupType>> TYPE_HOLDER_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(COMPONENT_SOUP_TYPE_REGISTRY_KEY));

    public static final Codec<HotpotComponentSoupType> TYPE_CODEC = Codec.lazyInitialized(() -> Codec
            .unboundedMap(ResourceLocation.CODEC, Sorted.codec(HotpotSoupComponentTypeSerializers.TYPE_HOLDER_CODEC.fieldOf("component")))
            .fieldOf("components")
            .codec()
            .xmap(HotpotComponentSoupType::new, HotpotComponentSoupType::getComponentTypeHolders));

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoupType> TYPE_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs
            .map(LinkedHashMap::new, ResourceLocation.STREAM_CODEC, Sorted.streamCodec(HotpotSoupComponentTypeSerializers.TYPE_HOLDER_STREAM_CODEC))
            .map(HotpotComponentSoupType::new, type -> new LinkedHashMap<>(type.getComponentTypeHolders())));

    public static final Codec<HotpotComponentSoup> CODEC = Codec.lazyInitialized(() -> TYPE_HOLDER_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder
            .value()
            .getCodec(holder)
            .fieldOf("components")));
    public static final Codec<HotpotComponentSoup> PARTIAL_CODEC = Codec.lazyInitialized(() -> TYPE_HOLDER_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder
            .value()
            .getPartialCodec(holder)
            .fieldOf("components")));

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoup> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> TYPE_HOLDER_STREAM_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder
            .value()
            .getStreamCodec(holder)));

    private final Map<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, Map.Entry<ResourceLocation, Sorted<IHotpotSoupComponent>>>> streamCodecs;
    private final Map<ResourceLocation, MapCodec<Map.Entry<ResourceLocation, Sorted<IHotpotSoupComponent>>>> codecs;

    private final Map<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> componentTypeHolders;
    private final Map<ResourceLocation, Holder<IHotpotSoupComponentTypeSerializer<?>>> componentTypeSerializers;

    private final Object2ObjectMap<List<?>, List<ResourceLocation>> cachedKeysByTypes;

    public HotpotComponentSoupType(Map<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> componentTypeHolders) {
        this.componentTypeHolders = HotpotCompoundSoupComponent.Type
                .expand(EntryStream.fromMap(componentTypeHolders), new AtomicInteger(0))
                .toSequencedMap();

        this.componentTypeSerializers = new LinkedHashMap<>();
        this.componentTypeHolders.forEach((key, sorted) -> componentTypeSerializers.put(key, sorted.value().value().getSerializerHolder()));

        this.codecs = EntryStream
                .fromMap(this.componentTypeHolders)
                .mapValue(HotpotComponentSoupType::makeCodec)
                .toMap();

        this.streamCodecs = EntryStream
                .fromMap(this.componentTypeHolders)
                .mapValue(HotpotComponentSoupType::makeStreamCodec)
                .toMap();

        this.cachedKeysByTypes = new Object2ObjectOpenHashMap<>();
    }

    public <T extends IHotpotSoupComponent> List<ResourceLocation> getComponentKeysByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> holders) {
        return cachedKeysByTypes.computeIfAbsent(holders, list -> EntryStream
                .fromMap(componentTypeSerializers)
                .filterValue(holders::contains)
                .keys()
                .toList());
    }

    public Codec<HotpotComponentSoup> getPartialCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.CODEC
                .dispatch("id", Map.Entry::getKey, codecs::get)
                .listOf()
                .xmap(EntryStream::toSequencedMap, EntryStream::toList)
                .xmap(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::getPartialComponents);
    }

    public Codec<HotpotComponentSoup> getCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.CODEC
                .dispatch("id", Map.Entry::getKey, codecs::get)
                .listOf()
                .xmap(HotpotComponentSoupType::toSortedSequencedMap, EntryStream::toList)
                .xmap(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::getComponents);
    }

    public StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoup> getStreamCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.STREAM_CODEC
                .<RegistryFriendlyByteBuf>cast()
                .dispatch(Map.Entry::getKey, streamCodecs::get)
                .apply(ByteBufCodecs.list())
                .map(EntryStream::toSequencedMap, EntryStream::toList)
                .map(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::getComponents);
    }

    public HotpotComponentSoup createComponentSoup(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return EntryStream
                .fromMap(componentTypeHolders)
                .mapValue(Sorted.valueMapper(Holder::value))
                .<Sorted<IHotpotSoupComponent>>mapValue(Sorted.valueMapper(IHotpotSoupComponentType::createSoupComponent))
                .toSequencedMap(map -> new HotpotComponentSoup(map, soupTypeHolder));
    }

    public boolean hasComponentType(Supplier<? extends IHotpotSoupComponentTypeSerializer<?>> holder) {
        return componentTypeSerializers.containsValue(holder);
    }

    public Map<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> getComponentTypeHolders() {
        return componentTypeHolders;
    }

    public Map<ResourceLocation, Holder<IHotpotSoupComponentTypeSerializer<?>>> getComponentTypeSerializers() {
        return componentTypeSerializers;
    }

    private static <T extends IHotpotSoupComponent> MapCodec<IHotpotSoupComponent> castCodec(MapCodec<T> codec) {
        return codec.xmap(Function.identity(), c -> (T) c);
    }

    private static <T extends IHotpotSoupComponent> StreamCodec<RegistryFriendlyByteBuf, IHotpotSoupComponent> castStreamCodec(StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return streamCodec.map(Function.identity(), c -> (T) c);
    }

    public static HotpotComponentSoup loadSoup(Holder<HotpotComponentSoupType> holder) {
        return holder.value().createComponentSoup(holder);
    }

    public static HotpotComponentSoup loadSoup(ResourceKey<HotpotComponentSoupType> key, HolderLookup.Provider registryAccess) {
        return loadSoup(loadSoupTypeHolder(key, registryAccess));
    }

    public static HotpotComponentSoup loadEmptySoup(HolderLookup.Provider registryAccess) {
        return loadSoup(loadEmptySoupTypeHolder(registryAccess));
    }

    public static Holder<HotpotComponentSoupType> loadEmptySoupTypeHolder(HolderLookup.Provider registryAccess) {
        return getHolderLookup(registryAccess).getOrThrow(EMPTY_SOUP_TYPE_KEY);
    }

    public static HolderLookup<HotpotComponentSoupType> getHolderLookup(HolderLookup.Provider registryAccess) {
        return registryAccess.lookupOrThrow(COMPONENT_SOUP_TYPE_REGISTRY_KEY);
    }

    public static Holder<HotpotComponentSoupType> loadSoupTypeHolder(ResourceKey<HotpotComponentSoupType> key, HolderLookup.Provider registryAccess) {
        return getHolderLookup(registryAccess)
                .get(key)
                .map(Holder::getDelegate)
                .orElse(loadEmptySoupTypeHolder(registryAccess));
    }

    public static SequencedMap<ResourceLocation, Sorted<IHotpotSoupComponent>> toSortedSequencedMap(List<Map.Entry<ResourceLocation, Sorted<IHotpotSoupComponent>>> list) {
        return EntryStream
                .fromList(list)
                .sortedValue(Sorted.comparator())
                .toSequencedMap();
    }

    public static MapCodec<Map.Entry<ResourceLocation, Sorted<IHotpotSoupComponent>>> makeCodec(ResourceLocation resourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>> value) {
        return castCodec(value.value().value().getCodec())
                .xmap(value::mapValue, Sorted::value)
                .xmap(holder -> Map.entry(resourceLocation, holder), Map.Entry::getValue);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, Map.Entry<ResourceLocation, Sorted<IHotpotSoupComponent>>> makeStreamCodec(ResourceLocation resourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>> value) {
        return castStreamCodec(value.value().value().getStreamCodec())
                .map(value::mapValue, Sorted::value)
                .map(holder -> Map.entry(resourceLocation, holder), Map.Entry::getValue);
    }
}
