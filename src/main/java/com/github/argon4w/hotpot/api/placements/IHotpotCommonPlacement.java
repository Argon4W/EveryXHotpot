package com.github.argon4w.hotpot.api.placements;

import net.minecraft.world.item.ItemStack;

public interface IHotpotCommonPlacement extends IHotpotPlacement {
    void setCommonItemSlot(ItemStack itemStack);
}
