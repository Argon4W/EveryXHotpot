package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.api.items.IHotpotItemContainer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotPaperBowlDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.HotpotSoupStatus;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
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
        super(HotpotPlacementSerializers.PLACED_PAPER_BOWL_SERIALIZER, new Properties().stacksTo(64).component(HotpotModEntry.HOTPOT_PAPER_BOWL_DATA_COMPONENT, HotpotPaperBowlDataComponent.EMPTY));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, HotpotPlacedPaperBowl placement, ItemStack itemStack) {
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

        if (isPaperBowlEmpty(itemStack)) {
            itemStack.shrink(1);
            return InteractionResultHolder.success(itemStack);
        }

        ArrayList<ItemStack> items = new ArrayList<>(getPaperBowlItems(itemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(getPaperBowlSkewers(itemStack));

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

        if (itemStacks.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        ItemStack firstItemStack = itemStacks.getFirst().copy();

        if (canEatInPaperBowl(firstItemStack) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        HotpotItemUtils.addToInventory(player, firstItemStack);
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

        ItemStack firstItemStack = itemStacks.getFirst().copy();

        if (canEatInPaperBowl(firstItemStack)) {
            itemStacks.set(0, firstItemStack.finishUsingItem(level, player));
        }

        firstItemStack = itemStacks.getFirst().copy();

        if (!canEatInPaperBowl(firstItemStack)) {
            HotpotItemUtils.addToInventory(player, firstItemStack);
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
        ArrayList<ItemStack> items = new ArrayList<>(getPaperBowlItems(itemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(getPaperBowlSkewers(itemStack));

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

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
        ArrayList<ItemStack> items = new ArrayList<>(getPaperBowlItems(itemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(getPaperBowlSkewers(itemStack));

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

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

        return (int) (foodItemStack.getUseDuration(livingEntity) * getPaperBowlSoupStatus(itemStack).getUseDurationFactor());
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        List<ItemStack> items = getPaperBowlItems(itemStack);
        List<ItemStack> skewers = getPaperBowlSkewers(itemStack);

        List<ItemStack> itemStacks = skewers.isEmpty() ? items : skewers;

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack containedItemStack = itemStacks.getFirst();

        if (containedItemStack.getItem() instanceof IHotpotItemContainer container) {
            return container.getContainedItemStack(containedItemStack);
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

        return super.getDescriptionId(itemStack) + getPaperBowlSoupStatus(itemStack).getSuffix();
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
        return getPaperBowlSoupTypeKey(itemStack).equals(HotpotComponentSoupType.EMPTY_SOUP_TYPE_KEY);
    }

    public static boolean isPaperBowlSameSoup(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        return hotpotBlockEntity.getSoup().soupTypeHolder().is(getPaperBowlSoupTypeKey(itemStack));
    }

    public static ResourceKey<HotpotComponentSoupType> getPaperBowlSoupTypeKey(ItemStack itemStack) {
        return getDataComponent(itemStack).soupTypeKey();
    }

    public static HotpotSoupStatus getPaperBowlSoupStatus(ItemStack itemStack) {
        return getDataComponent(itemStack).soupStatus();
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

    public static void setPaperBowlSoupType(ItemStack itemStack, HotpotComponentSoup soup) {
        setDataComponent(itemStack, getDataComponent(itemStack).setSoupType(soup));
    }

    public static void setPaperBowlSoupStatus(ItemStack itemStack, HotpotSoupStatus soupStatus) {
        setDataComponent(itemStack, getDataComponent(itemStack).setSoupStatus(soupStatus));
    }
}
