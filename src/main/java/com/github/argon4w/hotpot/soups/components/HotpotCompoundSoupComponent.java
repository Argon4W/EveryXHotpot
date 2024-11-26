package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.fancytoys.codecs.Sorted;
import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.fancytoys.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupSyncData;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotCompoundSoupComponent implements IHotpotSoupComponent {

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(
            IHotpotTablewareInteraction.Context context,
            ItemStack itemStack,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(
            ItemStack itemStack,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByTableware(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Double> getContentTickSpeed(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Boolean> getHotpotLit(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Boolean> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Double> onAwardExperience(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos,
            IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onDiscardOverflowWaterLevel(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onEntityInside(
            Entity entity,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onTick(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void setWaterLevelWithOverflow(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void setWaterLevel(
            double waterLevel,
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public Optional<IHotpotSoupSyncData> getSoupComponenentSyncData(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Double> getWaterLevel(IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Double> getOverflowWaterLevel(IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public boolean shouldSendToClient() {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    public record Type(Map<ResourceLocation, Sorted<Either<ResourceKey<IHotpotSoupComponentType<?>>, IHotpotSoupComponentType<?>>>> componentTypeHolders) implements IHotpotSoupComponentType<HotpotCompoundSoupComponent> {

        @Override
        public HotpotCompoundSoupComponent createSoupComponent() {
            throw new IllegalStateException("Illegal call to a compound component type");
        }

        @Override
        public MapCodec<HotpotCompoundSoupComponent> getCodec() {
            throw new IllegalStateException("Illegal call to a compound component type");
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotCompoundSoupComponent> getStreamCodec() {
            throw new IllegalStateException("Illegal call to a compound component type");
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.COMPOUND_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public EntryStream<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> getComponentTypes(HolderLookup<IHotpotSoupComponentType<?>> lookup, AtomicInteger index) {
            return EntryStream
                    .fromMap(componentTypeHolders)
                    .sortedValue(Sorted.comparator())
                    .<Sorted<Holder<IHotpotSoupComponentType<?>>>>mapValue(Sorted.valueMapper(either -> either.map(lookup::getOrThrow, Holder::direct)))
                    .flatMap((location, sorted) -> expand(location, sorted, index));
        }

        public EntryStream<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> getComponentTypes(Sorted<Holder<IHotpotSoupComponentType<?>>> sorted, AtomicInteger index) {
            return Sorted
                    .lookup(sorted)
                    .map(lookup -> getComponentTypes(lookup, index))
                    .orElse(EntryStream.empty());
        }

        public static EntryStream<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> expand(ResourceLocation location, Sorted<Holder<IHotpotSoupComponentType<?>>> sorted, AtomicInteger index) {
            return sorted.value().value() instanceof Type type
                    ? type.getComponentTypes(sorted, index)
                    : EntryStream.fromKeyValue(location, sorted);
        }

        public static EntryStream<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> expand(EntryStream<ResourceLocation, Sorted<Holder<IHotpotSoupComponentType<?>>>> stream, AtomicInteger index) {
            return stream
                    .sequential()
                    .sortedValue(Sorted.comparator())
                    .flatMap((location, sorted) -> expand(location, sorted, index))
                    .mapValue(Sorted.posMapper(index::getAndIncrement));
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotCompoundSoupComponent> {

        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> Codec
                .unboundedMap(ResourceLocation.CODEC, Sorted.codec(Codec.either(HotpotSoupComponentTypeSerializers.KEY_CODEC, HotpotSoupComponentTypeSerializers.TYPE_CODEC).fieldOf("component")))
                .xmap(Type::new, Type::componentTypeHolders)
                .fieldOf("compounds"));

         public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs
                .map(LinkedHashMap::new, ResourceLocation.STREAM_CODEC, Sorted.streamCodec(ByteBufCodecs.either(HotpotSoupComponentTypeSerializers.KEY_STREAM_CODEC, HotpotSoupComponentTypeSerializers.TYPE_STREAM_CODEC)))
                .map(Type::new, type -> new LinkedHashMap<>(type.componentTypeHolders())));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotCompoundSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotCompoundSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
