package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record HotpotSpriteProcessorDataComponent(List<Holder<IHotpotSpriteProcessor>> processors) {
    public static final HotpotSpriteProcessorDataComponent EMPTY = new HotpotSpriteProcessorDataComponent(new ArrayList<>());

    public static final Codec<HotpotSpriteProcessorDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotSpriteProcessors.CODEC.listOf().fieldOf("processors").forGetter(HotpotSpriteProcessorDataComponent::processors)
            ).apply(data, HotpotSpriteProcessorDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSpriteProcessorDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, HotpotSpriteProcessors.STREAM_CODEC), HotpotSpriteProcessorDataComponent::processors,
                    HotpotSpriteProcessorDataComponent::new
            )
    );

    public HotpotSpriteProcessorDataComponent addProcessor(Holder<IHotpotSpriteProcessor> processor) {
        return processor.value() instanceof HotpotEmptySpriteProcessor ? this : new HotpotSpriteProcessorDataComponent(Stream.concat(processors.stream(), Stream.of(processor)).toList());
    }

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT);
    }

    public static HotpotSpriteProcessorDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT, EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotSpriteProcessorDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT, dataComponent);
    }

    public static List<Holder<IHotpotSpriteProcessor>> getProcessors(ItemStack itemStack) {
        return getDataComponent(itemStack).processors;
    }

    public static void addProcessor(ItemStack itemStack, Holder<IHotpotSpriteProcessor> processor) {
        setDataComponent(itemStack, getDataComponent(itemStack).addProcessor(processor));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSpriteProcessorDataComponent data && processors.equals(data.processors);
    }
}
