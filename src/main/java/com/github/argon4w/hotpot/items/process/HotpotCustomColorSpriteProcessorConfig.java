package com.github.argon4w.hotpot.items.process;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.client.HotpotColor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotCustomColorSpriteProcessorConfig(ResourceLocation id, HotpotColor color, ResourceLocation processorResourceLocation) implements IHotpotSpriteProcessorConfig {
    @Override
    public Holder<IHotpotSpriteProcessorConfigSerializer<?>> getSerializerHolder() {
        return HotpotSpriteProcessorConfigs.CUSTOM_COLOR_PROCESSOR_CONFIG;
    }

    @Override
    public ResourceLocation getProcessorResourceLocation() {
        return processorResourceLocation;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotCustomColorSpriteProcessorConfig config && config.color.equals(color) && config.processorResourceLocation.equals(processorResourceLocation) && config.id.equals(id);
    }

    public static class Serializer implements IHotpotSpriteProcessorConfigSerializer<HotpotCustomColorSpriteProcessorConfig> {
        public static final MapCodec<HotpotCustomColorSpriteProcessorConfig> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(config -> config.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(HotpotCustomColorSpriteProcessorConfig::id),
                        HotpotColor.CODEC.fieldOf("color").forGetter(HotpotCustomColorSpriteProcessorConfig::color),
                        ResourceLocation.CODEC.fieldOf("processor_resource_location").forGetter(HotpotCustomColorSpriteProcessorConfig::processorResourceLocation)
                ).apply(config, HotpotCustomColorSpriteProcessorConfig::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotCustomColorSpriteProcessorConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::id,
                        HotpotColor.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::color,
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteProcessorConfig::processorResourceLocation,
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
