package com.github.argon4w.hotpot.items.process;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupRendererConfigSpriteProcessorConfig(ResourceLocation soupRendererConfigResourceLocation, ResourceLocation processorResourceLocation, ResourceLocation resourceLocation) implements IHotpotSpriteProcessorConfig {
    @Override
    public Holder<IHotpotSpriteProcessorConfigSerializer<?>> getSerializer() {
        return HotpotSpriteProcessorConfigs.SOUP_TYPE_PROCESSOR_CONFIG;
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
        return obj instanceof HotpotSoupRendererConfigSpriteProcessorConfig config && soupRendererConfigResourceLocation.equals(config.soupRendererConfigResourceLocation) && processorResourceLocation.equals(config.processorResourceLocation) && resourceLocation.equals(config.resourceLocation);
    }

    public static class Serializer implements IHotpotSpriteProcessorConfigSerializer<HotpotSoupRendererConfigSpriteProcessorConfig> {
        public static final MapCodec<HotpotSoupRendererConfigSpriteProcessorConfig> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(config -> config.group(
                        ResourceLocation.CODEC.fieldOf("soup_renderer_config").forGetter(HotpotSoupRendererConfigSpriteProcessorConfig::soupRendererConfigResourceLocation),
                        ResourceLocation.CODEC.fieldOf("processor_resource_location").forGetter(HotpotSoupRendererConfigSpriteProcessorConfig::processorResourceLocation),
                        ResourceLocation.CODEC.fieldOf("resource_location").forGetter(HotpotSoupRendererConfigSpriteProcessorConfig::resourceLocation)
                ).apply(config, HotpotSoupRendererConfigSpriteProcessorConfig::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRendererConfigSpriteProcessorConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, HotpotSoupRendererConfigSpriteProcessorConfig::soupRendererConfigResourceLocation,
                        ResourceLocation.STREAM_CODEC, HotpotSoupRendererConfigSpriteProcessorConfig::processorResourceLocation,
                        ResourceLocation.STREAM_CODEC, HotpotSoupRendererConfigSpriteProcessorConfig::resourceLocation,
                        HotpotSoupRendererConfigSpriteProcessorConfig::new
                )
        );

        @Override
        public MapCodec<HotpotSoupRendererConfigSpriteProcessorConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRendererConfigSpriteProcessorConfig> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
