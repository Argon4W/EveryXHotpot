package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotEmptyPlacement implements IHotpotPlacement {
    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {

    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return false;
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<Integer> getPositions() {
        return List.of();
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.EMPTY_PLACEMENT_SERIALIZER;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotEmptyPlacement> {
        @Override
        public HotpotEmptyPlacement get(List<Integer> positions, ComplexDirection direction) {
            return get();
        }

        @Override
        public MapCodec<HotpotEmptyPlacement> getCodec() {
            return MapCodec.unit(this::get);
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of();
        }

        public HotpotEmptyPlacement get() {
            return new HotpotEmptyPlacement();
        }
    }
}
