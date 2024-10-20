package com.github.argon4w.hotpot.items.sprites;

import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfigSerializer;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupRendererConfigSpriteConfig(ResourceLocation id, ResourceLocation soupRendererConfigResourceLocation) implements IHotpotSpriteConfig {
    @Override
    public Holder<IHotpotSpriteConfigSerializer<?>> getSerializerHolder() {
        return HotpotSpriteConfigSerializers.SOUP_RENDERER_CONFIG_SPRITE_CONFIG_SERIALIZER;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSoupRendererConfigSpriteConfig config && soupRendererConfigResourceLocation.equals(config.soupRendererConfigResourceLocation) && id.equals(config.id);
    }

    public static class Serializer implements IHotpotSpriteConfigSerializer<HotpotSoupRendererConfigSpriteConfig> {
        public static final MapCodec<HotpotSoupRendererConfigSpriteConfig> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(config -> config.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(HotpotSoupRendererConfigSpriteConfig::id),
                        ResourceLocation.CODEC.fieldOf("soup_renderer_config_resource_location").forGetter(HotpotSoupRendererConfigSpriteConfig::soupRendererConfigResourceLocation)
                ).apply(config, HotpotSoupRendererConfigSpriteConfig::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRendererConfigSpriteConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, HotpotSoupRendererConfigSpriteConfig::id,
                        ResourceLocation.STREAM_CODEC, HotpotSoupRendererConfigSpriteConfig::soupRendererConfigResourceLocation,
                        HotpotSoupRendererConfigSpriteConfig::new
                )
        );

        @Override
        public MapCodec<HotpotSoupRendererConfigSpriteConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRendererConfigSpriteConfig> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
