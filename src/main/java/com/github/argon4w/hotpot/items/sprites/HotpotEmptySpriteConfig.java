package com.github.argon4w.hotpot.items.sprites;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotEmptySpriteConfig() implements IHotpotSpriteConfig {
    @Override
    public Holder<IHotpotSpriteConfigSerializer<?>> getSerializerHolder() {
        return HotpotSpriteConfigSerializers.EMPTY_SPRITE_CONFIG_SERIALIZER;
    }

    @Override
    public ResourceLocation getProcessorResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_config");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotEmptySpriteConfig;
    }

    public static class Serializer implements IHotpotSpriteConfigSerializer<HotpotEmptySpriteConfig> {
        public static final MapCodec<HotpotEmptySpriteConfig> CODEC = MapCodec.unit(HotpotEmptySpriteConfig::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotEmptySpriteConfig> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {}, pBuffer -> new HotpotEmptySpriteConfig());

        @Override
        public MapCodec<HotpotEmptySpriteConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotEmptySpriteConfig> getStreamCodec() {
            return STREAM_CODEC;
        }

        public HotpotEmptySpriteConfig get() {
            return new HotpotEmptySpriteConfig();
        }
    }
}
