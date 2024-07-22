package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotEmptyContent implements IHotpotContent {
    public HotpotEmptyContent() {}

    @Override
    public void create(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }

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
    public IHotpotContent load(CompoundTag compoundTag) {
        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return true;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "empty_content");
    }

    @Override
    public String toString() {
        return getResourceLocation().toString();
    }
}
