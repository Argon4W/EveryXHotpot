package com.github.argon4w.hotpot.items.sprites;

import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfigSerializer;
import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotCustomColorSpriteConfig(ResourceLocation id, HotpotColor color, ResourceLocation processorResourceLocation) implements IHotpotSpriteConfig {
    public ResourceLocation getProcessorResourceLocation() {
        return processorResourceLocation;
    }

    @Override
    public Holder<IHotpotSpriteConfigSerializer<?>> getSerializerHolder() {
        return HotpotSpriteConfigSerializers.CUSTOM_COLOR_SPRITE_CONFIG_SERIALIZER;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotCustomColorSpriteConfig config && config.color.equals(color) && config.processorResourceLocation.equals(processorResourceLocation) && config.id.equals(id);
    }

    public static class Serializer implements IHotpotSpriteConfigSerializer<HotpotCustomColorSpriteConfig> {
        public static final MapCodec<HotpotCustomColorSpriteConfig> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(config -> config.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(HotpotCustomColorSpriteConfig::id),
                        HotpotColor.CODEC.fieldOf("color").forGetter(HotpotCustomColorSpriteConfig::color),
                        ResourceLocation.CODEC.fieldOf("processor_resource_location").forGetter(HotpotCustomColorSpriteConfig::processorResourceLocation)
                ).apply(config, HotpotCustomColorSpriteConfig::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotCustomColorSpriteConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteConfig::id,
                        HotpotColor.STREAM_CODEC, HotpotCustomColorSpriteConfig::color,
                        ResourceLocation.STREAM_CODEC, HotpotCustomColorSpriteConfig::processorResourceLocation,
                        HotpotCustomColorSpriteConfig::new
                )
        );

        @Override
        public MapCodec<HotpotCustomColorSpriteConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotCustomColorSpriteConfig> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
