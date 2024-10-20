package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.items.components.HotpotNapkinHolderDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HotpotNapkinHolderItem extends HotpotPlacementBlockItem<HotpotPlacedNapkinHolder> {
    public HotpotNapkinHolderItem() {
        super(HotpotPlacementSerializers.NAPKIN_HOLDER_SERIALIZER, new Properties().component(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY));
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, HotpotPlacedNapkinHolder placement, ItemStack itemStack) {
        placement.setNapkinHolderItemSlot(itemStack.copyWithCount(1));
    }

    public static HotpotNapkinHolderDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotNapkinHolderDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, dataComponent);
    }

    public static SimpleItemSlot getNapkinItemSlot(ItemStack itemStack) {
        return getDataComponent(itemStack).itemSlot().copy();
    }

    public static void shrinkNapkinItemSlot(ItemStack itemStack, boolean consume) {
        setDataComponent(itemStack, getDataComponent(itemStack).shrinkNapkinItemSlot(consume));
    }

    public static void addNapkinItemSlot(ItemStack itemStack, ItemStack napkinItemStack) {
        setDataComponent(itemStack, getDataComponent(itemStack).addNapkinItemSlot(napkinItemStack));
    }

    public static void dropNapkinItemSlot(ItemStack itemStack, LevelBlockPos pos) {
        setDataComponent(itemStack, getDataComponent(itemStack).dropNapkinItemSlot(pos));
    }

    public static boolean isNapkinHolderEmpty(ItemStack itemStack) {
        return getDataComponent(itemStack).itemSlot().isEmpty();
    }

    public static boolean isNapkinItemSlotPaper(ItemStack itemStack) {
        return getDataComponent(itemStack).itemSlot().getItemStack().is(Items.PAPER);
    }
}
