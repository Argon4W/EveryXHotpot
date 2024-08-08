package com.github.argon4w.hotpot.items.process;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.client.HotpotColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotCustomColorSpriteProcessorConfig(HotpotColor color, ResourceLocation processorResourceLocation, ResourceLocation resourceLocation) implements IHotpotSpriteProcessorConfig {
    @Override
    public Holder<IHotpotSpriteProcessorConfigSerializer<?>> getSerializer() {
        return HotpotSpriteProcessorConfigs.CUSTOM_COLOR_PROCESSOR_CONFIG;
    }

    @Override
    public ResourceLocation getProcessorResourceLocation() {
        return processorResourceLocation;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotCustomColorSpriteProcessorConfig config && config.color.equals(color) && config.processorResourceLocation.equals(processorResourceLocation) && config.resourceLocation.equals(resourceLocation);
    }

    public static class Serializer implements IHotpotSpriteProcessorConfigSerializer<HotpotCustomColorSpriteProcessorConfig> {
        public static final MapCodec<HotpotCustomColorSpriteProcessorConfig> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(config -> config.group(
                        HotpotColor.CODEC.fieldOf("color").forGetter(HotpotCustomColorSpriteProcessorConfig::color),
                        ResourceLocation.CODEC.fieldOf("processor_resource_location").forGetter(HotpotCustomColorSpriteProcessorConfig::processorResourceLocation),
                        ResourceLocation.CODEC.fieldOf("resource_location").forGetter(HotpotCustomColorSpriteProcessorConfig::resourceLocation)
                ).apply(config, HotpotCustomColorSpriteProcessorConfig::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotCustomColorSpriteProcessorConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotColor.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::color,
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::processorResourceLocation,
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::resourceLocation,
                        HotpotCustomColorSpriteProcessorConfig::new
                )
        );

        @Override
        public MapCodec<HotpotCustomColorSpriteProcessorConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotCustomColorSpriteProcessorConfig> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
