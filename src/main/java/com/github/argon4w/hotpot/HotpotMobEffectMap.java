package com.github.argon4w.hotpot;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class HotpotMobEffectMap extends LinkedHashMap<Holder<MobEffect>, MobEffectInstance> {
    public static final Codec<HotpotMobEffectMap> CODEC = Codec.lazyInitialized(() -> MobEffectInstance.CODEC.listOf().xmap(HotpotMobEffectMap::new, HotpotMobEffectMap::getMobEffects));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotMobEffectMap> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()).map(HotpotMobEffectMap::new, HotpotMobEffectMap::getMobEffects));

    public HotpotMobEffectMap() {

    }

    public HotpotMobEffectMap(Collection<MobEffectInstance> mobEffects) {
        putEffects(mobEffects);
    }

    public HotpotMobEffectMap putEffects(Collection<MobEffectInstance> mobEffectInstances) {
        mobEffectInstances.forEach(this::putEffect);
        return this;
    }

    public HotpotMobEffectMap putEffects(HotpotMobEffectMap hotpotMobEffectMap) {
        return putEffects(hotpotMobEffectMap.values());
    }

    public void putEffect(MobEffectInstance mobEffectInstance) {
        keySet().stream().filter(holder -> holder.equals(mobEffectInstance.getEffect())).findFirst().ifPresentOrElse(holder -> get(holder).update(new MobEffectInstance(mobEffectInstance)), () -> putLast(mobEffectInstance.getEffect(), new MobEffectInstance(mobEffectInstance)));
    }

    public HotpotMobEffectMap copy() {
        return new HotpotMobEffectMap(getMobEffects());
    }

    public List<MobEffectInstance> getMobEffects() {
        return values().stream().map(MobEffectInstance::new).toList();
    }

    public static Codec<Sized> getSizedCodec(int size) {
        return Codec.lazyInitialized(() -> MobEffectInstance.CODEC.listOf(0, size).xmap(list -> new Sized(size), HotpotMobEffectMap::getMobEffects));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, Sized> getSizedStreamCodec(int size) {
        return NeoForgeStreamCodecs.lazy(() -> MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list(size)).map(list -> new Sized(size), HotpotMobEffectMap::getMobEffects));
    }

    public static class Sized extends HotpotMobEffectMap {
        private final int size;

        public Sized(int size) {
            this.size = size;
        }

        public Sized(int size, Collection<MobEffectInstance> mobEffectInstances) {
            this.size = size;
            putEffects(mobEffectInstances);
        }

        @Override
        public void putEffect(MobEffectInstance mobEffectInstance) {
            super.putEffect(mobEffectInstance);

            while (size() > size) {
                pollFirstEntry();
            }
        }

        @Override
        public Sized copy() {
            return new Sized(size(), getMobEffects());
        }
    }
}
