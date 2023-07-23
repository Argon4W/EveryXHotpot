package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.placeables.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class HotpotChopstickItem extends HotpotPlaceableBlockItem {
    public HotpotChopstickItem() {
        super(HotpotDefinitions.getPlaceableOrElseEmpty("PlacedChopstick"), new Properties().stacksTo(1));
    }

    @Override
    public boolean shouldPlace(Player player, InteractionHand hand, BlockPosWithLevel pos) {
        return player.isCrouching();
    }

    @Override
    public void setAdditional(HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity, BlockPosWithLevel pos, IHotpotPlaceable placeable, ItemStack itemStack) {
        if (placeable instanceof HotpotPlacedChopstick placedChopstick) {
            placedChopstick.setChopstickItemStack(itemStack);
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                if (player.canEat(true)) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.consume(itemStack);
                } else {
                    return InteractionResultHolder.fail(itemStack);
                }
            } else {
                if (player.addItem(chopstickFoodItemStack.split(1))) {
                    player.drop(chopstickFoodItemStack, false);
                }

                itemStack.getTag().put("Item", chopstickFoodItemStack.save(new CompoundTag()));
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                itemStack.getTag().put("Item", chopstickFoodItemStack.finishUsingItem(level, livingEntity).save(new CompoundTag()));
            }
        }

        return itemStack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            if (chopstickFoodItemStack.isEdible()) {
                return chopstickFoodItemStack.getUseAnimation();
            }
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack;

        if (!(chopstickFoodItemStack = getChopstickFoodItemStack(itemStack)).isEmpty()) {
            return chopstickFoodItemStack.isEdible() ? 8 : 0;
        }

        return 0;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HotpotModEntry.HOTPOT_BEWLR;
            }
        });
    }

    public static ItemStack getChopstickFoodItemStack(ItemStack itemStack) {
        ItemStack chopstickFoodItemStack = ItemStack.EMPTY;

        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get()) && itemStack.hasTag() && itemStack.getTag().contains("Item", Tag.TAG_COMPOUND)) {
            chopstickFoodItemStack = ItemStack.of(itemStack.getTag().getCompound("Item"));
        }

        return chopstickFoodItemStack;
    }
}
