package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotEmptyPlacement implements IHotpotPlacement {
    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_placement");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of();
    }

    @Override
    public boolean isConflict(int pos) {
        return false;
    }

    @Override
    public IHotpotPlacementFactory<?> getFactory() {
        return HotpotPlacements.getEmptyPlacementFactory();
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotEmptyPlacement> {
        @Override
        public HotpotEmptyPlacement buildFromSlots(int pos, Direction direction) {
            return build();
        }

        @Override
        public MapCodec<HotpotEmptyPlacement> buildFromCodec() {
            return MapCodec.unit(this::build);
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return false;
        }

        public HotpotEmptyPlacement build() {
            return new HotpotEmptyPlacement();
        }
    }
}
