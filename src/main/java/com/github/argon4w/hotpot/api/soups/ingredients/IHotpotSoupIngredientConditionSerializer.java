package com.github.argon4w.hotpot.api.soups.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupIngredientConditionSerializer<T extends IHotpotSoupIngredientCondition> {
    MapCodec<T> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
}
