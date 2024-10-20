package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.AbstractHotpotRotatingContentSerializer;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record HotpotItemStackDummyContent(ItemStack itemStack) implements IHotpotContent {
    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack;
    }

    @Override
    public List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return List.of(getContentItemStack(hotpotBlockEntity, pos));
    }

    @Override
    public boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks) {
        return false;
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.ITEM_STACK_DUMMY_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotRotatingContentSerializer<HotpotItemStackDummyContent> {
        public static final MapCodec<HotpotItemStackDummyContent> CODEC = ItemStack.CODEC.fieldOf("item_stack").xmap(HotpotItemStackDummyContent::new, HotpotItemStackDummyContent::itemStack);

        @Override
        public HotpotItemStackDummyContent createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction) {
            return new HotpotItemStackDummyContent(itemStack);
        }

        @Override
        public MapCodec<HotpotItemStackDummyContent> getCodec() {
            return CODEC;
        }
    }
}
