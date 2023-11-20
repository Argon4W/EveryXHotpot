package com.github.argon4w.hotpot;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class HotpotItemGroup extends ItemGroup {
    public HotpotItemGroup() {
        super("EveryXHotpot");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(HotpotModEntry.HOTPOT_BLOCK_ITEM.get());
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> p_78018_1_) {
        super.fillItemList(p_78018_1_);
        System.out.println(p_78018_1_);
    }
}
