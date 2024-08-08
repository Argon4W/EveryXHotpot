package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.types.HotpotCookingRecipeSoup;
import com.github.argon4w.hotpot.soups.types.HotpotDisassemblingRecipeSoupTypeRecipeSoup;
import com.github.argon4w.hotpot.soups.types.HotpotEmptySoup;
import com.github.argon4w.hotpot.soups.types.HotpotSmeltingRecipeSoup;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSoupTypeSerializers {
    public static final ResourceLocation EMPTY_SOUP_TYPE_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_soup");

    public static final ResourceKey<Registry<IHotpotSoupTypeSerializer<?>>> SOUP_TYPE_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "soup_type_serializer"));
    public static final DeferredRegister<IHotpotSoupTypeSerializer<?>> SOUP_TYPE_SERIALIZERS = DeferredRegister.create(SOUP_TYPE_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupTypeSerializer<?>> SOUP_TYPE_SERIALIZER_REGISTRY = SOUP_TYPE_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_SOUP_TYPE_SERIALIZER_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotSoupTypeSerializer<?>, HotpotDisassemblingRecipeSoupTypeRecipeSoup.Serializer> DISASSEMBLING_RECIPE_SOUP_TYPE_SERIALIZER = SOUP_TYPE_SERIALIZERS.register("disassembling_recipe_soup", HotpotDisassemblingRecipeSoupTypeRecipeSoup.Serializer::new);
    public static final DeferredHolder<IHotpotSoupTypeSerializer<?>, HotpotCookingRecipeSoup.Serializer> COOKING_RECIPE_SOUP_TYPE_SERIALIZER = SOUP_TYPE_SERIALIZERS.register("cooking_recipe_soup", HotpotCookingRecipeSoup.Serializer::new);
    public static final DeferredHolder<IHotpotSoupTypeSerializer<?>, HotpotSmeltingRecipeSoup.Serializer> SMELTING_RECIPE_SOUP_TYPE_SERIALIZER = SOUP_TYPE_SERIALIZERS.register("smelting_recipe_soup", HotpotSmeltingRecipeSoup.Serializer::new);
    public static final DeferredHolder<IHotpotSoupTypeSerializer<?>, HotpotEmptySoup.Serializer> EMPTY_SOUP_TYPE_SERIALIZER = SOUP_TYPE_SERIALIZERS.register("empty_soup", HotpotEmptySoup.Serializer::new);

    public static Registry<IHotpotSoupTypeSerializer<?>> getSoupTypeSerializerRegistry() {
        return SOUP_TYPE_SERIALIZER_REGISTRY;
    }

    public static HotpotSoupTypeHolder<HotpotEmptySoup> getEmptySoupTypeHolder() {
        return HotpotSoupTypeManager.EMPTY_SOUP_TYPE;
    }

    public static HotpotEmptySoup buildEmptySoup() {
        return HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.buildEmptySoup();
    }

    public static IHotpotSoup loadSoup(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        return HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.buildSoup(compoundTag, registryAccess);
    }

    public static Tag saveSoup(IHotpotSoup soupType, HolderLookup.Provider registryAccess) {
        return HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.saveSoup(soupType, registryAccess);
    }

    public static Codec<HotpotSoupTypeHolder<?>> getHolderCodec() {
        return HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.getHolderCodec();
    }

    public static StreamCodec<ByteBuf, HotpotSoupTypeHolder<?>> getStreamHolderCodec() {
        return HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.getStreamHolderCodec();
    }
}
