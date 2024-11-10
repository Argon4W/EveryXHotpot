package com.github.argon4w.fancytoys;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class MobEffectMap extends LinkedHashMap<Holder<MobEffect>, MobEffectInstance> {

    public static final Codec<MobEffectMap> CODEC = Codec.lazyInitialized(() -> MobEffectInstance.CODEC
            .listOf()
            .xmap(MobEffectMap::new, MobEffectMap::getMobEffects));

    public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectMap> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> MobEffectInstance.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(MobEffectMap::new, MobEffectMap::getMobEffects));

    public MobEffectMap() {

    }

    public MobEffectMap(Collection<MobEffectInstance> mobEffects) {
        putEffects(mobEffects);
    }

    public void putEffect(MobEffectInstance mobEffectInstance) {
        keySet().stream()
                .filter(holder -> holder.equals(mobEffectInstance.getEffect()))
                .findFirst()
                .ifPresentOrElse(holder -> get(holder).update(new MobEffectInstance(mobEffectInstance)), () -> putLast(mobEffectInstance.getEffect(), new MobEffectInstance(mobEffectInstance)));
    }

    public MobEffectMap putEffects(Collection<MobEffectInstance> mobEffectInstances) {
        mobEffectInstances.forEach(this::putEffect);
        return this;
    }

    public MobEffectMap putEffects(MobEffectMap mobEffectMap) {
        return putEffects(mobEffectMap.values());
    }

    public MobEffectMap copy() {
        return new MobEffectMap(getMobEffects());
    }

    public List<MobEffectInstance> getMobEffects() {
        return values().stream().map(MobEffectInstance::new).toList();
    }

    public static Codec<Sized> getCodec(int size) {
        return Codec.lazyInitialized(() -> MobEffectInstance.CODEC
                .listOf(0, size)
                .xmap(list -> new Sized(size), MobEffectMap::getMobEffects));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, Sized> getStreamCodec(int size) {
        return NeoForgeStreamCodecs.lazy(() -> MobEffectInstance.STREAM_CODEC
                .apply(ByteBufCodecs.list(size))
                .map(list -> new Sized(size), MobEffectMap::getMobEffects));
    }

    public static class Sized extends MobEffectMap {

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
