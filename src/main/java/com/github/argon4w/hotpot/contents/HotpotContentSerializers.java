package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.IndexHolder;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotContentSerializers {
    public static final ResourceKey<Registry<IHotpotContentSerializer<?>>> CONTENT_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "content_serializer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IHotpotContentSerializer<?>>> SERIALIZER_HOLDER_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(CONTENT_SERIALIZER_REGISTRY_KEY));
    public static final Codec<Holder<IHotpotContentSerializer<?>>> SERIALIZER_HOLDER_CODEC = Codec.lazyInitialized(() -> getContentSerializerRegistry().holderByNameCodec());

    public static final MapCodec<IHotpotContent> CODEC = LazyMapCodec.of(() -> SERIALIZER_HOLDER_CODEC.dispatchMap(IHotpotContent::getContentSerializerHolder, holder -> holder.value().getCodec()));
    public static final Codec<IndexHolder<IHotpotContent>> HOLDER_CODEC = Codec.lazyInitialized(() -> IndexHolder.getIndexedCodec(CODEC, "slot"));
    public static final Codec<NonNullList<IHotpotContent>> HOTPOT_CONTENTS_CODEC = Codec.lazyInitialized(() -> IndexHolder.getSizedNonNullCodec(HOLDER_CODEC, 8, loadEmptyContent()));

    public static final ResourceLocation EMPTY_CONTENT_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_content");

    public static final DeferredRegister<IHotpotContentSerializer<?>> CONTENT_SERIALIZERS = DeferredRegister.create(CONTENT_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotContentSerializer<?>> CONTENT_SERIALIZER_REGISTRY = CONTENT_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_CONTENT_SERIALIZER_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotCampfireRecipeContent.Serializer> CAMPFIRE_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("campfire_recipe_content", HotpotCampfireRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotBlastingRecipeContent.Serializer> SMELTING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("blasting_recipe_content", HotpotBlastingRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotCookingRecipeContent.Serializer> COOKING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("cooking_recipe_content", HotpotCookingRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotDisassemblingContent.Serializer> DISASSEMBLING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("disassembling_recipe_content", HotpotDisassemblingContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotPiglinBarterRecipeContent.Serializer> PIGLIN_BARTER_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("piglin_barter_recipe_content", HotpotPiglinBarterRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotPlayerContent.Serializer> PLAYER_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("player_content", HotpotPlayerContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotStrainerBasketContent.Serializer> STRAINER_BASKET_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("strainer_basket_content", HotpotStrainerBasketContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotEmptyContent.Serializer> EMPTY_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("empty_content", HotpotEmptyContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotItemStackDummyContent.Serializer> ITEM_STACK_DUMMY_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("item_stack_dummy_content", HotpotItemStackDummyContent.Serializer::new);

    public static Registry<IHotpotContentSerializer<?>> getContentSerializerRegistry() {
        return CONTENT_SERIALIZER_REGISTRY;
    }

    public static HotpotEmptyContent loadEmptyContent() {
        return EMPTY_CONTENT_SERIALIZER.get().getEmptyContent();
    }
}
