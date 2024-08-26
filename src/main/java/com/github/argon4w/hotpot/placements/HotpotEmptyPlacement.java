package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotEmptyPlacement implements IHotpotPlacement {
    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {

    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        return false;
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<Integer> getPosList() {
        return List.of();
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.EMPTY_PLACEMENT_SERIALIZER;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotEmptyPlacement> {
        @Override
        public HotpotEmptyPlacement get(int pos, Direction direction) {
            return get();
        }

        @Override
        public MapCodec<HotpotEmptyPlacement> getCodec() {
            return MapCodec.unit(this::get);
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return false;
        }

        public HotpotEmptyPlacement get() {
            return new HotpotEmptyPlacement();
        }
    }
}
