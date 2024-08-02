package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.placements.IHotpotPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacementFactory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class HotpotPlateItem<T extends IHotpotPlate> extends HotpotPlacementBlockItem<T> {
    public HotpotPlateItem(DeferredHolder<IHotpotPlacementFactory<?>, ? extends IHotpotPlacementFactory<T>> holder) {
        super(holder);
    }

    public HotpotPlateItem(DeferredHolder<IHotpotPlacementFactory<?>, ? extends IHotpotPlacementFactory<T>> holder, Item.Properties properties) {
        super(holder, properties);
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos, T placement, ItemStack itemStack) {
        placement.setPlateItemSlot(itemStack.copyWithCount(1));
    }
}
