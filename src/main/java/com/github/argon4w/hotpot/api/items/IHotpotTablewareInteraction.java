package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotTablewareContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareInteraction {
    void interact(
            int position,
            int layer,
            Player player,
            InteractionHand hand,
            ItemStack itemStack,
            IHotpotTablewareContainer blockEntity,
            LevelBlockPos pos);
}
