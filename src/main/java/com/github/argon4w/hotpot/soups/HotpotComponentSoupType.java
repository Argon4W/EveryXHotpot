package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IndexHolder;
import com.github.argon4w.hotpot.soups.components.*;
import com.github.argon4w.hotpot.soups.components.recipes.HotpotSoupBaseRecipeAcceptorSoupComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class HotpotComponentSoupType {
    public static final ResourceKey<Registry<HotpotComponentSoupType>> COMPONENT_SOUP_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup"));
    public static final HotpotComponentSoupType UNIT_TYPE = new HotpotComponentSoupType(Map.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "base_soup_recipe_acceptor"), new IndexHolder<>(0, Holder.direct(HotpotSoupBaseRecipeAcceptorSoupComponent.TYPE))));
    public static final Holder<HotpotComponentSoupType> UNIT_TYPE_HOLDER = Holder.direct(UNIT_TYPE);
    public static final HotpotComponentSoup UNIT_SOUP = loadSoup(UNIT_TYPE_HOLDER);

    public static final Codec<HotpotComponentSoupType> TYPE_CODEC = Codec.lazyInitialized(() -> Codec.unboundedMap(ResourceLocation.CODEC, IndexHolder.getIndexedCodec(HotpotSoupComponentTypeSerializers.TYPE_HOLDER_CODEC.fieldOf("component"))).fieldOf("components").codec().xmap(HotpotComponentSoupType::new, HotpotComponentSoupType::getComponentTypeHolders));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoupType> TYPE_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.map(LinkedHashMap::new, ResourceLocation.STREAM_CODEC, IndexHolder.getIndexedStreamCodec(HotpotSoupComponentTypeSerializers.TYPE_HOLDER_STREAM_CODEC)).map(HotpotComponentSoupType::new, type -> new LinkedHashMap<>(type.getComponentTypeHolders())));

    public static final Codec<Holder<HotpotComponentSoupType>> TYPE_HOLDER_CODEC = Codec.lazyInitialized(() -> RegistryFileCodec.create(COMPONENT_SOUP_TYPE_REGISTRY_KEY, Codec.unit(UNIT_TYPE), true));
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<HotpotComponentSoupType>> TYPE_HOLDER_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holder(COMPONENT_SOUP_TYPE_REGISTRY_KEY, StreamCodec.unit(UNIT_TYPE)));

    public static final Codec<HotpotComponentSoup> CODEC = Codec.lazyInitialized(() -> TYPE_HOLDER_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder.value().getCodec(holder).fieldOf("components")));
    public static final Codec<HotpotComponentSoup> PARTIAL_CODEC = Codec.lazyInitialized(() -> TYPE_HOLDER_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder.value().getPartialCodec(holder).fieldOf("components")));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoup> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> TYPE_HOLDER_STREAM_CODEC.dispatch(HotpotComponentSoup::soupTypeHolder, holder -> holder.value().getStreamCodec(holder)));

    private final Map<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>> componentTypeHolders;
    private final Map<ResourceLocation, MapCodec<Map.Entry<ResourceLocation, IndexHolder<IHotpotSoupComponent>>>> codecs;
    private final Map<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, Map.Entry<ResourceLocation, IndexHolder<IHotpotSoupComponent>>>> streamCodecs;

    public HotpotComponentSoupType(Map<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>> componentTypeHolders) {
        this.componentTypeHolders = HotpotCompoundSoupComponent.Type.expandCompoundComponents(componentTypeHolders.entrySet().stream()).collect(EntryStreams.ofSequenced());
        this.codecs = this.componentTypeHolders.entrySet().stream().map(EntryStreams.mapEntryValue((resourceLocation, value) -> castCodec(value.value().value().getCodec()).xmap(component -> new IndexHolder<>(value.index(), component), IndexHolder::value).xmap(holder -> Map.entry(resourceLocation, holder), Map.Entry::getValue))).collect(EntryStreams.of());
        this.streamCodecs = this.componentTypeHolders.entrySet().stream().map(EntryStreams.mapEntryValue((resourceLocation, value) -> castStreamCodec(value.value().value().getStreamCodec()).map(component -> new IndexHolder<>(value.index(), component), IndexHolder::value).map(holder -> Map.entry(resourceLocation, holder), Map.Entry::getValue))).collect(EntryStreams.of());
    }

    public Codec<HotpotComponentSoup> getPartialCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.CODEC.dispatch("id", Map.Entry::getKey, codecs::get).listOf().xmap(list -> list.stream().collect(EntryStreams.ofSequenced()), map -> List.copyOf(map.entrySet())).xmap(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::getPartialComponents);
    }

    public Codec<HotpotComponentSoup> getCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.CODEC.dispatch("id", Map.Entry::getKey, codecs::get).listOf().xmap(list -> list.stream().sorted(IndexHolder.getIndexComparator(Map.Entry::getValue)).collect(EntryStreams.ofSequenced()), map -> List.copyOf(map.entrySet())).xmap(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::components);
    }

    public StreamCodec<RegistryFriendlyByteBuf, HotpotComponentSoup> getStreamCodec(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return ResourceLocation.STREAM_CODEC.<RegistryFriendlyByteBuf>cast().dispatch(Map.Entry::getKey, streamCodecs::get).apply(ByteBufCodecs.list()).map(list -> list.stream().collect(EntryStreams.ofSequenced()), map -> List.copyOf(map.entrySet())).map(map -> new HotpotComponentSoup(map, soupTypeHolder), HotpotComponentSoup::components);
    }

    public HotpotComponentSoup getComponentSoup(Holder<HotpotComponentSoupType> soupTypeHolder) {
        return new HotpotComponentSoup(componentTypeHolders.entrySet().stream().map(EntryStreams.mapEntryValue(value -> value.<IHotpotSoupComponent>mapValue(holder -> holder.value().get()))).collect(EntryStreams.ofSequenced()), soupTypeHolder);
    }

    public <T extends IHotpotSoupComponent> List<ResourceLocation> getComponentKeysByTypes(List<Supplier<? extends IHotpotSoupComponentTypeSerializer<? extends T>>> componentTypeSerializerHolders) {
        return componentTypeHolders.entrySet().stream().filter(EntryStreams.filterEntryValue(holder -> componentTypeSerializerHolders.stream().anyMatch(supplier -> supplier.equals(holder.value().value().getSerializerHolder())))).map(Map.Entry::getKey).toList();
    }

    public boolean hasComponentType(Supplier<? extends IHotpotSoupComponentTypeSerializer<?>> componentTypeSerializerHolder) {
        return componentTypeHolders.values().stream().anyMatch(component -> componentTypeSerializerHolder.equals(component.value().value().getSerializerHolder()));
    }

    public Map<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>> getComponentTypeHolders() {
        return componentTypeHolders;
    }

    public static Tag saveSoup(HotpotComponentSoup soup, HolderLookup.Provider registryAccess) {
        return CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), soup).resultOrPartial().orElse(new CompoundTag());
    }

    public static HotpotComponentSoup loadSoup(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        return CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), compoundTag).resultOrPartial().orElse(UNIT_SOUP);
    }

    public static HotpotComponentSoup loadSoup(Holder<HotpotComponentSoupType> holder) {
        return holder.value().getComponentSoup(holder);
    }

    public static HotpotComponentSoup loadEmptySoup() {
        return UNIT_SOUP;
    }

    @SuppressWarnings("unchecked")
    private static <T extends IHotpotSoupComponent> MapCodec<IHotpotSoupComponent> castCodec(MapCodec<T> codec) {
        return codec.xmap(Function.identity(), c -> (T) c);
    }

    @SuppressWarnings("unchecked")
    private static <T extends IHotpotSoupComponent> StreamCodec<RegistryFriendlyByteBuf, IHotpotSoupComponent> castStreamCodec(StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return streamCodec.map(Function.identity(), c -> (T) c);
    }
}
