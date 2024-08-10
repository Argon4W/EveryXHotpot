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

public abstract class AbstractTablewareInteractiveBlockEntity extends BlockEntity {
    public AbstractTablewareInteractiveBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        if (itemStack.getItem() instanceof IHotpotTablewareItem tablewareItem) {
            tablewareItem.tablewareInteract(hitPos, layer, player, hand, itemStack, this, selfPos);
        } else {
            setContentViaInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        }
    }

    public abstract ItemStack setContentViaTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
    public abstract void setContentViaInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
    public abstract ItemStack getContentViaTableware(Player player, InteractionHand hand, int hitPos, int layer, LevelBlockPos pos);
}
