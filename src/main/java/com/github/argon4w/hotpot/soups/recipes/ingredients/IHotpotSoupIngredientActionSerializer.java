package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupIngredientActionSerializer<T extends IHotpotSoupIngredientAction> {
    MapCodec<T> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    ResourceLocation getType();
}
