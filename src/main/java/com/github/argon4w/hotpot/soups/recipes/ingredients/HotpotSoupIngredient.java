package com.github.argon4w.hotpot.soups.recipes.ingredients;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupIngredient(IHotpotSoupIngredientCondition condition, IHotpotSoupIngredientAction action, int amount) {
    public static final Codec<IHotpotSoupIngredientCondition> CONDITION_CODEC = Codec.lazyInitialized(() -> HotpotSoupIngredients.getConditionRegistry().byNameCodec().dispatch(IHotpotSoupIngredientCondition::getSerializer, IHotpotSoupIngredientConditionSerializer::getCodec));
    public static final Codec<IHotpotSoupIngredientAction> ACTION_CODEC = Codec.lazyInitialized(() -> HotpotSoupIngredients.getActionRegistry().byNameCodec().dispatch(IHotpotSoupIngredientAction::getSerializer, IHotpotSoupIngredientActionSerializer::getCodec));

    public static final MapCodec<HotpotSoupIngredient> CODEC = LazyMapCodec.of(() ->
            RecordCodecBuilder.mapCodec(ingredient -> ingredient.group(
                    CONDITION_CODEC.fieldOf("condition").forGetter(HotpotSoupIngredient::condition),
                    ACTION_CODEC.fieldOf("action").forGetter(HotpotSoupIngredient::action),
                    Codec.INT.optionalFieldOf("amount", 1).forGetter(HotpotSoupIngredient::amount)
            ).apply(ingredient, HotpotSoupIngredient::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupIngredient> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.registry(HotpotSoupIngredients.CONDITION_REGISTRY_KEY).dispatch(IHotpotSoupIngredientCondition::getSerializer, IHotpotSoupIngredientConditionSerializer::getStreamCodec), HotpotSoupIngredient::condition,
                    ByteBufCodecs.registry(HotpotSoupIngredients.ACTION_REGISTRY_KEY).dispatch(IHotpotSoupIngredientAction::getSerializer, IHotpotSoupIngredientActionSerializer::getStreamCodec), HotpotSoupIngredient::action,
                    ByteBufCodecs.INT, HotpotSoupIngredient::amount,
                    HotpotSoupIngredient::new
            )
    );
}
