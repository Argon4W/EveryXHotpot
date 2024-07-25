package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.items.components.HotpotSkewerDataComponent;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
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

import java.util.*;
import java.util.function.Consumer;

public class HotpotSkewerItem extends Item implements IHotpotItemContainer, IHotpotSpecialHotpotCookingRecipeItem {
    public HotpotSkewerItem() {
        super(new Properties().component(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, new HotpotSkewerDataComponent(List.of())));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ArrayList<ItemStack> itemStacks = new ArrayList<>(getSkewerItems(itemStack));

        if (itemStacks.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        ItemStack firstItemStack = itemStacks.get(0);

        if (firstItemStack.isEmpty()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        if (firstItemStack.has(DataComponents.FOOD) && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }

        addToInventory(player, firstItemStack);
        itemStacks.remove(0);

        setSkewerItems(itemStack, itemStacks);

        if (isSkewerEmpty(itemStack)) {
            itemStack.shrink(1);
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        if (isSkewerEmpty(itemStack)) {
            return super.getMaxStackSize(itemStack);
        }

        return  1;
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

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return itemStack;
        }

        if (foodItemStack.has(DataComponents.FOOD)) {
            itemStacks.set(0, foodItemStack.finishUsingItem(level, livingEntity));
        }

        if (!itemStacks.get(0).has(DataComponents.FOOD)) {
            addToInventory(player, itemStacks.get(0));
            itemStacks.set(0, ItemStack.EMPTY);
        }

        if (itemStacks.get(0).isEmpty()) {
            itemStacks.remove(0);
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

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return UseAnim.NONE;
        }

        if (foodItemStack.has(DataComponents.FOOD)) {
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

        ItemStack foodItemStack = itemStacks.get(0);

        if (foodItemStack.isEmpty()) {
            return 0;
        }

        if (!foodItemStack.has(DataComponents.FOOD)) {
            return 0;
        }

        return (int) (foodItemStack.getUseDuration(livingEntity) * (0.5f / 1.5f));
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
        List<ItemStack> itemStacks = getSkewerItems(itemStack);
        return itemStacks.isEmpty() ? ItemStack.EMPTY : itemStacks.get(0);
    }

    @Override
    public int getCookingTime(ItemStack itemStack, IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        List<HotpotCookingRecipeContent> recipeContents = getSkewerItemRecipes(itemStack, hotpotBlockEntity);

        if (recipeContents.isEmpty()) {
            return -1;
        }

        return getSkewerItemRecipes(itemStack, hotpotBlockEntity).stream()
                .map(recipeContent -> recipeContent.remapCookingTime(soupType, recipeContent.getItemStack(), pos, hotpotBlockEntity))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.naturalOrder())
                .orElse(-1);
    }

    @Override
    public float getExperience(ItemStack itemStack, IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        List<HotpotCookingRecipeContent> skewerItemRecipes = getSkewerItemRecipes(itemStack, hotpotBlockEntity);
        float totalExperience = 0.0f;

        for (HotpotCookingRecipeContent recipeContent : skewerItemRecipes) {
            totalExperience = recipeContent.remapExperience(soupType, recipeContent.getItemStack(), pos, hotpotBlockEntity).orElse(0.0f);
        }

        return totalExperience;
    }

    @Override
    public ItemStack getResult(ItemStack itemStack, IHotpotSoupType soupType, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        List<HotpotCookingRecipeContent> skewerItemRecipes = getSkewerItemRecipes(itemStack, hotpotBlockEntity);
        List<ItemStack> resultItemStacks = new ArrayList<>();

        for (HotpotCookingRecipeContent recipeContent : skewerItemRecipes) {
            Optional<ItemStack> result = recipeContent.remapResult(soupType, recipeContent.getItemStack(), pos, hotpotBlockEntity);
            resultItemStacks.add(result.orElse(recipeContent.getItemStack()));
        }

        setSkewerItems(itemStack, resultItemStacks);

        return itemStack;
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return super.getDescriptionId(itemStack);
        }

        return super.getDescriptionId(itemStack) + ".hotpot";
    }

    public static ItemStack applyToSkewerItemStacks(ItemStack itemStack, Consumer<ItemStack> applier) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SKEWER.get())) {
            return itemStack;
        }

        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return itemStack;
        }

        if (!HotpotTagsHelper.getHotpotTags(itemStack).contains("SkewerItems", Tag.TAG_LIST)) {
            return itemStack;
        }

        List<ItemStack> skewerItems = getSkewerItems(itemStack);

        skewerItems.forEach(applier);
        setSkewerItems(itemStack, skewerItems);

        return itemStack;
    }

    public static void addSkewerItems(ItemStack itemStack, ItemStack added) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SKEWER.get())) {
            return;
        }

        if (added.isEmpty()) {
            return;
        }

        ArrayList<ItemStack> itemStacks = new ArrayList<>(getSkewerItems(itemStack));
        itemStacks.add(added);
        setSkewerItems(itemStack, itemStacks);
    }

    public static void setSkewerItems(ItemStack itemStack, List<ItemStack> itemStacks) {
        itemStack.set(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT, new HotpotSkewerDataComponent(List.copyOf(itemStacks)));
    }

    public static List<ItemStack> getSkewerItems(ItemStack itemStack) {
        if (!itemStack.is(HotpotModEntry.HOTPOT_SKEWER.get())) {
            return List.of();
        }

        if (!itemStack.has(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT)) {
            return List.of();
        }

        return List.copyOf(itemStack.get(HotpotModEntry.HOTPOT_SKEWER_DATA_COMPONENT).itemStacks());
    }

    public static List<HotpotCookingRecipeContent> getSkewerItemRecipes(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        List<ItemStack> skewerItemStacks = getSkewerItems(itemStack);

        if (skewerItemStacks.isEmpty()) {
            return List.of();
        }

        return skewerItemStacks.stream()
                .map(skewerItemStack -> new HotpotCookingRecipeContent(skewerItemStack, hotpotBlockEntity))
                .toList();
    }

    public static void addToInventory(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }

    public static boolean isSkewerEmpty(ItemStack itemStack) {
        return getSkewerItems(itemStack).isEmpty();
    }
}
