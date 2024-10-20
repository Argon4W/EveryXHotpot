package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.items.components.HotpotStrainerBasketDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedStrainerBasket;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotStrainerBasketItem extends HotpotPlacementBlockItem<HotpotPlacedStrainerBasket> {
    public HotpotStrainerBasketItem() {
        super(HotpotPlacementSerializers.PLACED_STRAINER_BASKET_SERIALIZER, new Properties().stacksTo(1).component(HotpotModEntry.HOTPOT_STRAINER_BASKET_DATA_COMPONENT, HotpotStrainerBasketDataComponent.EMPTY));
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, HotpotPlacedStrainerBasket placement, ItemStack itemStack) {
        placement.setStrainerBasketItemSlot(itemStack.copyWithCount(1));
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    public static boolean isFood(ItemStack itemStack) {
        return itemStack.has(DataComponents.FOOD);
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

    public static boolean isStrainerBasketEmpty(ItemStack itemStack) {
        return getStrainerBasketItems(itemStack).isEmpty();
    }

    public static void addStrainerBasketItems(ItemStack itemStack, ItemStack added) {
        setDataComponent(itemStack, getDataComponent(itemStack).addItemStack(added));
    }
}
