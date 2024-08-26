package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.IndexHolder;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.synchronizers.IHotpotSoupComponentSynchronizer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HotpotCompoundSoupComponent implements IHotpotSoupComponent {
    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getContentSerializerResultFromItemStack(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByTableware(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Double> getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<Boolean> getHotpotLit(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Boolean> result) {
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
    public IHotpotResult<Double> onAwardExperience(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    @Override
    public Optional<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizer(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        throw new IllegalStateException("Illegal call to a compound component");
    }

    public record Type(Map<ResourceLocation, IndexHolder<Either<ResourceKey<IHotpotSoupComponentType<?>>, IHotpotSoupComponentType<?>>>> componentTypeHolders) implements IHotpotSoupComponentType<HotpotCompoundSoupComponent> {
        @Override
        public HotpotCompoundSoupComponent get() {
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

        public Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> getComponentTypeHolderEntryStream(Holder<IHotpotSoupComponentType<?>> componentTypeHolder, int base, AtomicInteger self, AtomicInteger parent) {
            return componentTypeHolder instanceof Holder.Reference<IHotpotSoupComponentType<?>> reference && reference.unwrapLookup() != null ? componentTypeHolders.entrySet().stream().map(EntryStreams.mapEntryValue(holder -> holder.mapValue(either -> either.<Holder<IHotpotSoupComponentType<?>>>map(resourceKey -> reference.unwrapLookup().getOrThrow(resourceKey), Holder::direct)))).flatMap(entry -> expandCompoundComponents(entry, base, self, parent)) : Stream.empty();
        }

        public static Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> expandCompoundComponents(Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>> entry, int base, AtomicInteger self, AtomicInteger parent) {
            return entry.getValue().value().value() instanceof HotpotCompoundSoupComponent.Type type ? type.getComponentTypeHolderEntryStream(entry.getValue().value(), entry.getValue().index(), new AtomicInteger(self.get()), self) : Stream.of(entry).map(EntryStreams.mapEntryValue(holder -> holder.mapIndex(i -> base + parent.updateAndGet(v -> self.get() + i))));
        }

        public static Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> expandCompoundComponents(Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>> entry, AtomicInteger self) {
            return expandCompoundComponents(entry, 0, self, new AtomicInteger(0));
        }

        public static Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> expandCompoundComponents(Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> stream, AtomicInteger self) {
            return stream.flatMap(entry -> expandCompoundComponents(entry, self));
        }

        public static Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> expandCompoundComponents(Stream<Map.Entry<ResourceLocation, IndexHolder<Holder<IHotpotSoupComponentType<?>>>>> stream) {
            return expandCompoundComponents(stream, new AtomicInteger(0));
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotCompoundSoupComponent> {
        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> Codec.unboundedMap(ResourceLocation.CODEC, IndexHolder.getIndexedCodec(Codec.either(ResourceKey.codec(HotpotSoupComponentTypeSerializers.SOUP_COMPONENT_TYPE_REGISTRY_KEY), HotpotSoupComponentTypeSerializers.TYPE_CODEC).fieldOf("component"))).xmap(Type::new, Type::componentTypeHolders).fieldOf("compounds"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.map(LinkedHashMap::new, ResourceLocation.STREAM_CODEC, IndexHolder.getIndexedStreamCodec(ByteBufCodecs.either(ResourceKey.streamCodec(HotpotSoupComponentTypeSerializers.SOUP_COMPONENT_TYPE_REGISTRY_KEY), HotpotSoupComponentTypeSerializers.TYPE_STREAM_CODEC))).map(Type::new, type -> new LinkedHashMap<>(type.componentTypeHolders())));

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
