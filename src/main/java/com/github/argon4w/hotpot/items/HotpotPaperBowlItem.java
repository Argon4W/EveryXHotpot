package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotPaperBowlItem extends HotpotPlacementBlockItem implements IHotpotItemContainer {
    public HotpotPaperBowlItem() {
        super(() -> HotpotPlacements.PLACED_PAPER_BOWL.get().build());
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placement, ItemStack itemStack) {
        if (placement instanceof HotpotPlacedPaperBowl placedPaperBowl) {
            placedPaperBowl.setPaperBowlItemSlot(itemStack);
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ArrayList<ItemStack> itemStacks = new ArrayList<>(getPaperBowlItems(itemStack));
        boolean skewer = false;

        if (itemStacks.isEmpty()) {
            skewer = true;
            itemStacks = new ArrayList<>(getPaperBowlSkewers(itemStack));
        }

        if (itemStacks.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        ItemStack firstItemStack = itemStacks.get(0);

        if (firstItemStack.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (canEatInBowl(firstItemStack) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        addToInventory(player, firstItemStack);
        itemStacks.remove(0);

        if (skewer) {
            setPaperBowlSkewers(itemStack, itemStacks);
        } else {
            setPaperBowlItems(itemStack, itemStacks);
        }

        if (isBowlEmpty(itemStack)) {
            itemStack.shrink(1);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }


        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        if (isBowlClear(itemStack)) {
            return super.getMaxStackSize(itemStack);
        }

        return  1;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>(getPaperBowlItems(itemStack));
        boolean skewer = false;

        if (!(livingEntity instanceof Player player)) {
            return itemStack;
        }

        if (itemStacks.isEmpty()) {
            skewer = true;
            itemStacks = new ArrayList<>(getPaperBowlSkewers(itemStack));
        }

        if (itemStacks.isEmpty()) {
            return itemStack;
        }

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return itemStack;
        }

        if (canEatInBowl(foodItemStack)) {
            itemStacks.set(0, foodItemStack.finishUsingItem(level, livingEntity));
        }

        if (!canEatInBowl(itemStacks.get(0))) {
            addToInventory(player, itemStacks.get(0));
            itemStacks.set(0, ItemStack.EMPTY);
        }

        if (itemStacks.get(0).isEmpty()) {
            itemStacks.remove(0);
        }

        if (skewer) {
            setPaperBowlSkewers(itemStack, itemStacks);
        } else {
            setPaperBowlItems(itemStack, itemStacks);
        }

        if (isBowlEmpty(itemStack)) {
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

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (canEatInBowl(foodItemStack)) {
            return foodItemStack.getUseAnimation();
        }

        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        List<ItemStack> itemStacks = getPaperBowlItems(itemStack);

        if (itemStacks.isEmpty()) {
            itemStacks = new ArrayList<>(getPaperBowlSkewers(itemStack));
        }

        if (itemStacks.isEmpty()) {
            return 0;
        }

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return 0;
        }

        if (!canEatInBowl(foodItemStack)) {
            return 0;
        }

        if (isPaperBowlDrained(itemStack)) {
            return foodItemStack.getUseDuration();
        }

        return (int) (foodItemStack.getUseDuration() * 1.5f);
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
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        List<ItemStack> itemStacks = getPaperBowlItems(itemStack);

        if (itemStacks.isEmpty()) {
            itemStacks = getPaperBowlSkewers(itemStack);
        }

        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack containedItemStack = itemStacks.get(0);

        if (containedItemStack.getItem() instanceof IHotpotItemContainer itemContainer) {
            return itemContainer.getContainedItemStack(containedItemStack);
        }

        return containedItemStack;
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return super.getDescriptionId(itemStack);
        }

        if (HotpotTagsHelper.getHotpotTags(itemStack).contains("BowlSkewers")) {
            return super.getDescriptionId(itemStack) + ".skewer";
        }

        return super.getDescriptionId(itemStack) + ".hotpot";
    }

    public static void setPaperBowlItems(ItemStack itemStack, List<ItemStack> items) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "BowlItems", items.stream()
                .filter(item -> !item.isEmpty())
                .map(HotpotTagsHelper::saveItemStack)
                .collect(Collectors.toCollection(ListTag::new)));
    }

    public static void setPaperBowlSkewers(ItemStack itemStack, List<ItemStack> itemStacks) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "BowlSkewers", itemStacks.stream()
                .filter(item -> !item.isEmpty())
                .map(HotpotTagsHelper::saveItemStack)
                .collect(Collectors.toCollection(ListTag::new)));
    }

    public static void setPaperBowlSoup(ItemStack itemStack, IHotpotSoupType soupType) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "BowlSoup", StringTag.valueOf(soupType.getResourceLocation().toString()));
    }

    public static void setPaperBowlDrained(ItemStack itemStack, boolean drained) {
        HotpotTagsHelper.updateHotpotTags(itemStack, "BowlSoupDrained", ByteTag.valueOf(drained));
    }

    public static List<ItemStack> getPaperBowlItems(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL.get())) {
            return List.of();
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return List.of();
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("BowlItems", Tag.TAG_LIST)) {
            return List.of();
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getList("BowlItems", Tag.TAG_COMPOUND)
                .stream()
                .map(tag -> ItemStack.of((CompoundTag) tag))
                .filter(item -> !item.isEmpty())
                .toList();
    }

    public static List<ItemStack> getPaperBowlSkewers(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL.get())) {
            return List.of();
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return List.of();
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("BowlSkewers", Tag.TAG_LIST)) {
            return List.of();
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getList("BowlSkewers", Tag.TAG_COMPOUND)
                .stream()
                .map(tag -> ItemStack.of((CompoundTag) tag))
                .filter(item -> !item.isEmpty())
                .toList();
    }

    public static Optional<ResourceLocation> getPaperBowlSoup(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL.get())) {
            return Optional.empty();
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return Optional.empty();
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("BowlSoup", Tag.TAG_STRING)) {
            return Optional.empty();
        }

        String bowlSoup = HotpotTagsHelper.getHotpotTags(itemStack).getString("BowlSoup");

        if (!ResourceLocation.isValidResourceLocation(bowlSoup)) {
            return Optional.empty();
        }

        return Optional.of(new ResourceLocation(bowlSoup));
    }

    public static boolean isPaperBowlDrained(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL.get())) {
            return false;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return false;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("BowlSoupDrained", Tag.TAG_ANY_NUMERIC)) {
            return false;
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getBoolean("BowlSoupDrained");
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }

    public static boolean canEatInBowl(ItemStack itemStack) {
        return itemStack.isEdible() || itemStack.getItem() instanceof HotpotSkewerItem;
    }

    public static boolean isBowlEmpty(ItemStack itemStack) {
        return (getPaperBowlItems(itemStack).size() + getPaperBowlSkewers(itemStack).size()) == 0;
    }

    public static boolean isBowlClear(ItemStack itemStack) {
        return isBowlEmpty(itemStack) && getPaperBowlSoup(itemStack).isEmpty();
    }
}
