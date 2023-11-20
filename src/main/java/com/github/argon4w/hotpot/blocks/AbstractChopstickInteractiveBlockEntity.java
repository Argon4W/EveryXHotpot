package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractChopstickInteractiveBlockEntity extends TileEntity {
    public AbstractChopstickInteractiveBlockEntity(TileEntityType<?> p_155228_) {
        super(p_155228_);
    }

    public void interact(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            ItemStack chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
            chopstickFoodItemStack = chopstickFoodItemStack.isEmpty() ?
                    tryTakeOutContentViaChopstick(hitSection, selfPos)
                    : tryPlaceContentViaChopstick(hitSection, player, hand, chopstickFoodItemStack, selfPos);

            if (!(Block.byItem(chopstickFoodItemStack.getItem()) instanceof ShulkerBoxBlock)) {
                HotpotChopstickItem.setChopstickFoodItemStack(itemStack, chopstickFoodItemStack);
            } else {
                selfPos.dropItemStack(chopstickFoodItemStack);
                HotpotChopstickItem.setChopstickFoodItemStack(itemStack, ItemStack.EMPTY);
            }

            return;
        }

        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);
    }

    public abstract ItemStack tryPlaceContentViaChopstick(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos);
    public abstract void tryPlaceContentViaInteraction(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos);
    public abstract ItemStack tryTakeOutContentViaChopstick(int hitSection, BlockPosWithLevel pos);
}
