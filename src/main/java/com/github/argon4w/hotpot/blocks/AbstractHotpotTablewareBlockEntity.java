package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.items.IHotpotTablewareItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractHotpotTablewareBlockEntity extends BlockEntity {
    public AbstractHotpotTablewareBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        if (itemStack.getItem() instanceof IHotpotTablewareItem tablewareItem) {
            tablewareItem.interact(hitPos, layer, player, hand, itemStack, this, selfPos);
        } else {
            setContentByInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        }
    }

    public abstract ItemStack setContentByTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
    public abstract void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
    public abstract ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer, LevelBlockPos pos);
}
