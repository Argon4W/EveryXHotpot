package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
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

public class HotpotSoupBaseRecipe extends AbstractHotpotSoupRecipe {
    private final ResourceLocation location;
    private final ResourceLocation sourceSoup;
    private final ResourceLocation resultSoup;
    private final float resultWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final ResourceLocation soundEvent;

    public HotpotSoupBaseRecipe(ResourceLocation location, ResourceLocation sourceSoup, ResourceLocation resultSoup, float resultWaterLevel, Ingredient ingredient, ItemStack remainingItem, ResourceLocation soundEvent) {
        this.location = location;
        this.sourceSoup = sourceSoup;
        this.resultSoup = resultSoup;
        this.resultWaterLevel = resultWaterLevel;
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

    public ResourceLocation getSourceSoup() {
        return sourceSoup;
    }

    public IHotpotSoupType createResultSoup() {
        return HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildSoup(resultSoup);
    }

    public ResourceLocation getSoundEvent() {
        return soundEvent;
    }

    public float getResultWaterLevel() {
        return resultWaterLevel;
    }

    @Override
    public ResourceLocation getId() {
        return location;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_BASE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupBaseRecipe> {
        @Override
        public HotpotSoupBaseRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            if (!jsonObject.has("source_soup")) {
                throw new JsonParseException("Base soup recipe must have a \"source_soup\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "source_soup"))) {
                throw new JsonSyntaxException("\"source_soup\" in the base soup recipe must be a valid resource location");
            }

            if (!jsonObject.has("result_soup")) {
                throw new JsonParseException("Base soup recipe must have a \"result_soup\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "result_soup"))) {
                throw new JsonSyntaxException("\"result_soup\" in the base soup recipe must be a valid resource location");
            }

            if (!jsonObject.has("result_waterlevel")) {
                throw new JsonParseException("Base soup recipe must have a \"result_waterlevel\"");
            }

            if (!jsonObject.has("ingredient")) {
                throw new JsonParseException("Base soup recipe must have a \"ingredient\"");
            }

            if (!jsonObject.has("sound_event")) {
                throw new JsonParseException("Base soup recipe must have a \"remaining_item\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "sound_event"))) {
                throw new JsonSyntaxException("\"sound_event\" in the base soup recipe must be a valid resource location");
            }

            ResourceLocation sourceSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "source_soup"));
            ResourceLocation resultSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "result_soup"));
            float waterLevel = GsonHelper.getAsFloat(jsonObject, "result_waterlevel");
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            ResourceLocation soundEvent = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "sound_event"));

            if (!jsonObject.has("remaining_item")) {
                return new HotpotSoupBaseRecipe(resourceLocation, sourceSoup, resultSoup, waterLevel, ingredient, ItemStack.EMPTY, soundEvent);
            }

            if (jsonObject.get("remaining_item").isJsonObject()) {
                ItemStack remainingItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "remaining_item"));
                return new HotpotSoupBaseRecipe(resourceLocation, sourceSoup, resultSoup, waterLevel, ingredient, remainingItem, soundEvent);
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "remaining_item"))) {
                throw new JsonSyntaxException("\"remaining_item\" in the base soup recipe must be a valid resource location");
            }

            ResourceLocation itemResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "remaining_item"));
            ItemStack remainingItem = new ItemStack(ForgeRegistries.ITEMS.getDelegateOrThrow(itemResourceLocation));

            return new HotpotSoupBaseRecipe(resourceLocation, sourceSoup, resultSoup, waterLevel, ingredient, remainingItem, soundEvent);
        }

        @Override
        public @Nullable HotpotSoupBaseRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf byteBuf) {
            ResourceLocation sourceSoup = byteBuf.readResourceLocation();
            ResourceLocation resultSoup = byteBuf.readResourceLocation();
            float resultWaterLevel = byteBuf.readFloat();
            Ingredient ingredient = Ingredient.fromNetwork(byteBuf);
            ItemStack remainingItem = byteBuf.readItem();
            ResourceLocation soundEvent = byteBuf.readResourceLocation();

            return new HotpotSoupBaseRecipe(location, sourceSoup, resultSoup, resultWaterLevel, ingredient, remainingItem, soundEvent);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupBaseRecipe recipe) {
            byteBuf.writeResourceLocation(recipe.sourceSoup);
            byteBuf.writeResourceLocation(recipe.resultSoup);
            byteBuf.writeFloat(recipe.resultWaterLevel);
            recipe.ingredient.toNetwork(byteBuf);
            byteBuf.writeItem(recipe.remainingItem);
            byteBuf.writeResourceLocation(recipe.soundEvent);
        }
    }
}
