package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupIngredientConditionSerializer<T extends IHotpotSoupIngredientCondition> {
    MapCodec<T> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    ResourceLocation getType();
}
