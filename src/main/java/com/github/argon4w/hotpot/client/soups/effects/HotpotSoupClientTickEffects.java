package com.github.argon4w.hotpot.client.soups.effects;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class HotpotSoupClientTickEffects {
    public static final ResourceLocation EMPTY_SOUP_CLIENT_TICK_EFFECT_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup_client_tick_effect");
    public static final Codec<IHotpotSoupClientTickEffect> CODEC = Codec.lazyInitialized(() -> getSoupClientTickEffectSerializerRegistry().holderByNameCodec().dispatch(IHotpotSoupClientTickEffect::getSerializerHolder, holder -> holder.value().getCodec()));

    public static final ResourceKey<Registry<IHotpotSoupClientTickEffectSerializer<?>>> SOUP_CLIENT_TICK_EFFECT_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup_client_tick_effect_serializer"));
    public static final DeferredRegister<IHotpotSoupClientTickEffectSerializer<?>> SOUP_CLIENT_TICK_EFFECT_SERIALIZERS = DeferredRegister.create(SOUP_CLIENT_TICK_EFFECT_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupClientTickEffectSerializer<?>> SOUP_CLIENT_TICK_EFFECT_SERIALIZER_REGISTRY = SOUP_CLIENT_TICK_EFFECT_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_SOUP_CLIENT_TICK_EFFECT_SERIALIZER_LOCATION));

    public static final DeferredHolder<IHotpotSoupClientTickEffectSerializer<?>, HotpotEmptySoupClientTickEffect.Serializer> EMPTY_SOUP_CLIENT_TICK_EFFECT_SERIALIZER = SOUP_CLIENT_TICK_EFFECT_SERIALIZERS.register("empty_soup_client_tick_effect", HotpotEmptySoupClientTickEffect.Serializer::new);
    public static final DeferredHolder<IHotpotSoupClientTickEffectSerializer<?>, HotpotEmptyWaterSmokeEffect.Serializer> EMPTY_WATER_SMOKE_EFFECT_SERIALIZER = SOUP_CLIENT_TICK_EFFECT_SERIALIZERS.register("empty_water_smoke_effect", HotpotEmptyWaterSmokeEffect.Serializer::new);
    public static final DeferredHolder<IHotpotSoupClientTickEffectSerializer<?>, HotpotVaporEffect.Serializer> VAPOR_EFFECT_SERIALIZER = SOUP_CLIENT_TICK_EFFECT_SERIALIZERS.register("vapor_effect", HotpotVaporEffect.Serializer::new);

    public static Registry<IHotpotSoupClientTickEffectSerializer<?>> getSoupClientTickEffectSerializerRegistry() {
        return SOUP_CLIENT_TICK_EFFECT_SERIALIZER_REGISTRY;
    }
}
