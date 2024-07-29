package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotIngredientActionContext;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotIngredientActionExecutor;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HotpotSoupIngredientRecipe extends AbstractHotpotSoupRecipe {
    private final List<HotpotSoupIngredient> ingredients;
    private final HotpotSoupTypeFactoryHolder<?> sourceSoup, resultSoup;
    private final float resultWaterLevel;

    public HotpotSoupIngredientRecipe(List<HotpotSoupIngredient> ingredients, HotpotSoupTypeFactoryHolder<?> sourceSoup, HotpotSoupTypeFactoryHolder<?> resultSoup, float resultWaterLevel) {
        this.ingredients = ingredients;
        this.sourceSoup = sourceSoup;
        this.resultSoup = resultSoup;
        this.resultWaterLevel = resultWaterLevel;
    }

    public Optional<HotpotIngredientActionExecutor> matches(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        ArrayList<HotpotSoupIngredient> ingredients = new ArrayList<>(getSoupIngredients().stream().flatMap(i -> Stream.iterate(i, ingredient -> ingredient.amount() > 0, (ingredient) -> new HotpotSoupIngredient(ingredient.condition(), ingredient.action(), ingredient.amount() - 1))).toList());
        ArrayList<HotpotIngredientActionContext> successfulActions = new ArrayList<>();

        if (!hotpotBlockEntity.getSoup().getSoupTypeFactoryHolder().equals(sourceSoup)) {
            return Optional.empty();
        }

        IntStream.range(0, hotpotBlockEntity.getContents().size()).forEach(c -> {
            IntStream.range(0, ingredients.size()).filter(i -> ingredients.get(i) != null).filter(i -> ingredients.get(i).condition().matches(hotpotBlockEntity.getContents().get(c), hotpotBlockEntity.getSoup())).findFirst().ifPresent(i -> successfulActions.add(new HotpotIngredientActionContext(c, ingredients.set(i, null).action())));
        });

        if (ingredients.stream().anyMatch(Objects::nonNull)) {
            return Optional.empty();
        }

        return Optional.of(new HotpotIngredientActionExecutor(successfulActions, resultSoup.buildFromScratch(), resultWaterLevel));
    }

    public List<HotpotSoupIngredient> getSoupIngredients() {
        return ingredients;
    }

    public HotpotSoupTypeFactoryHolder<?>  getSourceSoup() {
        return sourceSoup;
    }

    public HotpotSoupTypeFactoryHolder<?>  getResultSoup() {
        return resultSoup;
    }

    public float getResultWaterLevel() {
        return resultWaterLevel;
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
        public static final MapCodec<HotpotSoupIngredientRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotSoupIngredient.CODEC.codec().listOf().fieldOf("ingredients").forGetter(HotpotSoupIngredientRecipe::getSoupIngredients),
                        HotpotSoupTypes.getHolderCodec().fieldOf("source_soup").forGetter(HotpotSoupIngredientRecipe::getSourceSoup),
                        HotpotSoupTypes.getHolderCodec().fieldOf("result_soup").forGetter(HotpotSoupIngredientRecipe::getResultSoup),
                        Codec.FLOAT.optionalFieldOf("result_waterlevel", 1.0f).forGetter(HotpotSoupIngredientRecipe::getResultWaterLevel)
                ).apply(recipe, HotpotSoupIngredientRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredientRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ByteBufCodecs.collection(ArrayList::new, HotpotSoupIngredient.STREAM_CODEC), HotpotSoupIngredientRecipe::getSoupIngredients,
                        HotpotSoupTypes.getStreamHolderCodec(), HotpotSoupIngredientRecipe::getSourceSoup,
                        HotpotSoupTypes.getStreamHolderCodec(), HotpotSoupIngredientRecipe::getResultSoup,
                        ByteBufCodecs.FLOAT, HotpotSoupIngredientRecipe::getResultWaterLevel,
                        HotpotSoupIngredientRecipe::new
                )
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
