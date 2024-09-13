package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
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

    public static class Serializer implements IHotpotContentSerializer<HotpotEmptyContent> {

        @Override
        public HotpotEmptyContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return get();
        }

        @Override
        public MapCodec<HotpotEmptyContent> getCodec() {
            return MapCodec.unit(this::get);
        }

        public HotpotEmptyContent get() {
            return new HotpotEmptyContent();
        }
    }
}
