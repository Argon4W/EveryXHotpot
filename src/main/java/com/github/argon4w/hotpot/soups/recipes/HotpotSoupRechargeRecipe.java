package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
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

public class HotpotSoupRechargeRecipe extends AbstractHotpotCommonInputRecipe {
    private final HotpotSoupTypeHolder<?> targetSoupType;
    private final float rechargeWaterLevel;
    private final Ingredient ingredient;
    private final ItemStack remainingItem;
    private final Holder<SoundEvent> soundEvent;

    public HotpotSoupRechargeRecipe(HotpotSoupTypeHolder<?> targetSoupType, float rechargeWaterLevel, Ingredient ingredient, ItemStack remainingItem, Holder<SoundEvent> soundEvent) {
        this.targetSoupType = targetSoupType;
        this.rechargeWaterLevel = rechargeWaterLevel;
        this.ingredient = ingredient;
        this.remainingItem = remainingItem;
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean matches(HotpotRecipeInput container, Level level) {
        return ingredient.test(container.itemStack()) && targetSoupType.equals(container.soup());
    }

    public ItemStack getRemainingItem() {
        return remainingItem.copy();
    }

    public HotpotSoupTypeHolder<?> getTargetSoupType() {
        return targetSoupType;
    }

    public Holder<SoundEvent> getSoundEvent() {
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
        public static final MapCodec<HotpotSoupRechargeRecipe> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(recipe -> recipe.group(
                        HotpotSoupTypeSerializers.getHolderCodec().fieldOf("target_soup").forGetter(HotpotSoupRechargeRecipe::getTargetSoupType),
                        Codec.FLOAT.fieldOf("recharge_waterlevel").forGetter(HotpotSoupRechargeRecipe::getRechargeWaterLevel),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(HotpotSoupRechargeRecipe::getIngredient),
                        ItemStack.OPTIONAL_CODEC.optionalFieldOf("remaining_item", ItemStack.EMPTY).forGetter(HotpotSoupRechargeRecipe::getRemainingItem),
                        SoundEvent.CODEC.fieldOf("sound_event").forGetter(HotpotSoupRechargeRecipe::getSoundEvent)
                ).apply(recipe, HotpotSoupRechargeRecipe::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupRechargeRecipe> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        HotpotSoupTypeSerializers.getStreamHolderCodec(), HotpotSoupRechargeRecipe::getTargetSoupType,
                        ByteBufCodecs.FLOAT, HotpotSoupRechargeRecipe::getRechargeWaterLevel,
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupRechargeRecipe::getIngredient,
                        ItemStack.OPTIONAL_STREAM_CODEC, HotpotSoupRechargeRecipe::getRemainingItem,
                        SoundEvent.STREAM_CODEC, HotpotSoupRechargeRecipe::getSoundEvent,
                        HotpotSoupRechargeRecipe::new
                )
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
