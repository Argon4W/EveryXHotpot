package com.github.argon4w.hotpot.placements.coords;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.items.IHotpotTablewareInteraction;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class HotpotPlacementCoords {

    protected final Optional<IHotpotPlacementContainer> container;
    protected final LevelBlockPos blockPos;

    public HotpotPlacementCoords(LevelBlockPos blockPos) {
        this.blockPos = blockPos;
        this.container = blockPos.getBlockEntity() instanceof IHotpotPlacementContainer container1
                ? Optional.of(container1)
                : Optional.empty();
    }

    public void interact(
            int hitPos,
            int layer,
            Player player,
            InteractionHand hand,
            ItemStack itemStack) {
        container.ifPresent(container1 -> container1.interact(new IHotpotTablewareInteraction.Context(
                hitPos,
                layer,
                player,
                hand,
                blockPos
        ), itemStack));
    }

    public static List<Integer> getNearbyOccupiedPositions(LevelBlockPos blockPos, int layer) {
        return getNearbyCoords(blockPos)
                .map(relative -> relative.getRelativeOccupiedPositions(layer))
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Integer> getOccupiedPositions(int layer) {
        return blockPos.isAir()
                ? List.of()
                : container.map(container1 -> container1.getOccupiedPositions(layer, blockPos)).orElse(List.of(5, 9, 6, 10));
    }

    public static Stream<Relative> getNearbyCoords(LevelBlockPos blockPos) {
        return Arrays
                .stream(ComplexDirection.values())
                .map(direction -> new HotpotPlacementCoords.Relative(blockPos, direction));
    }

    public static void interactNearbyPositions(
            LevelBlockPos blockPos,
            Player player,
            InteractionHand hand,
            ItemStack itemStack,
            int position,
            int layer) {
        HotpotPlacementCoords.getNearbyCoords(blockPos)
                .filter(relative -> relative.hasRelativePosition(position, layer))
                .findFirst()
                .ifPresent(relative -> relative.interact(position, layer, player, hand, itemStack));
    }

    public static class Relative extends HotpotPlacementCoords {

        private final ComplexDirection direction;

        public Relative(LevelBlockPos blockPos, ComplexDirection direction) {
            super(blockPos.relative(direction));
            this.direction = direction;
        }

        public List<Integer> getRelativeOccupiedPositions(int layer) {
            return super.getOccupiedPositions(layer)
                    .stream()
                    .map(i -> direction.getOpposite().relativeToCoords(i))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

        public boolean hasRelativePosition(int position, int layer) {
            return direction
                    .relativeToCoords(position)
                    .map(getOccupiedPositions(layer)::contains)
                    .orElse(false);
        }

        @Override
        public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack) {
            direction
                    .relativeToCoords(hitPos)
                    .ifPresent(i -> super.interact(i, layer, player, hand, itemStack));
        }
    }
}
