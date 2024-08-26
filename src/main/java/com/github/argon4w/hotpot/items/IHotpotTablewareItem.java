package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.AbstractHotpotTablewareBlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareItem {
    void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, AbstractHotpotTablewareBlockEntity blockEntity, LevelBlockPos selfPos);
}
