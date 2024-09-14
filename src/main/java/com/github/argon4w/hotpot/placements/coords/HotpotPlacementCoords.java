package com.github.argon4w.hotpot.placements.coords;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class HotpotPlacementCoords {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<IHotpotPlacementContainer> placementContainer;
    protected final LevelBlockPos blockPos;

    public HotpotPlacementCoords(LevelBlockPos blockPos) {
        this.blockPos = blockPos;
        this.placementContainer = blockPos.getBlockEntity() instanceof IHotpotPlacementContainer container ? Optional.of(container) : Optional.empty();
    }

    public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack) {
        placementContainer.ifPresent(placementContainer -> placementContainer.interact(hitPos, layer, player, hand, itemStack, blockPos));
    }

    public List<Integer> getOccupiedPositions(int layer, LevelBlockPos blockPos) {
        return blockPos.isAir() ? List.of() : placementContainer.map(placementContainer -> placementContainer.getOccupiedPositions(layer, blockPos)).orElse(List.of(5, 9, 6, 10));
    }

    public static Stream<Relative> getNearbyCoords(LevelBlockPos blockPos) {
        return Arrays.stream(ComplexDirection.values()).map(direction -> new HotpotPlacementCoords.Relative(blockPos, direction));
    }

    public static List<Integer> getNearbyOccupiedPositions(LevelBlockPos blockPos, int layer) {
        return getNearbyCoords(blockPos).map(relative -> relative.getRelativeOccupiedPositions(layer, blockPos)).flatMap(Collection::stream).toList();
    }

    public static void interactNearbyPositions(LevelBlockPos blockPos, Player player, InteractionHand hand, ItemStack itemStack, int position, int layer) {
        HotpotPlacementCoords.getNearbyCoords(blockPos).filter(relative -> relative.hasRelativePosition(position, layer, blockPos)).findFirst().ifPresent(relative -> relative.interact(position, layer, player, hand, itemStack));
    }

    public static class Relative extends HotpotPlacementCoords {
        private final ComplexDirection direction;

        public Relative(LevelBlockPos blockPos, ComplexDirection direction) {
            super(blockPos.relative(direction));
            this.direction = direction;
        }

        public List<Integer> getRelativeOccupiedPositions(int layer, LevelBlockPos blockPos) {
            return super.getOccupiedPositions(layer, blockPos).stream().map(i -> direction.getOpposite().relativeToCoords(i)).filter(Optional::isPresent).map(Optional::get).toList();
        }

        public boolean hasRelativePosition(int position, int layer, LevelBlockPos blockPos) {
            return direction.relativeToCoords(position).map(getOccupiedPositions(layer, blockPos)::contains).orElse(false);
        }

        @Override
        public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack) {
            direction.relativeToCoords(hitPos).ifPresent(i -> super.interact(i, layer, player, hand, itemStack));
        }

        public ComplexDirection getDirection() {
            return direction;
        }
    }
}
