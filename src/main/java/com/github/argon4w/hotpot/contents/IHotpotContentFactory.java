package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;

public interface IHotpotContentFactory<T extends IHotpotContent> {
    T buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity);
    MapCodec<T> buildFromCodec();
}
