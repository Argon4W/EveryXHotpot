package com.github.argon4w.hotpot.soups.recipes.ingredients.conditions;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentFactory;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientCondition;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientConditionSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotSoupContentCondition(Holder<IHotpotContentFactory<?>> contentFactoryHolder) implements IHotpotSoupIngredientCondition {
    @Override
    public boolean matches(IHotpotContent content, IHotpotSoupType soup) {
        return content.getContentFactoryHolder().equals(contentFactoryHolder);
    }

    @Override
    public IHotpotSoupIngredientConditionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONTENT_CONDITION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientConditionSerializer<HotpotSoupContentCondition> {
        public static final MapCodec<HotpotSoupContentCondition> CODEC = RecordCodecBuilder.mapCodec(condition -> condition.group(
                HotpotContents.FACTORY_CODEC.fieldOf("content").forGetter(HotpotSoupContentCondition::contentFactoryHolder)
        ).apply(condition, HotpotSoupContentCondition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupContentCondition> STREAM_CODEC = StreamCodec.composite(
                HotpotContents.STREAM_CODEC, HotpotSoupContentCondition::contentFactoryHolder,
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

        @Override
        public ResourceLocation getType() {
            return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "content");
        }
    }
}
