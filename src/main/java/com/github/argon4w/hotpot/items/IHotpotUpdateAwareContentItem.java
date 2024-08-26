package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.world.item.ItemStack;

public interface IHotpotUpdateAwareContentItem {
    ItemStack onContentUpdate(ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
}
