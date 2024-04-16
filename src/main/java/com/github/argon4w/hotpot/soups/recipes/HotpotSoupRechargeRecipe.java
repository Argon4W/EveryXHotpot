package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class HotpotSoupRechargeRecipe extends AbstractHotpotSoupRecipe {
    private final ResourceLocation location;
    private final ResourceLocation targetSoup;
    private final float rechargeWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final ResourceLocation soundEvent;

    public HotpotSoupRechargeRecipe(ResourceLocation location, ResourceLocation targetSoup, float rechargeWaterLevel, Ingredient ingredient, ItemStack remainingItem, ResourceLocation soundEvent) {
        this.location = location;
        this.targetSoup = targetSoup;
        this.rechargeWaterLevel = rechargeWaterLevel;
        this.ingredient = ingredient;
        this.remainingItem = remainingItem;
        this.soundEvent = soundEvent;
    }

    public boolean matches(ItemStack itemStack) {
        return ingredient.test(itemStack);
    }

    public ItemStack getRemainingItem() {
        return remainingItem;
    }

    public ResourceLocation getTargetSoup() {
        return targetSoup;
    }

    public ResourceLocation getSoundEvent() {
        return soundEvent;
    }

    public float getRechargeWaterLevel() {
        return rechargeWaterLevel;
    }

    @Override
    public ResourceLocation getId() {
        return location;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupRechargeRecipe> {
        @Override
        public HotpotSoupRechargeRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("target_soup")) {
                throw new JsonParseException("Soup recharge recipe must have a \"soup\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "target_soup"))) {
                throw new JsonSyntaxException("\"target_soup\" in the soup recharge recipe must be a valid resource location");
            }

            if (!jsonObject.has("recharge_waterlevel")) {
                throw new JsonParseException("Soup recharge recipe must have a \"recharge_waterlevel\"");
            }

            if (!jsonObject.has("ingredient")) {
                throw new JsonParseException("Soup recharge recipe must have a \"ingredient\"");
            }

            if (!jsonObject.has("sound_event")) {
                throw new JsonParseException("Soup recharge recipe must have a \"sound_event\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "sound_event"))) {
                throw new JsonSyntaxException("\"sound_event\" in the soup recharge recipe must be a valid resource location");
            }

            ResourceLocation targetSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "target_soup"));
            float waterLevel = GsonHelper.getAsFloat(jsonObject, "recharge_waterlevel");
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            ResourceLocation soundEvent = new ResourceLocation(GsonHelper.getAsString(jsonObject, "sound_event"));

            if (!jsonObject.has("remaining_item")) {
                return new HotpotSoupRechargeRecipe(resourceLocation, targetSoup, waterLevel, ingredient, ItemStack.EMPTY, soundEvent);
            }

            if (jsonObject.get("remaining_item").isJsonObject()) {
                ItemStack remainingItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "remaining_item"));
                return new HotpotSoupRechargeRecipe(resourceLocation, targetSoup, waterLevel, ingredient, remainingItem, soundEvent);
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "remaining_item"))) {
                throw new JsonSyntaxException("\"remaining_item\" in the soup recharge recipe must be a valid resource location");
            }

            ResourceLocation itemResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "remaining_item"));
            ItemStack remainingItem = new ItemStack(ForgeRegistries.ITEMS.getDelegateOrThrow(itemResourceLocation));

            return new HotpotSoupRechargeRecipe(resourceLocation, targetSoup, waterLevel, ingredient, remainingItem, soundEvent);
        }

        @Override
        public @Nullable HotpotSoupRechargeRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf byteBuf) {
            ResourceLocation targetSoup = byteBuf.readResourceLocation();
            float resultWaterLevel = byteBuf.readFloat();
            Ingredient ingredient = Ingredient.fromNetwork(byteBuf);
            ItemStack remainingItem = byteBuf.readItem();
            ResourceLocation soundEvent = byteBuf.readResourceLocation();

            return new HotpotSoupRechargeRecipe(location, targetSoup, resultWaterLevel, ingredient, remainingItem, soundEvent);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupRechargeRecipe recipe) {
            byteBuf.writeResourceLocation(recipe.targetSoup);
            byteBuf.writeFloat(recipe.rechargeWaterLevel);
            recipe.ingredient.toNetwork(byteBuf);
            byteBuf.writeItem(recipe.remainingItem);
            byteBuf.writeResourceLocation(recipe.soundEvent);
        }
    }
}
