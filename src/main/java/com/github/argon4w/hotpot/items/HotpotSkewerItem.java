package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.items.HotpotClientItemExtensions;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.items.components.HotpotSkewerDataComponent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class HotpotSkewerItem extends Item implements IHotpotItemContainer, IHotpotSpecialHotpotCookingRecipeItem {
    public HotpotSkewerItem() {
        super(new Properties().component(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, HotpotSkewerDataComponent.EMPTY));
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        return isSkewerEmpty(itemStack) ? super.getMaxStackSize(itemStack) : 1;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ArrayList<ItemStack> itemStacks = new ArrayList<>(getSkewerItems(itemStack));

        if (itemStacks.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        ItemStack firstItemStack = itemStacks.getFirst();

        if (firstItemStack.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (isFood(firstItemStack) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        addToInventory(player, firstItemStack);
        itemStacks.removeFirst();

        setSkewerItems(itemStack, itemStacks);

        if (isSkewerEmpty(itemStack)) {
            itemStack.shrink(1);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>(getSkewerItems(itemStack));

        if (!(livingEntity instanceof Player player)) {
            return itemStack;
        }

        if (itemStacks.isEmpty()) {
            return itemStack;
        }

        ItemStack firstItemStack = itemStacks.getFirst();

        if (firstItemStack.isEmpty()) {
            return itemStack;
        }

        if (isFood(firstItemStack)) {
            itemStacks.set(0, firstItemStack.finishUsingItem(level, livingEntity));
        }

        firstItemStack = itemStacks.getFirst();

        if (!isFood(firstItemStack)) {
            addToInventory(player, firstItemStack);
            itemStacks.set(0, ItemStack.EMPTY);
        }

        if (itemStacks.getFirst().isEmpty()) {
            itemStacks.removeFirst();
        }

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        setSkewerItems(itemStack, itemStacks);

        if (isSkewerEmpty(itemStack)) {
            return ItemStack.EMPTY;
        }

        return itemStack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        List<ItemStack> itemStacks = getSkewerItems(itemStack);

        if (itemStacks.isEmpty()) {
            return UseAnim.NONE;
        }

        ItemStack foodItemStack = itemStacks.getFirst();

        if (foodItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (isFood(foodItemStack)) {
            return foodItemStack.getUseAnimation();
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        List<ItemStack> itemStacks = getSkewerItems(itemStack);

        if (itemStacks.isEmpty()) {
            return 0;
        }

        ItemStack foodItemStack = itemStacks.getFirst();

        if (foodItemStack.isEmpty()) {
            return 0;
        }

        if (!isFood(foodItemStack)) {
            return 0;
        }

        return (int) (foodItemStack.getUseDuration(livingEntity) * (0.5f / 1.5f));
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        List<ItemStack> itemStacks = getSkewerItems(itemStack);
        return itemStacks.isEmpty() ? ItemStack.EMPTY : itemStacks.get(0);
    }

    @Override
    public int getCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content) {
        return getSkewerItems(itemStack).stream().map(skewerStack -> content.remapCookingTime(soupType, skewerStack, pos, hotpotBlockEntity)).filter(Optional::isPresent).mapToInt(Optional::get).max().orElse(-1);
    }

    @Override
    public double getExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content) {
        return getSkewerItems(itemStack).stream().map(skewerStack -> content.remapExperience(soupType, skewerStack, pos, hotpotBlockEntity)).filter(Optional::isPresent).mapToDouble(Optional::get).sum();
    }

    @Override
    public ItemStack getResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, HotpotCookingRecipeContent content) {
        setSkewerItems(itemStack, getSkewerItems(itemStack).stream().map(skewerStack -> content.remapResult(soupType, skewerStack, pos, hotpotBlockEntity)).filter(Optional::isPresent).map(Optional::get).toList());
        return itemStack;
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return isSkewerEmpty(itemStack) ? super.getDescriptionId(itemStack) : super.getDescriptionId(itemStack) + ".hotpot";
    }

    public static boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD);
    }

    public static void setSkewerItems(ItemStack itemStack, List<ItemStack> itemStacks) {
        itemStack.set(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, getDataComponent(itemStack).setItemStacks(itemStacks));
    }

    public static HotpotSkewerDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, HotpotSkewerDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotSkewerDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, dataComponent);
    }

    public static List<ItemStack> getSkewerItems(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).itemStacks());
    }

    public static boolean isSkewerEmpty(ItemStack itemStack) {
        return getSkewerItems(itemStack).isEmpty();
    }

    public static void addSkewerItems(ItemStack itemStack, ItemStack added) {
        setDataComponent(itemStack, getDataComponent(itemStack).addItemStack(added));
    }

    public static ItemStack applyToSkewerItemStacks(ItemStack itemStack, UnaryOperator<ItemStack> operator) {
        itemStack.set(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, getDataComponent(itemStack).applyToItemStacks(operator));
        return itemStack;
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }
}
