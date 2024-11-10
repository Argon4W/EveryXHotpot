package com.github.argon4w.hotpot.items;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.fancytoys.ItemUtils;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.api.items.IHotpotItemContainer;
import com.github.argon4w.hotpot.items.components.HotpotStrainerBasketDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedStrainerBasket;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HotpotStrainerBasketItem extends HotpotPlacementBlockItem<HotpotPlacedStrainerBasket> implements IHotpotItemContainer {

    public HotpotStrainerBasketItem() {
        super(
                HotpotPlacementSerializers.PLACED_STRAINER_BASKET_SERIALIZER,
                new Properties().stacksTo(1).component(HotpotModEntry.HOTPOT_STRAINER_BASKET_DATA_COMPONENT, HotpotStrainerBasketDataComponent.EMPTY));
    }

    @Override
    public void loadPlacement(
            IHotpotPlacementContainer container,
            LevelBlockPos pos,
            HotpotPlacedStrainerBasket placement,
            ItemStack itemStack) {
        placement.setStrainerBasketItemSlot(itemStack.copyWithCount(1));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @NotNull @Override
    public InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            Player player,
            @NotNull InteractionHand usedHand) {
        ItemStack mainHandItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
        LevelBlockPos pos = LevelBlockPos.fromVec3(level, player.position());

        if (isStrainerBasketEmpty(mainHandItemStack)) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        if (offHandItemStack.isEmpty()) {
            takeStrainerBasketItems(mainHandItemStack, player);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        if (!offHandItemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL)) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        if (HotpotPaperBowlItem.isPaperBowlUsed(offHandItemStack)) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        if (HotpotPaperBowlItem.isPaperBowlFull(offHandItemStack)) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        ItemStack paperBowlItemStack = offHandItemStack.split(1);
        ArrayList<ItemStack> strainerContents = new ArrayList<>(getStrainerBasketItems(mainHandItemStack));
        ArrayList<ItemStack> contents = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlItems(paperBowlItemStack));
        ArrayList<ItemStack> skewers = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlSkewers(paperBowlItemStack));

        while (!strainerContents.isEmpty() && ((contents.size() + skewers.size()) < 8)) {
            ItemStack itemStack = strainerContents.removeFirst().copy();

            if (itemStack.isEmpty()) {
                continue;
            }

            if (!itemStack.getItem().canFitInsideContainerItems()) {
                pos.dropItemStack(itemStack);
                continue;
            }

            if (itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK)) {
                pos.dropItemStack(itemStack);
                continue;
            }

            if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK)) {
                pos.dropItemStack(itemStack);
                continue;
            }

            if (itemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL)) {
                pos.dropItemStack(itemStack);
                continue;
            }

            if (itemStack.is(HotpotModEntry.HOTPOT_STRAINER_BASKET)) {
                pos.dropItemStack(itemStack);
                continue;
            }

            if (itemStack.getItem() instanceof HotpotSkewerItem) {
                skewers.add(itemStack);
                continue;
            }

            contents.add(itemStack);
        }

        setStrainerBasketItems(mainHandItemStack, strainerContents);
        HotpotPaperBowlItem.setPaperBowlItems(paperBowlItemStack, contents);
        HotpotPaperBowlItem.setPaperBowlSkewers(paperBowlItemStack, skewers);
        ItemUtils.addToInventory(player, paperBowlItemStack);

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @NotNull @Override
    public String getDescriptionId(@NotNull ItemStack itemStack) {
        if (isStrainerBasketEmpty(itemStack)) {
            return super.getDescriptionId(itemStack) + ".empty";
        }

        if (isStrainerBasketFull(itemStack)) {
            return super.getDescriptionId(itemStack) + ".full";
        }

        return super.getDescriptionId();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack itemStack,
            @NotNull TooltipContext context,
            List<Component> components,
            @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("item.everyxhotpot.hotpot_strainer_basket.cooking_speed", ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(getStrainerBasketCookingSpeed(itemStack))).withStyle(ChatFormatting.BLUE));
    }

    @Override
    public ItemStack getContainedItemStack(ItemStack itemStack) {
        return isStrainerBasketEmpty(itemStack) ? ItemStack.EMPTY : getStrainerBasketItems(itemStack).getFirst();
    }

    @Override
    public List<ItemStack> getAllContainedItemStacks(ItemStack itemStack) {
        return getStrainerBasketItems(itemStack);
    }

    public static ItemStack createStrainerBasketFromItems(List<ItemStack> itemStacks) {
        return Util.make(HotpotModEntry.HOTPOT_STRAINER_BASKET.toStack(), itemStack -> setDataComponent(itemStack, getDataComponent(itemStack).setItemStacks(itemStacks)));
    }

    public static HotpotStrainerBasketDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_STRAINER_BASKET_DATA_COMPONENT, HotpotStrainerBasketDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotStrainerBasketDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_STRAINER_BASKET_DATA_COMPONENT, dataComponent);
    }

    public static List<ItemStack> getStrainerBasketItems(ItemStack itemStack) {
        return List.copyOf(getDataComponent(itemStack).itemStacks());
    }

    public static int getStrainerBasketItemSize(ItemStack itemStack) {
        return getDataComponent(itemStack).itemStacks().size();
    }

    public static void setStrainerBasketItems(ItemStack itemStack, List<ItemStack> itemStacks) {
        setDataComponent(itemStack, getDataComponent(itemStack).setItemStacks(itemStacks));
    }

    public static boolean isStrainerBasketEmpty(ItemStack itemStack) {
        return getDataComponent(itemStack).itemStacks().isEmpty();
    }

    public static boolean isStrainerBasketFull(ItemStack itemStack) {
        return getStrainerBasketItemSize(itemStack) >= 8;
    }

    public static void addStrainerBasketItems(ItemStack itemStack, ItemStack added) {
        setDataComponent(itemStack, getDataComponent(itemStack).addItemStack(added));
    }

    public static void takeStrainerBasketItems(ItemStack itemStack, Player player) {
        setStrainerBasketItems(itemStack, Util.make(new ArrayList<>(getStrainerBasketItems(itemStack)), itemStacks -> ItemUtils.addToInventory(player, itemStacks.removeFirst())));
    }

    public static double getStrainerBasketCookingSpeed(ItemStack itemStack) {
        return getStrainerBasketCookingSpeed(getStrainerBasketItemSize(itemStack));
    }

    public static double getStrainerBasketCookingSpeed(long strainerContentSize) {
        return strainerContentSize > 4 ? Mth.lerp((strainerContentSize - 4.0) / 4.0, 1, 1.0 / 8.0) : 1.0;
    }
}
