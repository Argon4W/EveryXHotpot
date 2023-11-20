package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;
import com.github.argon4w.hotpot.placeables.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.function.Consumer;

public class HotpotChopstickItem extends HotpotPlaceableBlockItem {
    public HotpotChopstickItem() {
        super(HotpotPlaceables.getPlaceableOrElseEmpty("PlacedChopstick"), new Properties().setISTER(() -> HotpotBlockEntityWithoutLevelRenderer::new).stacksTo(1).tab(HotpotModEntry.HOTPOT_ITEM_GROUP));
    }

    @Override
    public boolean shouldPlace(PlayerEntity player, Hand hand, BlockPosWithLevel pos) {
        return player.isCrouching();
    }

    @Override
    public void setAdditional(HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity, BlockPosWithLevel pos, IHotpotPlaceable placeable, ItemStack itemStack) {
        if (placeable instanceof HotpotPlacedChopstick) {
            ((HotpotPlacedChopstick) placeable).setChopstickItemStack(itemStack);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                if (player.canEat(true)) {
                    player.startUsingItem(hand);
                    return ActionResult.consume(itemStack);
                } else {
                    return ActionResult.fail(itemStack);
                }
            } else {
                ItemStack notEdible = chopstickFoodItemStack.split(1);
                if (!player.inventory.add(notEdible)) {
                    player.drop(notEdible, false);
                }

                setChopstickFoodItemStack(itemStack, chopstickFoodItemStack);
                return ActionResult.pass(player.getItemInHand(hand));
            }
        }

        return ActionResult.pass(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World level, LivingEntity livingEntity) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                setChopstickFoodItemStack(itemStack, chopstickFoodItemStack.finishUsingItem(level, livingEntity));
            }
        }

        return itemStack;
    }



    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                return chopstickFoodItemStack.getUseAnimation();
            }
        }

        return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            return chopstickFoodItemStack.isEdible() ? 8 : 0;
        }

        return 0;
    }

    public static void setChopstickFoodItemStack(ItemStack chopstick, ItemStack itemStack) {
        HotpotTagsHelper.updateHotpotTag(chopstick, compoundTag -> compoundTag.put("ChopstickContent", itemStack.save(new CompoundNBT())));
    }

    public static ItemStack getChopstickFoodItemStack(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack = ItemStack.EMPTY;

        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get()) && HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("ChopstickContent", Constants.NBT.TAG_COMPOUND)) {
            chopstickFoodItemStack = ItemStack.of(HotpotTagsHelper.getHotpotTag(itemStack).getCompound("ChopstickContent"));
        }

        return chopstickFoodItemStack;
    }
}
