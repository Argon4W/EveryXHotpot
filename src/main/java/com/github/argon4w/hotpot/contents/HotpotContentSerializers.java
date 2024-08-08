package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.IntStream;

public class HotpotContentSerializers {
    public static final ResourceKey<Registry<IHotpotContentSerializer<?>>> CONTENT_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "content_serializer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IHotpotContentSerializer<?>>> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(CONTENT_SERIALIZER_REGISTRY_KEY));
    public static final Codec<Holder<IHotpotContentSerializer<?>>> SERIALIZER_CODEC = Codec.lazyInitialized(() -> getContentSerializerRegistry().holderByNameCodec());

    public static final MapCodec<IHotpotContent> CODEC = LazyMapCodec.of(() -> SERIALIZER_CODEC.dispatchMap(IHotpotContent::getContentSerializerHolder, holder -> holder.value().getCodec()));
    public static final Codec<List<HotpotContentWithSlot>> LIST_CODEC = Codec.lazyInitialized(() -> Codec.INT.dispatch("slot", HotpotContentWithSlot::slot, i -> CODEC.xmap(c -> new HotpotContentWithSlot(i, c), HotpotContentWithSlot::content)).listOf());

    public static final ResourceLocation EMPTY_CONTENT_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_content");

    public static final DeferredRegister<IHotpotContentSerializer<?>> CONTENT_SERIALIZERS = DeferredRegister.create(CONTENT_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotContentSerializer<?>> CONTENT_SERIALIZER_REGISTRY = CONTENT_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_CONTENT_SERIALIZER_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotCookingRecipeContent.Serializer> COOKING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("cooking_recipe_content", HotpotCookingRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotSmeltingRecipeContent.Serializer> SMELTING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("smelting_recipe_content", HotpotSmeltingRecipeContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotDisassemblingContent.Serializer> DISASSEMBLING_RECIPE_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("disassembling_content", HotpotDisassemblingContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotPlayerContent.Serializer> PLAYER_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("player_content", HotpotPlayerContent.Serializer::new);
    public static final DeferredHolder<IHotpotContentSerializer<?>, HotpotEmptyContent.Serializer> EMPTY_CONTENT_SERIALIZER = CONTENT_SERIALIZERS.register("empty_content", HotpotEmptyContent.Serializer::new);

    public static HotpotEmptyContent getEmptyContent() {
        return getEmptyContentSerializer().get();
    }

    public static HotpotEmptyContent.Serializer getEmptyContentSerializer() {
        return EMPTY_CONTENT_SERIALIZER.get();
    }

    public static Registry<IHotpotContentSerializer<?>> getContentSerializerRegistry() {
        return CONTENT_SERIALIZER_REGISTRY;
    }

    public static void loadContents(ListTag listTag, HolderLookup.Provider registryAccess, NonNullList<IHotpotContent> contents) {
        LIST_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), listTag).resultOrPartial().orElseGet(List::of).stream().filter(content -> content.slot() >= 0 && content.slot() < contents.size()).forEach(content -> contents.set(content.slot(), content.content()));
    }

    public static Tag saveContents(NonNullList<IHotpotContent> contents, HolderLookup.Provider registryAccess) {
        return LIST_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), IntStream.range(0, contents.size()).mapToObj(i -> new HotpotContentWithSlot(i, contents.get(i))).toList()).resultOrPartial().orElseGet(ListTag::new);
    }
}
