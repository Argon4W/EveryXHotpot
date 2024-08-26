package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.AbstractHotpotRecipeContent;
import com.github.argon4w.hotpot.items.components.HotpotSkewerDataComponent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HotpotSkewerItem extends Item implements IHotpotItemContainer, IHotpotCookingRecipeHolder, IHotpotCustomItemStackUpdaterProvider {
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

        ItemStack firstItemStack = itemStacks.getFirst().copy();

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

        ItemStack firstItemStack = itemStacks.getFirst().copy();

        if (isFood(firstItemStack)) {
            itemStacks.set(0, firstItemStack.finishUsingItem(level, livingEntity));
        }

        firstItemStack = itemStacks.getFirst().copy();

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
    public IHotpotItemStackUpdater getItemStackUpdater() {
        return (itemStack, consumer) -> {
            applySkewerItems(itemStack, consumer);
            return itemStack;
        };
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        return isSkewerEmpty(itemStack) ? ItemStack.EMPTY : getSkewerItems(itemStack).getFirst();
    }

    @Override
    public int getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return getSkewerItems(itemStack).stream().map(skewerStack -> content.getCookingTime(soup, skewerStack, pos, hotpotBlockEntity)).filter(Optional::isPresent).mapToInt(Optional::get).max().orElse(-1);
    }

    @Override
    public double getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        return getSkewerItems(itemStack).stream().map(skewerStack -> content.getExperience(soup, skewerStack, pos, hotpotBlockEntity)).filter(Optional::isPresent).mapToDouble(Optional::get).sum();
    }

    @Override
    public ItemStack getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity, AbstractHotpotRecipeContent content) {
        setSkewerItems(itemStack, getSkewerItems(itemStack).stream().map(skewerStack -> content.getResult(soup, skewerStack, pos, hotpotBlockEntity).orElse(skewerStack)).toList());
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
        setDataComponent(itemStack, getDataComponent(itemStack).setItemStacks(itemStacks));
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

    public static void applySkewerItems(ItemStack itemStack, Consumer<ItemStack> consumer) {
        setDataComponent(itemStack, getDataComponent(itemStack).applyItemStacks(consumer));
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }
}
