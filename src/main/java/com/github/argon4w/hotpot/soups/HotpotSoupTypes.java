package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.*;

public class HotpotSoupTypes {
    public static final ResourceLocation EMPTY_SOUP_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_soup");

    public static final ResourceKey<Registry<IHotpotSoupTypeSerializer<?>>> SOUP_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "soup_type"));
    public static final DeferredRegister<IHotpotSoupTypeSerializer<?>> SOUPS = DeferredRegister.create(SOUP_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotSoupTypeSerializer<?>>> SOUP_REGISTRY = SOUPS.makeRegistry(() -> new RegistryBuilder<IHotpotSoupTypeSerializer<?>>().setDefaultKey(EMPTY_SOUP_LOCATION));

    public static final RegistryObject<HotpotCookingRecipeSoupType.Serializer> COOKING_RECIPE_SOUP_SERIALIZER = SOUPS.register("cooking_recipe_soup", HotpotCookingRecipeSoupType.Serializer::new);
    public static final RegistryObject<HotpotSmeltingRecipeSoupType.Serializer> SMELTING_RECIPE_SOUP_SERIALIZER = SOUPS.register("smelting_recipe_soup", HotpotSmeltingRecipeSoupType.Serializer::new);
    public static final RegistryObject<HotpotEmptySoupType.Serializer> EMPTY_SOUP_SERIALIZER = SOUPS.register("empty_soup", HotpotEmptySoupType.Serializer::new);

    public static HotpotEmptySoupType.Serializer getEmptySoupTypeSerializer() {
        return EMPTY_SOUP_SERIALIZER.get();
    }

    public static IForgeRegistry<IHotpotSoupTypeSerializer<?>> getSoupTypeRegistry() {
        return SOUP_REGISTRY.get();
    }

    public static IHotpotSoupTypeSerializer<?> getSoupTypeSerializer(ResourceLocation resourceLocation) {
        return getSoupTypeRegistry().getValue(resourceLocation);
    }
}
