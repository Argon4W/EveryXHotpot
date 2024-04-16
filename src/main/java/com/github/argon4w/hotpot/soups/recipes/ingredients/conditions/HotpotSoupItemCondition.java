package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record HotpotSoupItemCondition(Ingredient ingredient) implements IHotpotSoupIngredientCondition {
    @Override
    public boolean matches(IHotpotContent content, IHotpotSoupType soup) {
        return content instanceof AbstractHotpotItemStackContent itemStackContent && ingredient.test(itemStackContent.getItemStack());
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.ITEM_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupItemCondition> {
        @Override
        public HotpotSoupItemCondition fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("predicate")) {
                throw new JsonParseException("Item condition must have a \"predicate\"");
            }

            return new HotpotSoupItemCondition(Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "predicate")));
        }

        @Override
        public HotpotSoupItemCondition fromNetwork(FriendlyByteBuf byteBuf) {
            return new HotpotSoupItemCondition(Ingredient.fromNetwork(byteBuf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupItemCondition condition) {
            condition.ingredient.toNetwork(byteBuf);
        }

        @Override
        public ResourceLocation getType() {
            return new ResourceLocation(HotpotModEntry.MODID, "item");
        }
    }
}
