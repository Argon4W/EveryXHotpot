package com.github.argon4w.hotpot.client.items.sprites.processors;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    public static Registry<IHotpotSpriteProcessor> getSpriteProcessorRegistry() {
        return SPRITE_PROCESSOR_REGISTRY;
    }
}
