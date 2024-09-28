package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import net.minecraft.world.item.ItemStack;

public interface IHotpotUpdateAwareContentItem {
    ItemStack onContentUpdate(ItemStack itemStack, IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
}
