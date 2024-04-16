package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupIngredientConditionSerializer<T extends IHotpotSoupIngredientCondition> {
    T fromJson(JsonObject jsonObject);
    T fromNetwork(FriendlyByteBuf byteBuf);
    void toNetwork(FriendlyByteBuf byteBuf, T condition);
    default void toNetwork0(FriendlyByteBuf byteBuf, IHotpotSoupIngredientCondition action) {
        toNetwork(byteBuf, (T) action);
    }
    ResourceLocation getType();
}
