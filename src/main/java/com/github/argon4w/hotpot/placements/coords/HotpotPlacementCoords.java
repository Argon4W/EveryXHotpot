package com.github.argon4w.hotpot.placements.coords;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotPlacementCoords {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<IHotpotPlacementContainer> placementContainer;
    protected final LevelBlockPos blockPos;

    public HotpotPlacementCoords(LevelBlockPos blockPos) {
        this.blockPos = blockPos;
        this.placementContainer = blockPos.getBlockEntity() instanceof IHotpotPlacementContainer container ? Optional.of(container) : Optional.empty();
    }

    public void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack) {
        placementContainer.ifPresent(placementContainer -> placementContainer.setContentByInteraction(hitPos, layer, player, hand, itemStack, blockPos));
    }

    public ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer) {
        return placementContainer.map(placementContainer -> placementContainer.getContentByTableware(player, hand, hitPos, layer, blockPos)).orElse(ItemStack.EMPTY);
    }

    public List<Integer> getOccupiedPositions(int layer) {
        return blockPos.isAir() ? List.of() : placementContainer.map(placementContainer -> placementContainer.getOccupiedPositions(layer)).orElse(List.of(5, 9, 6, 10));
    }

    public static class Relative extends HotpotPlacementCoords {
        private final ComplexDirection direction;

        public Relative(LevelBlockPos blockPos, ComplexDirection direction) {
            super(blockPos.relative(direction));
            this.direction = direction;
        }

        public List<Integer> getRelativeOccupiedPositions(int layer) {
            return super.getOccupiedPositions(layer).stream().map(i -> direction.getOpposite().relativeToCoords(i)).filter(Optional::isPresent).map(Optional::get).toList();
        }

        public boolean hasRelativePosition(int position, int layer) {
            return direction.relativeToCoords(position).map(getOccupiedPositions(layer)::contains).orElse(false);
        }

        @Override
        public void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack) {
            direction.relativeToCoords(hitPos).ifPresent(i -> super.setContentByInteraction(i, layer, player, hand, itemStack));
        }

        @Override
        public ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer) {
            return direction.relativeToCoords(hitPos).map(i -> super.getContentByTableware(player, hand, i, layer)).orElse(ItemStack.EMPTY);
        }

        public ComplexDirection getDirection() {
            return direction;
        }
    }
}
