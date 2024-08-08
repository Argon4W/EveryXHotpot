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
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotSoupBaseRecipe extends AbstractHotpotCommonInputRecipe {
    private final HotpotSoupTypeHolder<?> sourceSoupType;
    private final HotpotSoupTypeHolder<?> resultSoupType;
    private final float resultWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final Holder<SoundEvent> soundEvent;

    public HotpotSoupBaseRecipe(HotpotSoupTypeHolder<?> sourceSoupType, HotpotSoupTypeHolder<?> resultSoupType, float resultWaterLevel, Ingredient ingredient, ItemStack remainingItem, Holder<SoundEvent> soundEvent) {
        this.sourceSoupType = sourceSoupType;
        this.resultSoupType = resultSoupType;
        this.resultWaterLevel = resultWaterLevel;
        this.ingredient = ingredient;
        this.remainingItem = remainingItem;
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean matches(HotpotRecipeInput container, Level level) {
        return ingredient.test(container.itemStack()) && sourceSoupType.equals(container.soup());
    }

    public ItemStack getRemainingItem() {
        return remainingItem.copy();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public HotpotSoupTypeHolder<?> getSourceSoupType() {
        return sourceSoupType;
    }

    public HotpotSoupTypeHolder<?> getResultSoupType() {
        return resultSoupType;
    }

    public IHotpotSoup getResultSoup() {
        return resultSoupType.getSoup();
    }

    public Holder<SoundEvent> getSoundEvent() {
        return soundEvent;
    }

    public float getResultWaterLevel() {
        return resultWaterLevel;
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
        public static final MapCodec<HotpotSoupBaseRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotSoupTypeSerializers.getHolderCodec().fieldOf("source_soup").forGetter(HotpotSoupBaseRecipe::getSourceSoupType),
                        HotpotSoupTypeSerializers.getHolderCodec().fieldOf("result_soup").forGetter(HotpotSoupBaseRecipe::getResultSoupType),
                        Codec.FLOAT.fieldOf("result_waterlevel").forGetter(HotpotSoupBaseRecipe::getResultWaterLevel),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotSoupBaseRecipe::getIngredient),
                        ItemStack.OPTIONAL_CODEC.fieldOf("remaining_item").forGetter(HotpotSoupBaseRecipe::getRemainingItem),
                        SoundEvent.CODEC.fieldOf("sound_event").forGetter(HotpotSoupBaseRecipe::getSoundEvent)
                ).apply(recipe, HotpotSoupBaseRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupBaseRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotSoupTypeSerializers.getStreamHolderCodec(), HotpotSoupBaseRecipe::getSourceSoupType,
                        HotpotSoupTypeSerializers.getStreamHolderCodec(), HotpotSoupBaseRecipe::getResultSoupType,
                        ByteBufCodecs.FLOAT, HotpotSoupBaseRecipe::getResultWaterLevel,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupBaseRecipe::getIngredient,
                        ItemStack.OPTIONAL_STREAM_CODEC, HotpotSoupBaseRecipe::getRemainingItem,
                        SoundEvent.STREAM_CODEC, HotpotSoupBaseRecipe::getSoundEvent,
                        HotpotSoupBaseRecipe::new
                )
        );

        @Override
        public MapCodec<HotpotSoupBaseRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupBaseRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
