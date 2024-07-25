package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class HotpotSoupRechargeRecipe extends AbstractHotpotSoupRecipe {
    private final ResourceLocation targetSoup;
    private final float rechargeWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final ResourceLocation soundEvent;

    public HotpotSoupRechargeRecipe(ResourceLocation targetSoup, float rechargeWaterLevel, Ingredient ingredient, ItemStack remainingItem, ResourceLocation soundEvent) {
        this.targetSoup = targetSoup;
        this.rechargeWaterLevel = rechargeWaterLevel;
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

    public ResourceLocation getTargetSoup() {
        return targetSoup;
    }

    public ResourceLocation getSoundEvent() {
        return soundEvent;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public float getRechargeWaterLevel() {
        return rechargeWaterLevel;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return HotpotModEntry.HOTPOT_SOUP_RECHARGE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HotpotSoupRechargeRecipe> {
        public static final MapCodec<HotpotSoupRechargeRecipe> CODEC = RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                ResourceLocation.CODEC.fieldOf("target_soup").forGetter(HotpotSoupRechargeRecipe::getTargetSoup),
                Codec.FLOAT.fieldOf("recharge_waterlevel").forGetter(HotpotSoupRechargeRecipe::getRechargeWaterLevel),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(HotpotSoupRechargeRecipe::getIngredient),
                ItemStack.CODEC.optionalFieldOf("remaining_item", ItemStack.EMPTY).forGetter(HotpotSoupRechargeRecipe::getRemainingItem),
                ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(HotpotSoupRechargeRecipe::getSoundEvent)
        ).apply(recipe, HotpotSoupRechargeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRechargeRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, HotpotSoupRechargeRecipe::getTargetSoup,
                ByteBufCodecs.FLOAT, HotpotSoupRechargeRecipe::getRechargeWaterLevel,
                Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupRechargeRecipe::getIngredient,
                ItemStack.STREAM_CODEC, HotpotSoupRechargeRecipe::getRemainingItem,
                ResourceLocation.STREAM_CODEC, HotpotSoupRechargeRecipe::getSoundEvent,
                HotpotSoupRechargeRecipe::new
        );

        @Override
        public MapCodec<HotpotSoupRechargeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRechargeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
