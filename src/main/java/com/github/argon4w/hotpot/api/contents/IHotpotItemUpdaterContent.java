package com.github.argon4w.hotpot.api.contents;

import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;

public interface IHotpotItemUpdaterContent extends IHotpotContent {
    void updateItemStack(Consumer<ItemStack> consumer);
}
