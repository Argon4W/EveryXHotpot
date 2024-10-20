package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.api.placements.IHotpotCommonPlacement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class IHotpotCommonPlacementBlockItem<T extends IHotpotCommonPlacement> extends HotpotPlacementBlockItem<T> {
    public IHotpotCommonPlacementBlockItem(DeferredHolder<IHotpotPlacementSerializer<?>, ? extends IHotpotPlacementSerializer<T>> holder) {
        super(holder);
    }

    public IHotpotCommonPlacementBlockItem(DeferredHolder<IHotpotPlacementSerializer<?>, ? extends IHotpotPlacementSerializer<T>> holder, Item.Properties properties) {
        super(holder, properties);
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, T placement, ItemStack itemStack) {
        placement.setCommonItemSlot(itemStack.copyWithCount(1));
    }
}
