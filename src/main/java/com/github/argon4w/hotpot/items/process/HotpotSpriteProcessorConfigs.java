package com.github.argon4w.hotpot.items.process;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSpriteProcessorConfigs {
    public static final ResourceKey<Registry<IHotpotSpriteProcessorConfigSerializer<?>>> SPRITE_PROCESSOR_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "sprite_processor_config"));

    public static final Codec<IHotpotSpriteProcessorConfig> CODEC = Codec.lazyInitialized(() -> getSpriteProcessorConfigRegistry().holderByNameCodec().dispatch(IHotpotSpriteProcessorConfig::getSerializerHolder, holder -> holder.value().getCodec()));
    public static final StreamCodec<RegistryFriendlyByteBuf, IHotpotSpriteProcessorConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(SPRITE_PROCESSOR_CONFIG_REGISTRY_KEY).dispatch(IHotpotSpriteProcessorConfig::getSerializerHolder, holder -> holder.value().getStreamCodec()));

    public static final ResourceLocation EMPTY_SPRITE_PROCESSOR_CONFIG_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor_config");
    public static final DeferredRegister<IHotpotSpriteProcessorConfigSerializer<?>> SPRITE_PROCESSOR_CONFIGS = DeferredRegister.create(SPRITE_PROCESSOR_CONFIG_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSpriteProcessorConfigSerializer<?>> SPRITE_PROCESSOR_CONFIG_REGISTRY = SPRITE_PROCESSOR_CONFIGS.makeRegistry(builder -> builder.defaultKey(EMPTY_SPRITE_PROCESSOR_CONFIG_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotSpriteProcessorConfigSerializer<?>, HotpotSoupRendererConfigSpriteProcessorConfig.Serializer> SOUP_TYPE_PROCESSOR_CONFIG = SPRITE_PROCESSOR_CONFIGS.register("soup_renderer_config_processor_config", HotpotSoupRendererConfigSpriteProcessorConfig.Serializer::new);
    public static final DeferredHolder<IHotpotSpriteProcessorConfigSerializer<?>, HotpotCustomColorSpriteProcessorConfig.Serializer> CUSTOM_COLOR_PROCESSOR_CONFIG = SPRITE_PROCESSOR_CONFIGS.register("custom_color_processor_config", HotpotCustomColorSpriteProcessorConfig.Serializer::new);
    public static final DeferredHolder<IHotpotSpriteProcessorConfigSerializer<?>, HotpotEmptySpriteProcessorConfig.Serializer> EMPTY_SPRITE_PROCESSOR_CONFIG = SPRITE_PROCESSOR_CONFIGS.register("empty_sprite_processor_config", HotpotEmptySpriteProcessorConfig.Serializer::new);

    public static HotpotEmptySpriteProcessorConfig getEmptySpriteProcessorConfig() {
        return getEmptySpriteProcessorConfigSerializer().get();
    }

    public static HotpotEmptySpriteProcessorConfig.Serializer getEmptySpriteProcessorConfigSerializer() {
        return EMPTY_SPRITE_PROCESSOR_CONFIG.get();
    }

    public static Registry<IHotpotSpriteProcessorConfigSerializer<?>> getSpriteProcessorConfigRegistry() {
        return SPRITE_PROCESSOR_CONFIG_REGISTRY;
    }
}
