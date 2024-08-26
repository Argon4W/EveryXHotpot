package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotSoupCookingRecipe implements Recipe<HotpotRecipeInput> {
    private final Holder<HotpotComponentSoupType> targetSoupType;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final double experience;
    private final int cookingTime;

    public HotpotSoupCookingRecipe(Holder<HotpotComponentSoupType> targetSoupType, Ingredient ingredient, ItemStack result, double experience, int cookingTime) {
        this.targetSoupType = targetSoupType;
        this.ingredient = ingredient;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(HotpotRecipeInput input, Level level) {
        return targetSoupType.equals(input.soup().soupTypeHolder()) && ingredient.test(input.itemStack());
    }

    @Override
    public ItemStack assemble(HotpotRecipeInput input, HolderLookup.Provider registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_COOKING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_COOKING_RECIPE_TYPE.get();
    }

    public Holder<HotpotComponentSoupType> getTargetSoupType() {
        return targetSoupType;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    public double getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupCookingRecipe> {
        public static final int DEFAULT_COOKING_TIME = 100;
        public static final double DEFAULT_EXPERIENCE = 0;

        public static final MapCodec<HotpotSoupCookingRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotComponentSoupType.TYPE_HOLDER_CODEC.fieldOf("target_soup").forGetter(HotpotSoupCookingRecipe::getTargetSoupType),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotSoupCookingRecipe::getIngredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(HotpotSoupCookingRecipe::getResult),
                        Codec.DOUBLE.optionalFieldOf("experience", Serializer.DEFAULT_EXPERIENCE).forGetter(HotpotSoupCookingRecipe::getExperience),
                        Codec.INT.optionalFieldOf("cooking_time", Serializer.DEFAULT_COOKING_TIME).forGetter(HotpotSoupCookingRecipe::getCookingTime)
                ).apply(recipe, HotpotSoupCookingRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupCookingRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotComponentSoupType.TYPE_HOLDER_STREAM_CODEC, HotpotSoupCookingRecipe::getTargetSoupType,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupCookingRecipe::getIngredient,
                        ItemStack.STREAM_CODEC, HotpotSoupCookingRecipe::getResult,
                        ByteBufCodecs.DOUBLE, HotpotSoupCookingRecipe::getExperience,
                        ByteBufCodecs.INT, HotpotSoupCookingRecipe::getCookingTime,
                        HotpotSoupCookingRecipe::new
                )
        );

        @Override
        public MapCodec<HotpotSoupCookingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupCookingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
