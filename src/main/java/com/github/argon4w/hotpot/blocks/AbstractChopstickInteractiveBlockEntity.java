package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractChopstickInteractiveBlockEntity extends BlockEntity {
    public AbstractChopstickInteractiveBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            ItemStack chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);
            itemStack.getOrCreateTag().put("Item", (chopstickFoodItemStack.isEmpty() ?
                    tryTakeOutContentViaChopstick(hitSection, selfPos)
                    : tryPlaceContentViaChopstick(hitSection, player, hand, chopstickFoodItemStack, selfPos)
            ).save(new CompoundTag()));

            return;
        }

        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);
    }

    public abstract ItemStack tryPlaceContentViaChopstick(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos);
    public abstract void tryPlaceContentViaInteraction(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos);
    public abstract ItemStack tryTakeOutContentViaChopstick(int hitSection, BlockPosWithLevel pos);
}
