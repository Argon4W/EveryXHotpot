package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotPaperBowlDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.types.HotpotEmptySoupType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotPaperBowlItem extends HotpotPlacementBlockItem<HotpotPlacedPaperBowl> implements IHotpotItemContainer {
    public HotpotPaperBowlItem() {
        super(HotpotPlacements.PLACED_PAPER_BOWL, new Properties().stacksTo(64).component(HotpotModEntry.HOTPOT_PAPER_BOWL_DATA_COMPONENT, HotpotPaperBowlDataComponent.EMPTY));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, HotpotPlacedPaperBowl placement, ItemStack itemStack) {
        placement.setPaperBowlItemSlot(itemStack.copyWithCount(1));
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        return isPaperBowlClear(itemStack) ? super.getMaxStackSize(itemStack) : 1;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (isPaperBowlClear(itemStack)) {
            return InteractionResultHolder.success(itemStack);
        }

        ArrayList<ItemStack> items = new ArrayList<>(getPaperBowlItems(itemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(getPaperBowlSkewers(itemStack));

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

        if (itemStacks.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        ItemStack firstItemStack = itemStacks.getFirst();

        if (canEatInPaperBowl(firstItemStack) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        addToInventory(player, firstItemStack);
        itemStacks.removeFirst();

        setPaperBowlItems(itemStack, items);
        setPaperBowlSkewers(itemStack, skewers);

        if (isPaperBowlEmpty(itemStack)) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) {
            return itemStack;
        }

        if (isPaperBowlClear(itemStack)) {
            return itemStack;
        }

        ArrayList<ItemStack> items = new ArrayList<>(getPaperBowlItems(itemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(getPaperBowlSkewers(itemStack));

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

        if (itemStacks.isEmpty()) {
            return itemStack;
        }

        ItemStack firstItemStack = itemStacks.getFirst();

        if (canEatInPaperBowl(firstItemStack)) {
            itemStacks.set(0, firstItemStack.finishUsingItem(level, player));
        }

        firstItemStack = itemStacks.getFirst();

        if (!canEatInPaperBowl(firstItemStack)) {
            addToInventory(player, firstItemStack);
            itemStacks.removeFirst();
        }

        setPaperBowlItems(itemStack, items);
        setPaperBowlSkewers(itemStack, skewers);

        if (isPaperBowlEmpty(itemStack)) {
            return ItemStack.EMPTY;
        }

        return itemStack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        List<ItemStack> itemStacks = getPaperBowlItems(itemStack);

        if (itemStacks.isEmpty()) {
            itemStacks = new ArrayList<>(getPaperBowlSkewers(itemStack));
        }

        if (itemStacks.isEmpty()) {
            return UseAnim.NONE;
        }

        ItemStack foodItemStack = itemStacks.getFirst();

        if (foodItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (canEatInPaperBowl(foodItemStack)) {
            return foodItemStack.getUseAnimation();
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        List<ItemStack> itemStacks = getPaperBowlItems(itemStack);

        if (itemStacks.isEmpty()) {
            itemStacks = new ArrayList<>(getPaperBowlSkewers(itemStack));
        }

        if (itemStacks.isEmpty()) {
            return 0;
        }

        ItemStack foodItemStack = itemStacks.getFirst();

        if (foodItemStack.isEmpty()) {
            return 0;
        }

        if (!canEatInPaperBowl(foodItemStack)) {
            return 0;
        }

        if (isPaperBowlDrained(itemStack)) {
            return foodItemStack.getUseDuration(livingEntity);
        }

        return (int) (foodItemStack.getUseDuration(livingEntity) * 1.5f);
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        List<ItemStack> itemStacks = getPaperBowlItems(itemStack);

        if (itemStacks.isEmpty()) {
            itemStacks = getPaperBowlSkewers(itemStack);
        }

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack containedItemStack = itemStacks.getFirst();

        if (containedItemStack.getItem() instanceof IHotpotItemContainer itemContainer) {
            return itemContainer.getContainedItemStack(containedItemStack);
        }

        return containedItemStack;
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        if (isPaperBowlEmpty(itemStack)) {
            return super.getDescriptionId(itemStack);
        }

        if (!getPaperBowlSkewers(itemStack).isEmpty()) {
            return super.getDescriptionId(itemStack) + ".skewer";
        }

        if (isPaperBowlDrained(itemStack)) {
            return super.getDescriptionId(itemStack) + ".drained";
        }

        return super.getDescriptionId(itemStack) + ".hotpot";
    }

    public static boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD);
    }

    public static boolean canEatInPaperBowl(ItemStack itemStack) {
        return isFood(itemStack) || (itemStack.is(HotpotModEntry.HOTPOT_SKEWER) && !HotpotSkewerItem.isSkewerEmpty(itemStack));
    }

    public static boolean isPaperBowlEmpty(ItemStack itemStack) {
        return getDataComponent(itemStack).isPaperBowlEmpty();
    }

    public static boolean isPaperBowlUsed(ItemStack itemStack) {
        return itemStack.isEmpty() || (isPaperBowlEmpty(itemStack) && !isPaperBowlSoupEmpty(itemStack));
    }

    public static boolean isPaperBowlClear(ItemStack itemStack) {
        return isPaperBowlEmpty(itemStack) && isPaperBowlSoupEmpty(itemStack);
    }

    public static List<ItemStack> getPaperBowlItems(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).items());
    }

    public static boolean isPaperBowlItemsClear(ItemStack itemStack) {
        return getDataComponent(itemStack).items().isEmpty();
    }

    public static List<ItemStack> getPaperBowlSkewers(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).skewers());
    }

    public static boolean isPaperBowlSkewersClear(ItemStack itemStack) {
        return getDataComponent(itemStack).skewers().isEmpty();
    }

    public static boolean isPaperBowlSoupEmpty(ItemStack itemStack) {
        return getPaperBowlSoup(itemStack).value() instanceof HotpotEmptySoupType.Factory;
    }

    public static HotpotSoupTypeFactoryHolder<?> getPaperBowlSoup(ItemStack itemStack) {
        return getDataComponent(itemStack).soupTypeFactory();
    }

    public static boolean isPaperBowlDrained(ItemStack itemStack) {
        return getDataComponent(itemStack).drained();
    }

    public static HotpotPaperBowlDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_PAPER_BOWL_DATA_COMPONENT, HotpotPaperBowlDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotPaperBowlDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_PAPER_BOWL_DATA_COMPONENT, dataComponent);
    }

    public static void setPaperBowlItems(ItemStack itemStack, List<ItemStack> items) {
        setDataComponent(itemStack, getDataComponent(itemStack).setItems(items));
    }

    public static void setPaperBowlSkewers(ItemStack itemStack, List<ItemStack> skewers) {
        setDataComponent(itemStack, getDataComponent(itemStack).setSkewers(skewers));
    }

    public static void setPaperBowlSoup(ItemStack itemStack, IHotpotSoupType soupType) {
        setDataComponent(itemStack, getDataComponent(itemStack).setSoupType(soupType));
    }

    public static void setPaperBowlDrained(ItemStack itemStack, boolean drained) {
        setDataComponent(itemStack, getDataComponent(itemStack).setDrained(drained));
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }
}
