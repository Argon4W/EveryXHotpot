package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.item.ItemStack;

public interface IHotpotSpecialContentItem {
    ItemStack onOtherContentUpdate(ItemStack selfItemStack, ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    ItemStack getSelfItemStack(ItemStack selfItemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
}
