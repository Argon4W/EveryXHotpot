package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredient;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotpotSoupIngredientRecipe extends AbstractHotpotSoupRecipe {
    private final List<HotpotSoupIngredient> ingredients;
    private final ResourceLocation sourceSoup, resultSoup;

    public HotpotSoupIngredientRecipe(List<HotpotSoupIngredient> ingredients, ResourceLocation sourceSoup, ResourceLocation resultSoup) {
        this.ingredients = ingredients;
        this.sourceSoup = sourceSoup;
        this.resultSoup = resultSoup;
    }

    public Optional<IHotpotSoupType> matches(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        NonNullList<IHotpotContent> copiedContents = new NonNullList<>(hotpotBlockEntity.copyContents(), HotpotContents.buildEmptyContent());
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
                    copiedContents.set(i, ingredient.action().action(pos, hotpotBlockEntity, content, hotpotBlockEntity.getSoup(), soup));
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

    public ResourceLocation getSourceSoup() {
        return sourceSoup;
    }

    public ResourceLocation getResultSoup() {
        return resultSoup;
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
        public static final MapCodec<HotpotSoupIngredientRecipe> CODEC = RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                HotpotSoupIngredient.CODEC.codec().listOf().fieldOf("ingredients").forGetter(HotpotSoupIngredientRecipe::getSoupIngredients),
                ResourceLocation.CODEC.fieldOf("source_soup").forGetter(HotpotSoupIngredientRecipe::getSourceSoup),
                ResourceLocation.CODEC.fieldOf("result_soup").forGetter(HotpotSoupIngredientRecipe::getResultSoup)
        ).apply(recipe, HotpotSoupIngredientRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredientRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, HotpotSoupIngredient.STREAM_CODEC), HotpotSoupIngredientRecipe::getSoupIngredients,
                ResourceLocation.STREAM_CODEC, HotpotSoupIngredientRecipe::getSourceSoup,
                ResourceLocation.STREAM_CODEC, HotpotSoupIngredientRecipe::getResultSoup,
                HotpotSoupIngredientRecipe::new
        );

        @Override
        public MapCodec<HotpotSoupIngredientRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredientRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
