package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class HotpotContents {
    public record A(int a, ItemStack b) {
        public static final Codec<A> C = Codec.INT.dispatch("slot", e -> e.a, i -> ItemStack.CODEC.xmap(s -> new A(i, s), v -> v.b));
    }

    public static final MapCodec<IHotpotContent> CODEC = getContentRegistry().byNameCodec().dispatchMap(IHotpotContent::getFactory, IHotpotContentFactory::buildFromCodec);
    public static final Codec<List<HotpotContentWithSlot>> SLOT_CODEC = Codec.INT.dispatch("slot", HotpotContentWithSlot::slot, i -> CODEC.xmap(c -> new HotpotContentWithSlot(0, c), HotpotContentWithSlot::content)).listOf();

    public static final ResourceLocation EMPTY_CONTENT_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_content");

    public static final ResourceKey<Registry<IHotpotContentFactory<?>>> CONTENT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "content"));
    public static final DeferredRegister<IHotpotContentFactory<?>> CONTENTS = DeferredRegister.create(CONTENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotContentFactory<?>> CONTENT_REGISTRY = CONTENTS.makeRegistry(builder -> builder.defaultKey(EMPTY_CONTENT_LOCATION));

    public static final DeferredHolder<IHotpotContentFactory<?>, HotpotCookingRecipeContent.Factory> COOKING_RECIPE_CONTENT = CONTENTS.register("cooking_recipe_content", HotpotCookingRecipeContent.Factory::new);
    public static final DeferredHolder<IHotpotContentFactory<?>, HotpotSmeltingRecipeContent.Factory> SMELTING_RECIPE_CONTENT = CONTENTS.register("smelting_recipe_content", HotpotSmeltingRecipeContent.Factory::new);
    public static final DeferredHolder<IHotpotContentFactory<?>, HotpotDisassemblingContent.Factory> DISASSEMBLING_RECIPE_CONTENT = CONTENTS.register("disassembling_content", HotpotDisassemblingContent.Factory::new);
    public static final DeferredHolder<IHotpotContentFactory<?>, HotpotPlayerContent.Factory> PLAYER_CONTENT = CONTENTS.register("player_content", HotpotPlayerContent.Factory::new);
    public static final DeferredHolder<IHotpotContentFactory<?>, HotpotEmptyContent.Factory> EMPTY_CONTENT = CONTENTS.register("empty_content", HotpotEmptyContent.Factory::new);

    public static HotpotEmptyContent.Factory getEmptyContentFactory() {
        return EMPTY_CONTENT.get();
    }

    public static HotpotEmptyContent buildEmptyContent() {
        return getEmptyContentFactory().build();
    }

    public static Registry<IHotpotContentFactory<?>> getContentRegistry() {
        return CONTENT_REGISTRY;
    }

    public static IHotpotContentFactory<?> getContentFactory(ResourceLocation resourceLocation) {
        return getContentRegistry().get(resourceLocation);
    }

    public static void loadContents(ListTag listTag, HolderLookup.Provider registryAccess, NonNullList<IHotpotContent> contents) {
        SLOT_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), listTag).resultOrPartial().orElseGet(List::of).stream().filter(content -> content.slot() >= 0 && content.slot() < contents.size()).forEach(content -> contents.set(content.slot(), content.content()));
    }

    public static Tag saveContents(NonNullList<IHotpotContent> contents, HolderLookup.Provider registryAccess) {
        return SLOT_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), IntStream.range(0, contents.size()).mapToObj(i -> new HotpotContentWithSlot(i, contents.get(i))).toList()).resultOrPartial().orElseGet(ListTag::new);
    }
}
