package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupIngredientActionSerializer<T extends IHotpotSoupIngredientAction> {
    T fromJson(JsonObject jsonObject);
    T fromNetwork(FriendlyByteBuf byteBuf);
    void toNetwork(FriendlyByteBuf byteBuf, T assembler);
    default void toNetwork0(FriendlyByteBuf byteBuf, IHotpotSoupIngredientAction action) {
        toNetwork(byteBuf, (T) action);
    }
    ResourceLocation getType();
}
