package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record HotpotSoupContentCondition(ResourceLocation resourceLocation) implements IHotpotSoupIngredientCondition {
    @Override
    public boolean matches(IHotpotContent content, IHotpotSoupType soup) {
        return content.getResourceLocation().equals(resourceLocation);
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONTENT_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupContentCondition> {
        @Override
        public HotpotSoupContentCondition fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("content")) {
                throw new JsonParseException("Content condition must have a \"content\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "content"))) {
                throw new JsonSyntaxException("\"content\" in the content action must be a valid resource location");
            }

            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "content"));
            return new HotpotSoupContentCondition(resourceLocation);
        }

        @Override
        public HotpotSoupContentCondition fromNetwork(FriendlyByteBuf byteBuf) {
            return new HotpotSoupContentCondition(byteBuf.readResourceLocation());
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupContentCondition condition) {
            byteBuf.writeResourceLocation(condition.resourceLocation);
        }

        @Override
        public ResourceLocation getType() {
            return new ResourceLocation(HotpotModEntry.MODID, "content");
        }
    }
}
