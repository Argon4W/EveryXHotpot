package com.github.argon4w.hotpot.soups.recipes.ingredients.actions;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientActionSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class HotpotSoupConsumeAction implements IHotpotSoupIngredientAction {
    @Override
    public IHotpotContent action(LevelBlockPos pos, IHotpotContent content, IHotpotSoupType source, IHotpotSoupType target) {
        return HotpotContents.getEmptyContent().build();
    }

    @Override
    public IHotpotSoupIngredientActionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONSUME_ACTION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientActionSerializer<HotpotSoupConsumeAction> {
        @Override
        public HotpotSoupConsumeAction fromJson(JsonObject jsonObject) {
            return new HotpotSoupConsumeAction();
        }

        @Override
        public HotpotSoupConsumeAction fromNetwork(FriendlyByteBuf byteBuf) {
            return new HotpotSoupConsumeAction();
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupConsumeAction action) {

        }

        @Override
        public ResourceLocation getType() {
            return new ResourceLocation(HotpotModEntry.MODID, "consume");
        }
    }
}
