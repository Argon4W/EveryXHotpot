package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record HotpotSoupIngredient(IHotpotSoupIngredientCondition condition, IHotpotSoupIngredientAction action) {
    public static final Serializer SERIALIZER = new Serializer();

    public static class Serializer {
        public HotpotSoupIngredient fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("condition")) {
                throw new JsonParseException("Ingredient must have a \"condition\"");
            }

            if (!jsonObject.has("action")) {
                throw new JsonParseException("Ingredient must have a \"action\"");
            }

            JsonObject condition = GsonHelper.getAsJsonObject(jsonObject, "condition");
            JsonObject action = GsonHelper.getAsJsonObject(jsonObject, "action");

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(condition, "type"))) {
                throw new JsonParseException("\"type\" in the \"condition\" of the ingredient must be a valid resource location.");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(action, "type"))) {
                throw new JsonParseException("\"type\" in the \"action\" of the ingredient must be a valid resource location.");
            }

            ResourceLocation conditionResourceLocation = new ResourceLocation(GsonHelper.getAsString(condition, "type"));
            ResourceLocation actionResourceLocation = new ResourceLocation(GsonHelper.getAsString(action, "type"));

            return new HotpotSoupIngredient(
                    HotpotSoupIngredients.getConditionSerializer(conditionResourceLocation).fromJson(condition),
                    HotpotSoupIngredients.getActionSerializer(actionResourceLocation).fromJson(action)
            );
        }

        public HotpotSoupIngredient fromNetwork(FriendlyByteBuf byteBuf) {
            ResourceLocation condition = byteBuf.readResourceLocation();
            ResourceLocation action = byteBuf.readResourceLocation();

            return new HotpotSoupIngredient(
                    HotpotSoupIngredients.getConditionSerializer(condition).fromNetwork(byteBuf),
                    HotpotSoupIngredients.getActionSerializer(action).fromNetwork(byteBuf)
            );
        }

        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupIngredient ingredient) {
            byteBuf.writeResourceLocation(ingredient.condition.getSerializer().getType());
            byteBuf.writeResourceLocation(ingredient.action.getSerializer().getType());

            ingredient.condition.getSerializer().toNetwork0(byteBuf, ingredient.condition);
            ingredient.action.getSerializer().toNetwork0(byteBuf, ingredient.action);
        }
    }
}
