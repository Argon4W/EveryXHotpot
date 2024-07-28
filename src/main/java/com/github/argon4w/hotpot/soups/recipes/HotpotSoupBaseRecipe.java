package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotSoupBaseRecipe extends AbstractHotpotSoupRecipe {
    private final ResourceLocation sourceSoup;
    private final ResourceLocation resultSoup;
    private final float resultWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final ResourceLocation soundEvent;

    public HotpotSoupBaseRecipe(ResourceLocation sourceSoup, ResourceLocation resultSoup, float resultWaterLevel, Ingredient ingredient, ItemStack remainingItem, ResourceLocation soundEvent) {
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
        return remainingItem.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return super.getResultItem(access);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ResourceLocation getSourceSoup() {
        return sourceSoup;
    }

    public ResourceLocation getResultSoup() {
        return resultSoup;
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
                        ResourceLocation.CODEC.fieldOf("source_soup").forGetter(HotpotSoupBaseRecipe::getSourceSoup),
                        ResourceLocation.CODEC.fieldOf("result_soup").forGetter(HotpotSoupBaseRecipe::getResultSoup),
                        Codec.FLOAT.fieldOf("result_waterlevel").forGetter(HotpotSoupBaseRecipe::getResultWaterLevel),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(HotpotSoupBaseRecipe::getIngredient),
                        ItemStack.OPTIONAL_CODEC.fieldOf("remaining_item").forGetter(HotpotSoupBaseRecipe::getRemainingItem),
                        ResourceLocation.CODEC.fieldOf("sound_event").forGetter(HotpotSoupBaseRecipe::getSoundEvent)
                ).apply(recipe, HotpotSoupBaseRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupBaseRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, HotpotSoupBaseRecipe::getSourceSoup,
                        ResourceLocation.STREAM_CODEC, HotpotSoupBaseRecipe::getResultSoup,
                        ByteBufCodecs.FLOAT, HotpotSoupBaseRecipe::getResultWaterLevel,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupBaseRecipe::getIngredient,
                        ItemStack.OPTIONAL_STREAM_CODEC, HotpotSoupBaseRecipe::getRemainingItem,
                        ResourceLocation.STREAM_CODEC, HotpotSoupBaseRecipe::getSoundEvent,
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
