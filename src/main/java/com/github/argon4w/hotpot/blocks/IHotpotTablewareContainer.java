package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.items.IHotpotTablewareItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareContainer {
    default void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        if (itemStack.getItem() instanceof IHotpotTablewareItem tablewareItem) {
            tablewareItem.interact(hitPos, layer, player, hand, itemStack, this, selfPos);
        } else {
            setContentByInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        }
    }

    default ItemStack setContentByTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        setContentByInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        return itemStack;
    }

    void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos);
    ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer, LevelBlockPos pos);
}
