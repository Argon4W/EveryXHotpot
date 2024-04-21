package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.AbstractTablewareInteractiveBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
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

public class HotpotChopstickItem extends HotpotPlacementBlockItem implements IHotpotTablewareItem, IHotpotItemContainer {
    public HotpotChopstickItem() {
        super(() -> HotpotPlacements.PLACED_CHOPSTICK.get().build(), new Properties().stacksTo(1));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placement, ItemStack itemStack) {
        if (placement instanceof HotpotPlacedChopstick placedChopstick) {
            placedChopstick.setChopstickItemStack(itemStack);
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (heldItemStack.isEdible() && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        if (!player.getInventory().add(heldItemStack)) {
            player.drop(heldItemStack, false);
        }

        setHeldItemStack(itemStack, ItemStack.EMPTY);

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return itemStack;
        }

        if (heldItemStack.isEdible()) {
            setHeldItemStack(itemStack, heldItemStack.finishUsingItem(level, livingEntity));
        }

        return itemStack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (heldItemStack.isEdible()) {
            return heldItemStack.getUseAnimation();
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        ItemStack heldItemStack = getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            return 0;
        }

        if (heldItemStack.isEdible()) {
            return 8;
        }

        return 0;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER;
            }
        });
    }

    @Override
    public void tablewareInteract(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, AbstractTablewareInteractiveBlockEntity blockEntity, LevelBlockPos selfPos) {
        if (!(itemStack.getItem() instanceof HotpotChopstickItem)) {
            return;
        }

        ItemStack heldItemStack = HotpotChopstickItem.getHeldItemStack(itemStack);

        if (heldItemStack.isEmpty()) {
            heldItemStack = blockEntity.tryTakeOutContentViaTableware(hitPos, selfPos);
        } else {
            heldItemStack = blockEntity.tryPlaceContentViaTableware(hitPos, player, hand, heldItemStack, selfPos);
        }

        if (heldItemStack.getItem().canFitInsideContainerItems()) {
            HotpotChopstickItem.setHeldItemStack(itemStack, heldItemStack);
        } else {
            selfPos.dropItemStack(heldItemStack);
            HotpotChopstickItem.setHeldItemStack(itemStack, ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        return getHeldItemStack(itemStack);
    }

    public static void setHeldItemStack(ItemStack chopstick, ItemStack itemStack) {
        HotpotTagsHelper.updateHotpotTags(chopstick, "ChopstickHeldItem", itemStack.save(new CompoundTag()));
    }

    public static ItemStack getHeldItemStack(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            return ItemStack.EMPTY;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return ItemStack.EMPTY;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("ChopstickHeldItem", Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(HotpotTagsHelper.getHotpotTags(itemStack).getCompound("ChopstickHeldItem"));
    }
}
