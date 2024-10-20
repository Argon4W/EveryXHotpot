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

public class HotpotEmptyContent implements IHotpotContent {
    @Override
    public ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return List.of();
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks) {
        return false;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.EMPTY_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotRotatingContentSerializer<HotpotEmptyContent> {
        public static final HotpotEmptyContent UNIT = new HotpotEmptyContent();

        @Override
        public HotpotEmptyContent createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction) {
            return UNIT;
        }

        @Override
        public MapCodec<HotpotEmptyContent> getCodec() {
            return MapCodec.unit(UNIT);
        }

        @Override
        public int getPriority() {
            return 0;
        }

        public HotpotEmptyContent getEmptyContent() {
            return UNIT;
        }
    }
}
