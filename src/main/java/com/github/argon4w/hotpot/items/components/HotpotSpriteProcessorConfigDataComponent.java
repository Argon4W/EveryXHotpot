package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.process.HotpotEmptySpriteProcessorConfig;
import com.github.argon4w.hotpot.items.process.HotpotSpriteProcessorConfigs;
import com.github.argon4w.hotpot.items.process.IHotpotSpriteProcessorConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record HotpotSpriteProcessorConfigDataComponent(List<IHotpotSpriteProcessorConfig> processorConfigs) {
    public static final HotpotSpriteProcessorConfigDataComponent EMPTY = new HotpotSpriteProcessorConfigDataComponent(new ArrayList<>());

    public static final Codec<HotpotSpriteProcessorConfigDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotSpriteProcessorConfigs.CODEC.listOf().fieldOf("processor_configs").forGetter(HotpotSpriteProcessorConfigDataComponent::processorConfigs)
            ).apply(data, HotpotSpriteProcessorConfigDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSpriteProcessorConfigDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, HotpotSpriteProcessorConfigs.STREAM_CODEC), HotpotSpriteProcessorConfigDataComponent::processorConfigs,
                    HotpotSpriteProcessorConfigDataComponent::new
            )
    );

    public HotpotSpriteProcessorConfigDataComponent addProcessorConfig(IHotpotSpriteProcessorConfig processorConfig) {
        return processorConfig instanceof HotpotEmptySpriteProcessorConfig ? this : hasSameProcessorConfig(processorConfig) ? replaceProcessorConfig(processorConfig) : new HotpotSpriteProcessorConfigDataComponent(Stream.concat(processorConfigs.stream(), Stream.of(processorConfig)).toList());
    }

    public HotpotSpriteProcessorConfigDataComponent replaceProcessorConfig(IHotpotSpriteProcessorConfig processorConfig) {
        return new HotpotSpriteProcessorConfigDataComponent(processorConfigs.stream().map(config -> config.getResourceLocation().equals(processorConfig.getResourceLocation()) ? processorConfig : config).toList());
    }

    public boolean hasSameProcessorConfig(IHotpotSpriteProcessorConfig processorConfig) {
        return processorConfigs.stream().anyMatch(config -> config.getResourceLocation().equals(processorConfig.getResourceLocation()));
    }

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT);
    }

    public static HotpotSpriteProcessorConfigDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT, EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotSpriteProcessorConfigDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_SPRITE_PROCESSOR_DATA_COMPONENT, dataComponent);
    }

    public static List<IHotpotSpriteProcessorConfig> getProcessorConfigs(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).processorConfigs);
    }

    public static void addProcessorConfig(ItemStack itemStack, IHotpotSpriteProcessorConfig processorConfig) {
        setDataComponent(itemStack, getDataComponent(itemStack).addProcessorConfig(processorConfig));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSpriteProcessorConfigDataComponent data && processorConfigs.equals(data.processorConfigs);
    }
}
