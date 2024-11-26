package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.item.ItemStack;

public interface IHotpotUpdateAwareContentItem {

    ItemStack onContentUpdate(
            ItemStack itemStack,
            IHotpotContent content,
            HotpotBlockEntity hotpotBlockEntity,
            LevelBlockPos pos);
}
