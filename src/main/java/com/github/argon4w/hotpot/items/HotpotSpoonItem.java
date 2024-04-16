package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.AbstractTablewareInteractiveBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HotpotSpoonItem extends HotpotPlacementBlockItem implements IHotpotTablewareItem {
    public HotpotSpoonItem() {
        super(() -> HotpotPlacements.PLACED_SPOON.get().build(), new Properties().stacksTo(1));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching();
    }

    @Override
    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placement, ItemStack itemStack) {
        if (placement instanceof HotpotPlacedSpoon placedSpoon) {
            placedSpoon.setSpoonItemStack(itemStack);
        }
    }

    @Override
    public void tablewareInteract(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, AbstractTablewareInteractiveBlockEntity blockEntity, LevelBlockPos selfPos) {

    }
}
