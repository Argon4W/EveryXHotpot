package com.github.argon4w.hotpot.soups.recipes.effects;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HotpotRandomMobEffectMap extends HashMap<Integer, MobEffectInstance> {
    public static final Codec<HotpotRandomMobEffectMap> CODEC = Codec.lazyInitialized(() -> Codec.INT.dispatch("index", Map.Entry::getKey, i -> MobEffectInstance.CODEC.xmap(mobEffectInstance -> Map.entry(i, mobEffectInstance), Map.Entry::getValue).fieldOf("effect")).listOf().xmap(HotpotRandomMobEffectMap::new, map -> List.copyOf(map.entrySet())).fieldOf("effects").codec());
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotRandomMobEffectMap> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.INT.<RegistryFriendlyByteBuf>cast().dispatch(Map.Entry::getKey, i -> MobEffectInstance.STREAM_CODEC.map(mobEffectInstance -> Map.entry(i, mobEffectInstance), Map.Entry::getValue)).apply(ByteBufCodecs.list()).map(HotpotRandomMobEffectMap::new, map -> List.copyOf(map.entrySet())));
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public static final ResourceKey<Registry<HotpotRandomMobEffectMap>> RANDOM_MOB_EFFECT_MAP_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "random_mob_effect"));
    public static final Codec<Holder<HotpotRandomMobEffectMap>> HOLDER_CODEC = Codec.lazyInitialized(() -> RegistryFileCodec.create(RANDOM_MOB_EFFECT_MAP_REGISTRY_KEY, CODEC));
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<HotpotRandomMobEffectMap>> HOLDER_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holder(RANDOM_MOB_EFFECT_MAP_REGISTRY_KEY, STREAM_CODEC));

    public HotpotRandomMobEffectMap(List<Map.Entry<Integer, MobEffectInstance>> entries) {
        entries.forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    public Optional<MobEffectInstance> getEffect(IHotpotRandomMobEffectKey key) {
        return key.getEffect(this);
    }

    public Optional<MobEffectInstance> getRandom(int from, int to) {
        return getClosest(RANDOM_SOURCE.nextInt(from, to + 1));
    }

    public Optional<MobEffectInstance> getClosest(int key) {
        return keySet().stream().reduce((int1, int2) -> Math.abs(int1 - key) < Math.abs(int2 - key) ? int1 : int2).map(this::get);
    }
}
