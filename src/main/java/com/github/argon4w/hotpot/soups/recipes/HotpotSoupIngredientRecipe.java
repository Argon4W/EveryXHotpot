package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotIngredientActionContext;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotIngredientActionExecutor;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredient;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotIngredientRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HotpotSoupIngredientRecipe extends AbstractHotpotSoupRecipe<HotpotIngredientRecipeInput> {
    private final List<HotpotSoupIngredient> ingredients;
    private final ResourceKey<HotpotComponentSoupType> sourceSoupTypeKey, resultSoupTypeKey;
    private final double resultWaterLevel;

    public HotpotSoupIngredientRecipe(List<HotpotSoupIngredient> ingredients, ResourceKey<HotpotComponentSoupType> sourceSoupTypeKey, ResourceKey<HotpotComponentSoupType> resultSoupTypeKey, double resultWaterLevel) {
        this.ingredients = ingredients;
        this.sourceSoupTypeKey = sourceSoupTypeKey;
        this.resultSoupTypeKey = resultSoupTypeKey;
        this.resultWaterLevel = resultWaterLevel;
    }

    @Override
    public boolean matches(HotpotIngredientRecipeInput container, Level level) {
        return sourceSoupTypeKey.equals(container.hotpotBlockEntity().getSoup().soupTypeHolder().getKey()) && findMatches(container.hotpotBlockEntity(), new ArrayList<>(getExpendedSoupIngredients()), (contentIndex, ingredientIndex, ingredient) -> ingredient).size() == getExpendedSoupIngredients().size();
    }

    public HotpotIngredientActionExecutor assemble(HotpotBlockEntity hotpotBlockEntity, HolderLookup.Provider registryAccess) {
        return new HotpotIngredientActionExecutor(findMatches(hotpotBlockEntity, new ArrayList<>(getExpendedSoupIngredients()), (contentIndex, ingredientIndex, ingredient) -> new HotpotIngredientActionContext(contentIndex, ingredient.action())), HotpotComponentSoupType.loadSoup(resultSoupTypeKey, registryAccess), resultWaterLevel);
    }

    public <T> List<T> findMatches(HotpotBlockEntity hotpotBlockEntity, List<HotpotSoupIngredient> ingredients, TriFunction<Integer, Integer, HotpotSoupIngredient, T> function) {
        return IntStream.range(0, hotpotBlockEntity.getContents().size()).mapToObj(c -> IntStream.range(0, ingredients.size()).filter(i -> ingredients.get(i) != null).filter(i -> ingredients.get(i).condition().matches(hotpotBlockEntity.getContents().get(c), hotpotBlockEntity.getSoup())).mapToObj(i -> function.apply(c, i, ingredients.set(i, null))).findFirst()).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public List<HotpotSoupIngredient> getExpendedSoupIngredients() {
        return getSoupIngredients().stream().flatMap(i -> Stream.iterate(i, ingredient -> ingredient.amount() > 0, (ingredient) -> new HotpotSoupIngredient(ingredient.condition(), ingredient.action(), ingredient.amount() - 1))).toList();
    }

    public List<HotpotSoupIngredient> getSoupIngredients() {
        return ingredients;
    }

    public ResourceKey<HotpotComponentSoupType> getSourceSoupTypeKey() {
        return sourceSoupTypeKey;
    }

    public ResourceKey<HotpotComponentSoupType> getResultSoupTypeKey() {
        return resultSoupTypeKey;
    }

    public double getResultWaterLevel() {
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
                        HotpotComponentSoupType.KEY_CODEC.fieldOf("source_soup").forGetter(HotpotSoupIngredientRecipe::getSourceSoupTypeKey),
                        HotpotComponentSoupType.KEY_CODEC.fieldOf("result_soup").forGetter(HotpotSoupIngredientRecipe::getResultSoupTypeKey),
                        Codec.DOUBLE.optionalFieldOf("result_water_level", 1.0).forGetter(HotpotSoupIngredientRecipe::getResultWaterLevel)
                ).apply(recipe, HotpotSoupIngredientRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredientRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ByteBufCodecs.collection(ArrayList::new, HotpotSoupIngredient.STREAM_CODEC), HotpotSoupIngredientRecipe::getSoupIngredients,
                        HotpotComponentSoupType.KEY_STREAM_CODEC, HotpotSoupIngredientRecipe::getSourceSoupTypeKey,
                        HotpotComponentSoupType.KEY_STREAM_CODEC, HotpotSoupIngredientRecipe::getResultSoupTypeKey,
                        ByteBufCodecs.DOUBLE, HotpotSoupIngredientRecipe::getResultWaterLevel,
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
