package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.fancytoys.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.api.soups.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.api.soups.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupContentCondition(Holder<IHotpotContentSerializer<?>> contentSerializerHolder) implements IHotpotSoupIngredientCondition {

    @Override
    public boolean matches(IHotpotContent content, HotpotComponentSoup soup) {
        return content.getContentSerializerHolder().equals(contentSerializerHolder);
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONTENT_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupContentCondition> {

        public static final MapCodec<HotpotSoupContentCondition> CODEC = LazyMapCodec.of(() -> HotpotContentSerializers.SERIALIZER_HOLDER_CODEC
                .fieldOf("content")
                .xmap(HotpotSoupContentCondition::new, HotpotSoupContentCondition::contentSerializerHolder));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupContentCondition> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> HotpotContentSerializers.SERIALIZER_HOLDER_STREAM_CODEC
                .map(HotpotSoupContentCondition::new, HotpotSoupContentCondition::contentSerializerHolder));

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
