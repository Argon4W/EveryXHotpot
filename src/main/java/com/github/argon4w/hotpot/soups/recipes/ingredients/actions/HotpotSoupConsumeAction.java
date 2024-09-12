package com.github.argon4w.hotpot.soups.recipes.ingredients.actions;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientActionSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HotpotSoupConsumeAction implements IHotpotSoupIngredientAction {
    @Override
    public void action(int pos, HotpotBlockEntity hotpotBlockEntity, IHotpotContent content, HotpotComponentSoup sourceSoup, HotpotComponentSoup resultSoup, LevelBlockPos selfPos) {
        hotpotBlockEntity.setEmptyContent(pos);
    }

    @Override
    public IHotpotSoupIngredientActionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.CONSUME_ACTION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientActionSerializer<HotpotSoupConsumeAction> {
        public static final HotpotSoupConsumeAction UNIT = new HotpotSoupConsumeAction();

        public static final MapCodec<HotpotSoupConsumeAction> CODEC = MapCodec.unit(UNIT);
        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupConsumeAction> STREAM_CODEC = StreamCodec.unit(UNIT);

        @Override
        public MapCodec<HotpotSoupConsumeAction> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupConsumeAction> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
