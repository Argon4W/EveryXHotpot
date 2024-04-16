package com.github.argon4w.hotpot.soups.recipes.ingredients.actions;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupRechargeRecipe;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientActionSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public record HotpotSoupReplaceItemAction(ItemStack itemStack) implements IHotpotSoupIngredientAction {
    @Override
    public IHotpotContent action(LevelBlockPos pos, IHotpotContent content, IHotpotSoupType source, IHotpotSoupType target) {
        return target.remapItemStack(true, itemStack, pos).orElse(HotpotContents.getEmptyContent().build());
    }

    @Override
    public IHotpotSoupIngredientActionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.REPLACE_ACTION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientActionSerializer<HotpotSoupReplaceItemAction> {
        @Override
        public HotpotSoupReplaceItemAction fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("result")) {
                throw new JsonParseException("Replace action must have a \"result\"");
            }

            if (jsonObject.get("result").isJsonObject()) {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                return new HotpotSoupReplaceItemAction(result.copyWithCount(1));
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "result"))) {
                throw new JsonSyntaxException("\"result\" in the replace action must be a valid resource location");
            }

            ResourceLocation itemResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "result"));
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getDelegateOrThrow(itemResourceLocation));

            return new HotpotSoupReplaceItemAction(result.copyWithCount(1));
        }

        @Override
        public HotpotSoupReplaceItemAction fromNetwork(FriendlyByteBuf byteBuf) {
            return new HotpotSoupReplaceItemAction(byteBuf.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupReplaceItemAction action) {
            byteBuf.writeItem(action.itemStack);
        }

        @Override
        public ResourceLocation getType() {
            return new ResourceLocation(HotpotModEntry.MODID, "replace");
        }
    }
}
