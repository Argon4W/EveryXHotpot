package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record HotpotSkewerDataComponent(List<ItemStack> itemStacks, Optional<List<HotpotCookingRecipeContent>> skewerRecipes) {
    public static final Codec<HotpotSkewerDataComponent> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(data -> data.group(
            ItemStack.CODEC.listOf().fieldOf("item_stacks").forGetter(HotpotSkewerDataComponent::itemStacks),
            HotpotContents.COOKING_RECIPE_CONTENT.get().buildFromCodec().codec().listOf().optionalFieldOf("skewer_recipes").forGetter(HotpotSkewerDataComponent::skewerRecipes)
    ).apply(data, HotpotSkewerDataComponent::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSkewerDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotSkewerDataComponent::itemStacks,
            HotpotSkewerDataComponent::new
    ));

    public HotpotSkewerDataComponent(List<ItemStack> itemStacks) {
        this(itemStacks, Optional.empty());
    }
}
