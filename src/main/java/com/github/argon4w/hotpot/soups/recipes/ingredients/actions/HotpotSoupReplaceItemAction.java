package com.github.argon4w.hotpot.soups.recipes.ingredients.actions;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.HotpotContentSerializers;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.ingredients.HotpotSoupIngredients;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientAction;
import com.github.argon4w.hotpot.soups.recipes.ingredients.IHotpotSoupIngredientActionSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupReplaceItemAction(ItemStack itemStack) implements IHotpotSoupIngredientAction {
    @Override
    public void action(int pos, HotpotBlockEntity hotpotBlockEntity, IHotpotContent content, HotpotComponentSoup sourceSoup, HotpotComponentSoup resultSoup, LevelBlockPos selfPos) {
        hotpotBlockEntity.setContent(pos, resultSoup.getContentSerializerResultFromItemStack(itemStack.copy(), hotpotBlockEntity, selfPos).orElse(HotpotContentSerializers.EMPTY_CONTENT_SERIALIZER).value().get(itemStack.copy(), hotpotBlockEntity, selfPos), selfPos);
    }

    @Override
    public IHotpotSoupIngredientActionSerializer<?> getSerializer() {
        return HotpotSoupIngredients.REPLACE_ACTION_SERIALIZER.get();
    }

    public static class Serializer implements IHotpotSoupIngredientActionSerializer<HotpotSoupReplaceItemAction> {
        public static final MapCodec<HotpotSoupReplaceItemAction> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(action -> action.group(
                        ItemStack.CODEC.fieldOf("result").forGetter(HotpotSoupReplaceItemAction::itemStack)
                ).apply(action, HotpotSoupReplaceItemAction::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupReplaceItemAction> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, HotpotSoupReplaceItemAction::itemStack,
                        HotpotSoupReplaceItemAction::new
                )
        );

        @Override
        public MapCodec<HotpotSoupReplaceItemAction> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotSoupReplaceItemAction> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
