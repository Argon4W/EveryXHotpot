package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HotpotSoupContentCondition(Holder<IHotpotContentSerializer<?>> contentSerializerHolder) implements IHotpotSoupIngredientCondition {
    @Override
    public boolean matches(IHotpotContent content, IHotpotSoup soup) {
        return content.getContentSerializerHolder().equals(contentSerializerHolder);
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONTENT_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupContentCondition> {
        public static final MapCodec<HotpotSoupContentCondition> CODEC = RecordCodecBuilder.mapCodec(condition -> condition.group(
                HotpotContentSerializers.SERIALIZER_CODEC.fieldOf("content").forGetter(HotpotSoupContentCondition::contentSerializerHolder)
        ).apply(condition, HotpotSoupContentCondition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupContentCondition> STREAM_CODEC = StreamCodec.composite(
                HotpotContentSerializers.STREAM_CODEC, HotpotSoupContentCondition::contentSerializerHolder,
                HotpotSoupContentCondition::new
        );

        @Override
        public MapCodec<HotpotSoupContentCondition> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupContentCondition> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
