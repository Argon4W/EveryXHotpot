package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
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
    public boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return false;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_content");
    }

    @Override
    public IHotpotContentFactory<?> getFactory() {
        return HotpotContents.EMPTY_CONTENT.get();
    }

    public static class Factory implements IHotpotContentFactory<HotpotEmptyContent> {

        @Override
        public HotpotEmptyContent buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
            return build();
        }

        @Override
        public MapCodec<HotpotEmptyContent> buildFromCodec() {
            return MapCodec.unit(this::build);
        }

        public HotpotEmptyContent build() {
            return new HotpotEmptyContent();
        }
    }
}
