package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.*;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.items.HotpotStrainerBasketItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class HotpotStrainerBasketContent implements IHotpotPickableContent, IHotpotItemUpdaterContent {
    private final Direction direction;
    private final NonNullList<IHotpotContent> basketContents;

    public HotpotStrainerBasketContent(Direction direction, NonNullList<IHotpotContent> basketContents) {
        this.direction = direction;
        this.basketContents = basketContents;
    }

    public HotpotStrainerBasketContent(List<ItemStack> itemStacks, Direction direction, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        this.direction = direction;
        this.basketContents = IntStream.range(0, Math.min(8, itemStacks.size())).collect(() -> NonNullList.withSize(8, HotpotContentSerializers.loadEmptyContent()), (contents, i) -> contents.set(i, hotpotBlockEntity.getSoup().getContentSerializerResultFromItemStack(itemStacks.get(i), hotpotBlockEntity, pos).orElse(HotpotContentSerializers.ITEM_STACK_DUMMY_CONTENT_SERIALIZER).value().createContent(itemStacks.get(i).copy(), hotpotBlockEntity, pos, direction)), (contents1, contents2) -> {});
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return HotpotStrainerBasketItem.createStrainerBasketFromItems(basketContents.stream().map(content -> hotpotBlockEntity.getSoup().getContentResultByTableware(content, hotpotBlockEntity, pos).map(c -> c.getContentItemStack(hotpotBlockEntity, pos).copy()).orElse(ItemStack.EMPTY)).filter(Predicate.not(ItemStack::isEmpty)).toList());
    }

    @Override
    public List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return List.of(getContentItemStack(hotpotBlockEntity, pos));
    }

    @Override
    public boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks) {
        basketContents.stream().filter(content -> content.onTick(hotpotBlockEntity, pos, ticks)).peek(content -> hotpotBlockEntity.getSoup().onContentUpdate(content, hotpotBlockEntity, pos)).findAny().ifPresent(content -> hotpotBlockEntity.markDataChanged());
        return false;
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public void updateItemStack(Consumer<ItemStack> consumer) {
        basketContents.stream().filter(content -> content instanceof IHotpotItemUpdaterContent).forEach(content -> ((IHotpotItemUpdaterContent) content).updateItemStack(consumer));
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.STRAINER_BASKET_CONTENT_SERIALIZER;
    }

    public Direction getDirection() {
        return direction;
    }

    public NonNullList<IHotpotContent> getBasketContents() {
        return basketContents;
    }

    public static class Serializer extends AbstractHotpotFixedContentSerializer<HotpotStrainerBasketContent> {
        public static final MapCodec<HotpotStrainerBasketContent> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(content -> content.group(
                    Direction.CODEC.fieldOf("direction").forGetter(HotpotStrainerBasketContent::getDirection),
                    HotpotContentSerializers.HOTPOT_CONTENTS_CODEC.fieldOf("basket_contents").forGetter(HotpotStrainerBasketContent::getBasketContents)
                ).apply(content, HotpotStrainerBasketContent::new))
        );

        @Override
        public HotpotStrainerBasketContent createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction) {
            return new HotpotStrainerBasketContent(HotpotStrainerBasketItem.getStrainerBasketItems(itemStack.split(1)), direction, hotpotBlockEntity, pos);
        }

        @Override
        public MapCodec<HotpotStrainerBasketContent> getCodec() {
            return CODEC;
        }
    }
}
