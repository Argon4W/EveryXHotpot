package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.items.components.HotpotNapkinHolderDataComponent;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import net.minecraft.world.item.ItemStack;

public class HotpotNapkinHolderItem extends HotpotPlacementBlockItem<HotpotPlacedNapkinHolder> {
    public HotpotNapkinHolderItem() {
        super(HotpotPlacementSerializers.NAPKIN_HOLDER_SERIALIZER, new Properties().component(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY));
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, HotpotPlacedNapkinHolder placement, ItemStack itemStack) {
        placement.setNapkinHolderItemSlot(itemStack.copyWithCount(1));
    }

    public static HotpotNapkinHolderDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, HotpotNapkinHolderDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotNapkinHolderDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DATA_COMPONENT, dataComponent);
    }

    public static ItemStack getNapkinItemStack(ItemStack itemStack) {
        return getDataComponent(itemStack).itemStack().copy();
    }

    public static ItemStack setNapkinItemStack(ItemStack itemStack, ItemStack napkinItemStack) {
        setDataComponent(itemStack, new HotpotNapkinHolderDataComponent(napkinItemStack));
        return itemStack;
    }
}
