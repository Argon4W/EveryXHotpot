package com.github.argon4w.hotpot.api.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import net.minecraft.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareContainer extends IHotpotTablewareInteraction {
    @Override
    default void interact(
            int position,
            int layer,
            Player player,
            InteractionHand hand,
            ItemStack itemStack,
            IHotpotTablewareContainer blockEntity,
            LevelBlockPos pos) {
        setContentByInteraction(position, layer, player, hand, itemStack, pos);
    }

    default void interact(
            int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        (itemStack.getItem() instanceof IHotpotTablewareInteraction interaction ? interaction : this)
                .interact(position, layer, player, hand, itemStack, this, pos);
    }

    default ItemStack setContentByTableware(
            int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        return Util.make(
                itemStack, itemStack1 -> setContentByInteraction(position, layer, player, hand, itemStack1, pos));
    }

    void setContentByInteraction(
            int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos);

    ItemStack getContentByTableware(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos);
}
