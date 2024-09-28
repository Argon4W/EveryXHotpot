package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.api.soups.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.api.soups.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupItemCondition(Ingredient ingredient) implements IHotpotSoupIngredientCondition {
    @Override
    public boolean matches(IHotpotContent content, HotpotComponentSoup soup) {
        return content instanceof AbstractHotpotItemStackContent itemStackContent && ingredient.test(itemStackContent.getItemStack());
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.ITEM_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupItemCondition> {
        public static final MapCodec<HotpotSoupItemCondition> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(condition -> condition.group(
                        Ingredient.CODEC.fieldOf("predicate").forGetter(HotpotSoupItemCondition::ingredient)
                ).apply(condition, HotpotSoupItemCondition::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupItemCondition> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, HotpotSoupItemCondition::ingredient,
                        HotpotSoupItemCondition::new
                )
        );

        @Override
        public MapCodec<HotpotSoupItemCondition> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupItemCondition> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
