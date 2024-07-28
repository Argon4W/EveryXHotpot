package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class HotpotSoupTypes {
    public static final ResourceKey<Registry<IHotpotSoupTypeFactory<?>>> SOUP_FACTORY_DATA_PACK_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soups"));
    public static final Codec<IHotpotSoupTypeFactory<?>> CODEC = Codec.lazyInitialized(() -> getSoupTypeRegistry().byNameCodec().dispatch(IHotpotSoupTypeFactory::getSerializer, IHotpotSoupFactorySerializer::getCodec));
    public static final Codec<Holder<IHotpotSoupTypeFactory<?>>> HOLDER_CODEC = Codec.lazyInitialized(() -> RegistryFixedCodec.create(SOUP_FACTORY_DATA_PACK_REGISTRY_KEY));
    public static final Codec<HotpotHolderSoupTypeTypeFactory<?>> HOLDER_FACTORY_CODEC = Codec.lazyInitialized(() -> HOLDER_CODEC.xmap(HotpotHolderSoupTypeTypeFactory::new, HotpotHolderSoupTypeTypeFactory::holder));

    public static final ResourceLocation EMPTY_SOUP_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");

    public static final ResourceKey<Registry<IHotpotSoupFactorySerializer<?>>> SOUP_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup_type"));
    public static final DeferredRegister<IHotpotSoupFactorySerializer<?>> SOUPS = DeferredRegister.create(SOUP_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupFactorySerializer<?>> SOUP_REGISTRY = SOUPS.makeRegistry(builder -> builder.defaultKey(EMPTY_SOUP_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Serializer> DISASSEMBLING_RECIPE_SOUP_SERIALIZER = SOUPS.register("disassembling_recipe_soup", HotpotDisassemblingRecipeSoupTypeRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotCookingRecipeSoupType.Serializer> COOKING_RECIPE_SOUP_SERIALIZER = SOUPS.register("cooking_recipe_soup", HotpotCookingRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotSmeltingRecipeSoupType.Serializer> SMELTING_RECIPE_SOUP_SERIALIZER = SOUPS.register("smelting_recipe_soup", HotpotSmeltingRecipeSoupType.Serializer::new);
    public static final DeferredHolder<IHotpotSoupFactorySerializer<?>, HotpotEmptySoupType.Serializer> EMPTY_SOUP_SERIALIZER = SOUPS.register("empty_soup", HotpotEmptySoupType.Serializer::new);

    public static Registry<IHotpotSoupFactorySerializer<?>> getSoupTypeRegistry() {
        return SOUP_REGISTRY;
    }

    public static HotpotWrappedSoupTypeTypeFactory<HotpotEmptySoupType> getEmptySoupFactory() {
        return HotpotSoupTypeFactoryManager.EMPTY_SOUP_FACTORY;
    }

    public static HotpotEmptySoupType buildEmptySoup() {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildEmptySoup();
    }

    public static IHotpotSoupType loadSoup(CompoundTag compoundTag) {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildSoup(compoundTag);
    }

    public static Tag saveSoup(IHotpotSoupType soupType, HolderLookup.Provider registryAccess) {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.saveSoup(soupType, registryAccess);
    }
}
