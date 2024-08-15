package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotCookingRecipe implements Recipe<HotpotRecipeInput> {
    private final HotpotSoupTypeHolder<?> targetSoupType;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public HotpotCookingRecipe(HotpotSoupTypeHolder<?> targetSoupType, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        this.targetSoupType = targetSoupType;
        this.ingredient = ingredient;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(HotpotRecipeInput input, Level level) {
        return targetSoupType.equals(input.soup()) && ingredient.test(input.itemStack());
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
        return HotpotModEntry.HOTPOT_COOKING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_COOKING_RECIPE.get();
    }

    public HotpotSoupTypeHolder<?> getTargetSoupType() {
        return targetSoupType;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public static class Serializer implements RecipeSerializer<HotpotCookingRecipe> {
        public static final int DEFAULT_COOKING_TIME = 100;
        public static final float DEFAULT_EXPERIENCE = 0f;

        public static final MapCodec<HotpotCookingRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotSoupTypeSerializers.getHolderCodec().fieldOf("target_soup").forGetter(HotpotCookingRecipe::getTargetSoupType),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotCookingRecipe::getIngredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(HotpotCookingRecipe::getResult),
                        Codec.FLOAT.optionalFieldOf("experience", Serializer.DEFAULT_EXPERIENCE).forGetter(HotpotCookingRecipe::getExperience),
                        Codec.INT.optionalFieldOf("cooking_time", Serializer.DEFAULT_COOKING_TIME).forGetter(HotpotCookingRecipe::getCookingTime)
                ).apply(recipe, HotpotCookingRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotCookingRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotSoupTypeSerializers.getStreamHolderCodec(), HotpotCookingRecipe::getTargetSoupType,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotCookingRecipe::getIngredient,
                        ItemStack.STREAM_CODEC, HotpotCookingRecipe::getResult,
                        ByteBufCodecs.FLOAT, HotpotCookingRecipe::getExperience,
                        ByteBufCodecs.INT, HotpotCookingRecipe::getCookingTime,
                        HotpotCookingRecipe::new
                )
        );

        @Override
        public MapCodec<HotpotCookingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotCookingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
