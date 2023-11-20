package com.github.argon4w.hotpot;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class ItemUtils1201 {
    public static ActionResult<ItemStack> startUsingInstantly(World p_150960_, PlayerEntity p_150961_, Hand p_150962_) {
        p_150961_.startUsingItem(p_150962_);
        return ActionResult.consume(p_150961_.getItemInHand(p_150962_));
    }

    public static ItemStack createFilledResult(ItemStack p_41818_, PlayerEntity p_41819_, ItemStack p_41820_, boolean p_41821_) {
        boolean flag = p_41819_.abilities.instabuild;
        if (p_41821_ && flag) {
            if (!p_41819_.inventory.contains(p_41820_)) {
                p_41819_.inventory.add(p_41820_);
            }

            return p_41818_;
        } else {
            if (!flag) {
                p_41818_.shrink(1);
            }

            if (p_41818_.isEmpty()) {
                return p_41820_;
            } else {
                if (!p_41819_.inventory.add(p_41820_)) {
                    p_41819_.drop(p_41820_, false);
                }

                return p_41818_;
            }
        }
    }

    public static ItemStack createFilledResult(ItemStack p_41814_, PlayerEntity p_41815_, ItemStack p_41816_) {
        return createFilledResult(p_41814_, p_41815_, p_41816_, true);
    }

    public static void onContainerDestroyed(ItemEntity p_150953_, Stream<ItemStack> p_150954_) {
        World level = p_150953_.level;
        if (!level.isClientSide) {
            p_150954_.forEach((p_289504_) -> {
                level.addFreshEntity(new ItemEntity(level, p_150953_.getX(), p_150953_.getY(), p_150953_.getZ(), p_289504_));
            });
        }
    }
}
