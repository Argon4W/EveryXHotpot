package com.github.argon4w.hotpot.api.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IHotpotContentSerializer<T extends IHotpotContent> {
    T createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction);
    MapCodec<T> getCodec();
    int indexToPosition(int index, int time);
    int positionToIndex(int clickPosition, int time);
    int getPriority();
}
