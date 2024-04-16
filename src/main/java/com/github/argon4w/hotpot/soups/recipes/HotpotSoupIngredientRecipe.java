package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredient;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotSoupIngredientRecipe extends AbstractHotpotSoupRecipe {
    private final ResourceLocation location;
    private final List<HotpotSoupIngredient> ingredients;
    private final ResourceLocation sourceSoup, resultSoup;

    public HotpotSoupIngredientRecipe(ResourceLocation location, List<HotpotSoupIngredient> ingredients, ResourceLocation sourceSoup, ResourceLocation resultSoup) {
        this.location = location;
        this.ingredients = ingredients;
        this.sourceSoup = sourceSoup;
        this.resultSoup = resultSoup;
    }

    public Optional<IHotpotSoupType> matches(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        NonNullList<IHotpotContent> copiedContents = new NonNullList<>(hotpotBlockEntity.copyContents(), HotpotContents.getEmptyContent().build());
        ArrayList<HotpotSoupIngredient> copiedIngredients = new ArrayList<>(getSoupIngredients());
        IHotpotSoupType soup = HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildSoup(resultSoup);

        if (!hotpotBlockEntity.getSoup().getResourceLocation().equals(sourceSoup)) {
            return Optional.empty();
        }

        for (int i = 0; i < copiedContents.size(); i ++) {
            IHotpotContent content = copiedContents.get(i);

            for (int j = 0; j < copiedIngredients.size(); j ++) {
                HotpotSoupIngredient ingredient = copiedIngredients.get(j);

                if (ingredient.condition().matches(content, hotpotBlockEntity.getSoup())) {
                    copiedIngredients.remove(j);
                    copiedContents.set(i, ingredient.action().action(pos, content, hotpotBlockEntity.getSoup(), soup));
                    break;
                }
            }
        }

        if (!copiedIngredients.isEmpty()) {
            return Optional.empty();
        } else {
            hotpotBlockEntity.setContents(copiedContents);
            return Optional.of(soup);
        }
    }

    public List<HotpotSoupIngredient> getSoupIngredients() {
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return location;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_INGREDIENT_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupIngredientRecipe> {
        @Override
        public HotpotSoupIngredientRecipe fromJson(ResourceLocation location, JsonObject jsonObject) {
            if (!jsonObject.has("ingredients")) {
                throw new JsonParseException("Ingredient soup recipe must have a \"ingredients\"");
            }

            if (!jsonObject.has("source_soup")) {
                throw new JsonParseException("Ingredient soup recipe must have a \"source_soup\"");
            }

            if (!jsonObject.has("result_soup")) {
                throw new JsonParseException("Ingredient soup recipe must have a \"result_soup\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "source_soup"))) {
                throw new JsonSyntaxException("\"source_soup\" in the ingredient soup recipe must be a valid resource location");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "result_soup"))) {
                throw new JsonSyntaxException("\"result_soup\" in the ingredient soup recipe must be a valid resource location");
            }

            JsonArray array = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            ArrayList<HotpotSoupIngredient> ingredients = new ArrayList<>();
            ResourceLocation sourceSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "source_soup"));
            ResourceLocation resultSoup = new ResourceLocation(GsonHelper.getAsString(jsonObject, "result_soup"));

            for (int i = 0; i < array.size(); i ++) {
                JsonObject ingredientObject = GsonHelper.convertToJsonObject(array.get(i), "ingredients[%d]".formatted(i));

                if (!ingredientObject.has("amount")) {
                    ingredients.add(HotpotSoupIngredient.SERIALIZER.fromJson(ingredientObject));
                    continue;
                }

                for (int j = 0; j < GsonHelper.getAsInt(ingredientObject, "amount") ; j ++) {
                    ingredients.add(HotpotSoupIngredient.SERIALIZER.fromJson(ingredientObject));
                }
            }

            return new HotpotSoupIngredientRecipe(location, ImmutableList.copyOf(ingredients), sourceSoup, resultSoup);
        }

        @Override
        public @Nullable HotpotSoupIngredientRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf byteBuf) {
            int ingredientSize = byteBuf.readVarInt();
            ArrayList<HotpotSoupIngredient> ingredients = new ArrayList<>();

            for (int i = 0; i < ingredientSize; i ++) {
                ingredients.add(HotpotSoupIngredient.SERIALIZER.fromNetwork(byteBuf));
            }

            ResourceLocation sourceSoup = byteBuf.readResourceLocation();
            ResourceLocation resultSoup = byteBuf.readResourceLocation();

            return new HotpotSoupIngredientRecipe(location, List.copyOf(ingredients), sourceSoup, resultSoup);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, HotpotSoupIngredientRecipe recipe) {
            byteBuf.writeVarInt(recipe.getSoupIngredients().size());

            for (HotpotSoupIngredient ingredient : recipe.getSoupIngredients()) {
                HotpotSoupIngredient.SERIALIZER.toNetwork(byteBuf, ingredient);
            }

            byteBuf.writeResourceLocation(recipe.sourceSoup);
            byteBuf.writeResourceLocation(recipe.resultSoup);
        }
    }
}
