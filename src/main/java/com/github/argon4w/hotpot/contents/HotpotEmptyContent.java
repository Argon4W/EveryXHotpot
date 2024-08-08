package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotEmptyContent implements IHotpotContent {
    @Override
    public ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float ticks) {
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
        public HotpotEmptyContent getFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
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
