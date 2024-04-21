package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.world.item.ItemStack;

public interface IHotpotSpecialContentItem {
    ItemStack onOtherContentUpdate(ItemStack self, ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    ItemStack updateSelf(ItemStack self, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
}
