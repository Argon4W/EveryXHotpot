package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HotpotSoupIngredient(IHotpotSoupIngredientCondition condition, IHotpotSoupIngredientAction action) {
    public static final Codec<IHotpotSoupIngredientCondition> CONDITION_CODEC = HotpotSoupIngredients.getConditionRegistry().byNameCodec().dispatch(IHotpotSoupIngredientCondition::getSerializer, IHotpotSoupIngredientConditionSerializer::getCodec);
    public static final Codec<IHotpotSoupIngredientAction> ACTION_CODEC = HotpotSoupIngredients.getActionRegistry().byNameCodec().dispatch(IHotpotSoupIngredientAction::getSerializer, IHotpotSoupIngredientActionSerializer::getCodec);

    public static final MapCodec<HotpotSoupIngredient> CODEC = RecordCodecBuilder.mapCodec(ingredient -> ingredient.group(
            CONDITION_CODEC.fieldOf("condition").forGetter(HotpotSoupIngredient::condition),
            ACTION_CODEC.fieldOf("action").forGetter(HotpotSoupIngredient::action)
    ).apply(ingredient, HotpotSoupIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(HotpotSoupIngredients.CONDITION_REGISTRY_KEY).dispatch(IHotpotSoupIngredientCondition::getSerializer, IHotpotSoupIngredientConditionSerializer::getStreamCodec), HotpotSoupIngredient::condition,
            ByteBufCodecs.registry(HotpotSoupIngredients.ACTION_REGISTRY_KEY).dispatch(IHotpotSoupIngredientAction::getSerializer, IHotpotSoupIngredientActionSerializer::getStreamCodec), HotpotSoupIngredient::action,
            HotpotSoupIngredient::new
    );
}
