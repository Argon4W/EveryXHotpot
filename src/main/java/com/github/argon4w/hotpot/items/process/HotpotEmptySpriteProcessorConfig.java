package com.github.argon4w.hotpot.items.process;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotEmptySpriteProcessorConfig() implements IHotpotSpriteProcessorConfig {
    @Override
    public Holder<IHotpotSpriteProcessorConfigSerializer<?>> getSerializerHolder() {
        return HotpotSpriteProcessorConfigs.EMPTY_SPRITE_PROCESSOR_CONFIG;
    }

    @Override
    public ResourceLocation getProcessorResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotEmptySpriteProcessorConfig;
    }

    public static class Serializer implements IHotpotSpriteProcessorConfigSerializer<HotpotEmptySpriteProcessorConfig> {
        public static final MapCodec<HotpotEmptySpriteProcessorConfig> CODEC = MapCodec.unit(HotpotEmptySpriteProcessorConfig::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotEmptySpriteProcessorConfig> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {}, pBuffer -> new HotpotEmptySpriteProcessorConfig());

        @Override
        public MapCodec<HotpotEmptySpriteProcessorConfig> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotEmptySpriteProcessorConfig> getStreamCodec() {
            return STREAM_CODEC;
        }

        public HotpotEmptySpriteProcessorConfig get() {
            return new HotpotEmptySpriteProcessorConfig();
        }
    }
}
