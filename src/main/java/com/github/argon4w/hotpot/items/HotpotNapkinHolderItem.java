package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotNapkinHolderDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.SimpleItemSlot;
import net.minecraft.world.item.ItemStack;

public class HotpotNapkinHolderItem extends HotpotPlacementBlockItem<HotpotPlacedNapkinHolder> {
    public HotpotNapkinHolderItem() {
        super(HotpotPlacements.NAPKIN_HOLDER, new Properties().component(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY));
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, HotpotPlacedNapkinHolder placement, ItemStack itemStack) {
        placement.setNapkinHolderItemSlot(itemStack.copyWithCount(1));
    }

    public static HotpotNapkinHolderDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY);
    }

    public static HotpotNapkinHolderDataComponent getDataComponent(SimpleItemSlot slot) {
        return getDataComponent(slot.getItemStack());
    }

    public static void setDataComponent(ItemStack itemStack, HotpotNapkinHolderDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, dataComponent);
    }

    public static void setDataComponent(SimpleItemSlot slot, HotpotNapkinHolderDataComponent dataComponent) {
        setDataComponent(slot.getItemStack(), dataComponent);
    }

    public static boolean isNapkinHolderEmpty(SimpleItemSlot slot) {
        return getNapkinHolderItemSlot(slot).isEmpty();
    }

    public static boolean isNapkinHolderEmpty(ItemStack itemStack) {
        return getNapkinHolderItemSlot(itemStack).isEmpty();
    }

    public static void addNapkinHolderItemStack(SimpleItemSlot slot, ItemStack added) {
        setDataComponent(slot, getDataComponent(slot).addItemStack(added));
    }

    public static void shrinkNapkinHolderItemStack(SimpleItemSlot slot) {
        setDataComponent(slot, getDataComponent(slot).shrinkItemStack());
    }

    public static void dropNapkinHolderItemStack(SimpleItemSlot slot, LevelBlockPos pos) {
        setDataComponent(slot, getDataComponent(slot).dropItemStack(pos));
    }

    public static SimpleItemSlot getNapkinHolderItemSlot(SimpleItemSlot slot) {
        return getDataComponent(slot.getItemStack()).napkinItemSlot().copy();
    }

    public static SimpleItemSlot getNapkinHolderItemSlot(ItemStack itemStack) {
        return getDataComponent(itemStack).napkinItemSlot().copy();
    }
}
