package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotPlacementContainerBlockEntity {
    void markDataChanged();
    boolean isInfiniteContent();
    boolean place(IHotpotPlacement placement, int pos, int layer);
    void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
}
