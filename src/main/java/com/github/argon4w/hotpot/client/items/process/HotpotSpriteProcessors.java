package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotHeavySaucedSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotLightSaucedSpriteProcessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSpriteProcessors {
    public static final ResourceKey<Registry<IHotpotSpriteProcessor>> SPRITE_PROCESSOR_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "sprite_processor"));

    public static final ResourceLocation EMPTY_SPRITE_PROCESSOR_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor");
    public static final DeferredRegister<IHotpotSpriteProcessor> SPRITE_PROCESSORS = DeferredRegister.create(SPRITE_PROCESSOR_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSpriteProcessor> SPRITE_PROCESSOR_REGISTRY = SPRITE_PROCESSORS.makeRegistry(builder -> builder.defaultKey(EMPTY_SPRITE_PROCESSOR_LOCATION));

    public static final DeferredHolder<IHotpotSpriteProcessor, HotpotLightSaucedSpriteProcessor> LIGHT_SAUCED_PROCESSOR = SPRITE_PROCESSORS.register("light_sauced_processor", HotpotLightSaucedSpriteProcessor::new);
    public static final DeferredHolder<IHotpotSpriteProcessor, HotpotHeavySaucedSpriteProcessor> HEAVY_SAUCED_PROCESSOR = SPRITE_PROCESSORS.register("heavy_sauced_processor", HotpotHeavySaucedSpriteProcessor::new);
    public static final DeferredHolder<IHotpotSpriteProcessor, HotpotEmptySpriteProcessor> EMPTY_SPRITE_PROCESSOR = SPRITE_PROCESSORS.register("empty_sprite_processor", HotpotEmptySpriteProcessor::new);

    public static IHotpotSpriteProcessor getEmptySpriteProcessor() {
        return EMPTY_SPRITE_PROCESSOR.get();
    }

    public static IHotpotSpriteProcessor getSpriteProcessor(ResourceLocation resourceLocation) {
        return getSpriteProcessorRegistry().get(resourceLocation);
    }

    public static Registry<IHotpotSpriteProcessor> getSpriteProcessorRegistry() {
        return SPRITE_PROCESSOR_REGISTRY;
    }
}
