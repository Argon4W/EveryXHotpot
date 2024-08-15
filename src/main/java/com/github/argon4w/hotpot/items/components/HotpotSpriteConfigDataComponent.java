package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.sprites.HotpotEmptySpriteConfig;
import com.github.argon4w.hotpot.items.sprites.HotpotSpriteConfigSerializers;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record HotpotSpriteConfigDataComponent(List<IHotpotSpriteConfig> spriteConfigs) {
    public static final HotpotSpriteConfigDataComponent EMPTY = new HotpotSpriteConfigDataComponent(new ArrayList<>());

    public static final Codec<HotpotSpriteConfigDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotSpriteConfigSerializers.CODEC.listOf().fieldOf("sprite_configs").forGetter(HotpotSpriteConfigDataComponent::spriteConfigs)
            ).apply(data, HotpotSpriteConfigDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSpriteConfigDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, HotpotSpriteConfigSerializers.STREAM_CODEC), HotpotSpriteConfigDataComponent::spriteConfigs,
                    HotpotSpriteConfigDataComponent::new
            )
    );

    public HotpotSpriteConfigDataComponent addSpriteConfig(IHotpotSpriteConfig spriteConfig) {
        return spriteConfig instanceof HotpotEmptySpriteConfig ? this : contains(spriteConfig) ? replaceSpriteConfig(spriteConfig) : new HotpotSpriteConfigDataComponent(Stream.concat(spriteConfigs.stream(), Stream.of(spriteConfig)).toList());
    }

    public HotpotSpriteConfigDataComponent replaceSpriteConfig(IHotpotSpriteConfig spriteConfig) {
        return new HotpotSpriteConfigDataComponent(spriteConfigs.stream().map(config -> config.getResourceLocation().equals(spriteConfig.getResourceLocation()) ? spriteConfig : config).toList());
    }

    public boolean contains(IHotpotSpriteConfig spriteConfig) {
        return spriteConfigs.stream().anyMatch(config -> config.getResourceLocation().equals(spriteConfig.getResourceLocation()));
    }

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_SPRITE_CONFIG_DATA_COMPONENT);
    }

    public static HotpotSpriteConfigDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SPRITE_CONFIG_DATA_COMPONENT, EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotSpriteConfigDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_SPRITE_CONFIG_DATA_COMPONENT, dataComponent);
    }

    public static List<IHotpotSpriteConfig> getSpriteConfigs(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).spriteConfigs);
    }

    public static void addSpriteConfig(ItemStack itemStack, IHotpotSpriteConfig spriteConfig) {
        setDataComponent(itemStack, getDataComponent(itemStack).addSpriteConfig(spriteConfig));
    }

    public static void addSpriteConfigs(ItemStack itemStack, List<IHotpotSpriteConfig> spriteConfigs) {
        spriteConfigs.forEach(spriteConfig -> addSpriteConfig(itemStack, spriteConfig));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSpriteConfigDataComponent data && spriteConfigs.equals(data.spriteConfigs);
    }
}
