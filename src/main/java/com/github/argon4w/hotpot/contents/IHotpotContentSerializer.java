package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;

public interface IHotpotContentSerializer<T extends IHotpotContent> {
    T getFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    MapCodec<T> getCodec();
}
