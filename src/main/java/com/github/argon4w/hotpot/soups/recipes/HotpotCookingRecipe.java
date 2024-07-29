package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotCookingRecipe extends AbstractCookingRecipe {
    private final HotpotSoupTypeFactoryHolder<?> targetSoup;

    public HotpotCookingRecipe(HotpotSoupTypeFactoryHolder<?> targetSoup, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(HotpotModEntry.HOTPOT_COOKING_RECIPE.get(), group, CookingBookCategory.FOOD, ingredient, result, experience, cookingTime);
        this.targetSoup = targetSoup;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_COOKING_RECIPE_SERIALIZER.get();
    }

    public boolean matches(IHotpotSoupType soupType) {
        return soupType.getSoupTypeFactoryHolder().equals(targetSoup);
    }

    public HotpotSoupTypeFactoryHolder<?> getTargetSoup() {
        return targetSoup;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<HotpotCookingRecipe> {
        public static final int DEFAULT_COOKING_TIME = 100;
        public static final float DEFAULT_EXPERIENCE = 0f;

        public static final MapCodec<HotpotCookingRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotSoupTypes.getHolderCodec().fieldOf("target_soup").forGetter(HotpotCookingRecipe::getTargetSoup),
                        Codec.STRING.fieldOf("group").forGetter(HotpotCookingRecipe::getGroup),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotCookingRecipe::getIngredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(HotpotCookingRecipe::getResult),
                        Codec.FLOAT.optionalFieldOf("experience", Serializer.DEFAULT_EXPERIENCE).forGetter(AbstractCookingRecipe::getExperience),
                        Codec.INT.optionalFieldOf("cooking_time", Serializer.DEFAULT_COOKING_TIME).forGetter(AbstractCookingRecipe::getCookingTime)
                ).apply(recipe, HotpotCookingRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotCookingRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotSoupTypes.getStreamHolderCodec(), HotpotCookingRecipe::getTargetSoup,
                        ByteBufCodecs.STRING_UTF8, Recipe::getGroup,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotCookingRecipe::getIngredient,
                        ItemStack.STREAM_CODEC, HotpotCookingRecipe::getResult,
                        ByteBufCodecs.FLOAT, AbstractCookingRecipe::getExperience,
                        ByteBufCodecs.INT, AbstractCookingRecipe::getCookingTime,
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
