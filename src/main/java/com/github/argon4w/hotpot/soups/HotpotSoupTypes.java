package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSoupTypes {
    public static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");

    public static final ResourceKey<Registry<IHotpotSoupFactorySerializer<?>>> SOUP_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup_type"));
    public static final DeferredRegister<IHotpotSoupFactorySerializer<?>> SOUPS = DeferredRegister.create(SOUP_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupFactorySerializer<?>> SOUP_REGISTRY = SOUPS.makeRegistry(builder -> builder.defaultKey(EMPTY_SOUP_LOCATION));

    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Serializer> DISASSEMBLING_RECIPE_SOUP_SERIALIZER = SOUPS.register("disassembling_recipe_soup", HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotCookingRecipeSoupType.Serializer> COOKING_RECIPE_SOUP_SERIALIZER = SOUPS.register("cooking_recipe_soup", HotpotCookingRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotSmeltingRecipeSoupType.Serializer> SMELTING_RECIPE_SOUP_SERIALIZER = SOUPS.register("smelting_recipe_soup", HotpotSmeltingRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotEmptySoupType.Serializer> EMPTY_SOUP_SERIALIZER = SOUPS.register("empty_soup", HotpotEmptySoupType.Serializer::new);

    public static Registry<IHotpotSoupFactorySerializer<?>> getSoupTypeRegistry() {
        return SOUP_REGISTRY;
    }

    public static IHotpotSoupType loadSoup(CompoundTag compoundTag) {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildSoup(compoundTag);
    }

    public static Tag saveSoup(IHotpotSoupType soupType, HolderLookup.Provider registryAccess) {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.saveSoup(soupType, registryAccess);
    }
}
