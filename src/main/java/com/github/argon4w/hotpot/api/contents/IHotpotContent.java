package com.github.argon4w.hotpot.api.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IHotpotContent {
    boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    ItemStack getContentItemStack(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    boolean onTick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, double ticks);
    void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    Holder<IHotpotContentSerializer<?>> getContentSerializerHolder();
}
