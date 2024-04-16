package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

public class HotpotCookingRecipe extends AbstractCookingRecipe {
    private final ResourceLocation targetSoup;

    public HotpotCookingRecipe(ResourceLocation targetSoup, ResourceLocation resourceLocation, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(HotpotModEntry.HOTPOT_COOKING_RECIPE.get(), resourceLocation, group, category, ingredient, result, experience, cookingTime);
        this.targetSoup = targetSoup;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    public boolean matchesTargetSoup(IHotpotSoupType soupType) {
        return soupType.getResourceLocation().equals(getTargetSoup());
    }

    public ResourceLocation getTargetSoup() {
        return targetSoup;
    }

    public static class Serializer implements RecipeSerializer<HotpotCookingRecipe> {
        public static final int DEFAULT_COOKING_TIME = 100;
        public static final float DEFAULT_EXPERIENCE = 0f;

        public HotpotCookingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("result")) {
                throw new JsonSyntaxException("Hotpot cooking result must have a \"result\"");
            }

            if (!jsonObject.has("ingredient")) {
                throw new JsonSyntaxException("Hotpot cooking result must have a \"ingredient\"");
            }

            if (!jsonObject.has("target_soup")) {
                throw new JsonSyntaxException("Hotpot cooking result must have a \"target_soup\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "target_soup"))) {
                throw new JsonSyntaxException("\"target_soup\" in the hotpot cooking recipe must be a valid resource location");
            }

            String group = GsonHelper.getAsString(jsonObject, "group", "");
            CookingBookCategory category = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CookingBookCategory.MISC);
            Ingredient ingredient = Ingredient.fromJson(jsonObject.get("ingredient"), false);
            float experience = GsonHelper.getAsFloat(jsonObject, "experience", DEFAULT_EXPERIENCE);
            int cookingTime = GsonHelper.getAsInt(jsonObject, "cooking_time", DEFAULT_COOKING_TIME);
            ResourceLocation targetSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "target_soup"));

            if (jsonObject.get("result").isJsonObject()) {
                ItemStack resultItemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                return new HotpotCookingRecipe(targetSoup, resourceLocation, group, category, ingredient, resultItemStack, experience, cookingTime);
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "result"))) {
                throw new JsonSyntaxException("\"result\" in the hotpot cooking recipe must be a valid resource location");
            }

            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "result"));
            ItemStack resultItemStack = new ItemStack(ForgeRegistries.ITEMS.getDelegateOrThrow(resourcelocation));

            return new HotpotCookingRecipe(targetSoup, resourceLocation, group, category, ingredient, resultItemStack, experience, cookingTime);
        }

        public HotpotCookingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
            ResourceLocation targetSoup = byteBuf.readResourceLocation();
            String group = byteBuf.readUtf();
            CookingBookCategory category = byteBuf.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(byteBuf);
            ItemStack itemStack = byteBuf.readItem();
            float experience = byteBuf.readFloat();
            int cookingTime = byteBuf.readVarInt();

            return new HotpotCookingRecipe(targetSoup, resourceLocation, group, category, ingredient, itemStack, experience, cookingTime);
        }

        public void toNetwork(FriendlyByteBuf byteBuf, HotpotCookingRecipe recipe) {
            byteBuf.writeResourceLocation(recipe.targetSoup);
            byteBuf.writeUtf(recipe.group);
            byteBuf.writeEnum(recipe.category());
            recipe.ingredient.toNetwork(byteBuf);
            byteBuf.writeItem(recipe.result);
            byteBuf.writeFloat(recipe.experience);
            byteBuf.writeVarInt(recipe.cookingTime);
        }
    }
}
