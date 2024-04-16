package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotHeavySaucedSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotLightSaucedSpriteProcessor;
import com.github.argon4w.hotpot.client.items.renderers.HotpotChopstickRenderer;
import com.github.argon4w.hotpot.client.items.renderers.HotpotEmptyItemSpecialRenderer;
import com.github.argon4w.hotpot.client.items.renderers.HotpotPaperBowlRenderer;
import com.github.argon4w.hotpot.client.items.renderers.HotpotSpicePackRenderer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotSpriteProcessors {
    public static final ResourceLocation EMPTY_SPRITE_PROCESSOR_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_sprite_processor");

    public static final ResourceKey<Registry<IHotpotSpriteProcessor>> SPRITE_PROCESSOR_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "sprite_processor"));
    public static final DeferredRegister<IHotpotSpriteProcessor> SPRITE_PROCESSORS = DeferredRegister.create(SPRITE_PROCESSOR_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotSpriteProcessor>> SPRITE_PROCESSOR_REGISTRY = SPRITE_PROCESSORS.makeRegistry(() -> new RegistryBuilder<IHotpotSpriteProcessor>().setDefaultKey(EMPTY_SPRITE_PROCESSOR_LOCATION));

    public static final RegistryObject<IHotpotSpriteProcessor> LIGHT_SAUCED_PROCESSOR = SPRITE_PROCESSORS.register("light_sauced_processor", HotpotLightSaucedSpriteProcessor::new);
    public static final RegistryObject<IHotpotSpriteProcessor> HEAVY_SAUCED_PROCESSOR = SPRITE_PROCESSORS.register("heavy_sauced_processor", HotpotHeavySaucedSpriteProcessor::new);
    public static final RegistryObject<IHotpotSpriteProcessor> EMPTY_SPRITE_PROCESSOR = SPRITE_PROCESSORS.register("empty_sprite_processor", HotpotEmptySpriteProcessor::new);

    public static IHotpotSpriteProcessor getEmptySpriteProcessor() {
        return EMPTY_SPRITE_PROCESSOR.get();
    }

    public static IForgeRegistry<IHotpotSpriteProcessor> getSpriteProcessorRegistry() {
        return SPRITE_PROCESSOR_REGISTRY.get();
    }

    public static IHotpotSpriteProcessor getSpriteProcessor(ResourceLocation resourceLocation) {
        return getSpriteProcessorRegistry().getValue(resourceLocation);
    }

    public static void applyProcessor(ResourceLocation processorResourceLocation, ItemStack itemStack) {
        HotpotTagsHelper.updateHotpotTag(itemStack, hotpotTags -> {
            CompoundTag processed = hotpotTags.contains("Processed") ? hotpotTags.getCompound("Processed") : new CompoundTag();
            processed.putBoolean(processorResourceLocation.toString(), true);
            hotpotTags.put("Processed", processed);
        });
    }
}
