package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.blocks.AbstractTablewareInteractiveBlockEntity;
import com.github.argon4w.hotpot.client.items.IHotpotSpecialRenderedItem;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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

public class HotpotChopstickItem extends HotpotPlacementBlockItem implements IHotpotTablewareItem, IHotpotSpecialRenderedItem {
    public HotpotChopstickItem() {
        super(() -> HotpotPlacements.PLACED_CHOPSTICK.get().build(), new Properties().stacksTo(1));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching();
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
                ItemStack notEdible = chopstickFoodItemStack.split(1);
                if (!player.getInventory().add(notEdible)) {
                    player.drop(notEdible, false);
                }

                setChopstickFoodItemStack(itemStack, chopstickFoodItemStack);
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
                setChopstickFoodItemStack(itemStack, chopstickFoodItemStack.finishUsingItem(level, livingEntity));
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
                return HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER;
            }
        });
    }

    @Override
    public void tablewareInteract(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, AbstractTablewareInteractiveBlockEntity blockEntity, LevelBlockPos selfPos) {
        ItemStack chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack);

        if (chopstickFoodItemStack.isEmpty()) {
            chopstickFoodItemStack = blockEntity.tryTakeOutContentViaChopstick(hitPos, selfPos);
        } else {
            chopstickFoodItemStack = blockEntity.tryPlaceContentViaChopstick(hitPos, player, hand, chopstickFoodItemStack, selfPos);
        }

        if (chopstickFoodItemStack.getItem().canFitInsideContainerItems()) {
            HotpotChopstickItem.setChopstickFoodItemStack(itemStack, chopstickFoodItemStack);
        } else {
            selfPos.dropItemStack(chopstickFoodItemStack);
            HotpotChopstickItem.setChopstickFoodItemStack(itemStack, ItemStack.EMPTY);
        }
    }

    @Override
    public ResourceLocation getSpecialRendererResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "chopstick_renderer");
    }

    public static void setChopstickFoodItemStack(ItemStack chopstick, ItemStack itemStack) {
        HotpotTagsHelper.updateHotpotTag(chopstick, compoundTag -> compoundTag.put("ChopstickContent", itemStack.save(new CompoundTag())));
    }

    public static ItemStack getChopstickFoodItemStack(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            return ItemStack.EMPTY;
        }

        if (!HotpotTagsHelper.hasHotpotTag(itemStack)) {
            return ItemStack.EMPTY;
        }

        if (!HotpotTagsHelper.getHotpotTag(itemStack).contains("ChopstickContent", Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(HotpotTagsHelper.getHotpotTag(itemStack).getCompound("ChopstickContent"));
    }
}
