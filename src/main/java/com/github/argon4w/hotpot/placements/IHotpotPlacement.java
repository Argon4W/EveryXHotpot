package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IHotpotPlacement {
    boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container);
    ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware);
    void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos);
    ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos);
    List<Integer> getPoslist();
    boolean isConflict(int pos);
    Holder<IHotpotPlacementFactory<?>> getPlacementFactoryHolder();

    static float getSlotX(int slot) {
        return ((2 & slot) > 0 ? 0.5f : 0f);
    }

    static float getSlotZ(int slot) {
        return ((1 & slot) > 0 ? 0.5f : 0f);
    }
}
