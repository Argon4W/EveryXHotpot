package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.items.IHotpotTablewareItem;
import net.minecraft.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareContainer extends IHotpotTablewareItem {
    @Override
    default void interact(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, IHotpotTablewareContainer blockEntity, LevelBlockPos pos) {
        setContentByInteraction(position, layer, player, hand, itemStack, pos);
    }

    default void interact(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        (itemStack.getItem() instanceof IHotpotTablewareItem tablewareItem ? tablewareItem : this).interact(position, layer, player, hand, itemStack, this, pos);
    }

    default ItemStack setContentByTableware(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        return Util.make(itemStack, itemStack2 -> setContentByInteraction(position, layer, player, hand, itemStack2, pos));
    }

    ItemStack getContentByTableware(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos);
    void setContentByInteraction(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos);
}
