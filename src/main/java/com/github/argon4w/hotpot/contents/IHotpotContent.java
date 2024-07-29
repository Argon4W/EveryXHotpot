package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotContent {
    boolean tick(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    ItemStack takeOut(Player player, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void onOtherContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    Holder<IHotpotContentFactory<?>> getContentFactoryHolder();
}
