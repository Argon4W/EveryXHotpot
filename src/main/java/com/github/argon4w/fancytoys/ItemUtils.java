package com.github.argon4w.fancytoys;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {

    public static void consumeAndReturnRemaining(Player player, ItemStack itemStack, ItemStack remainingItem) {
        itemStack.consume(1, player);

        if (!player.hasInfiniteMaterials() || !player.getInventory().contains(remainingItem)) {
            player.getInventory().add(remainingItem);
        }
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }
}
