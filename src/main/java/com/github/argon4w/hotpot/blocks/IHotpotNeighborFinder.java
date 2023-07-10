package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface IHotpotNeighborFinder {
    default <T> Optional<T> tryFindNeighbor(
            int maximumNeighbors, BlockPosWithLevel pos,
            LinkedList<BlockPosWithLevel> existingNeighbors,
            BiPredicate<IHotpotNeighborFinder, BlockPosWithLevel> when,
            BiFunction<IHotpotNeighborFinder, BlockPosWithLevel, T> function
    ) {
        BlockPosWithLevel[] neighbors = {pos.north(), pos.south(), pos.east(), pos.west()};
        existingNeighbors.add(pos);

        if (when.test(this, pos)) {
            return Optional.of(function.apply(this, pos));
        }

        for (BlockPosWithLevel neighbor : neighbors) {
            if (!existingNeighbors.contains(neighbor)
                    && neighbor.getBlockEntity() instanceof IHotpotNeighborFinder neighborFinder
                    && (existingNeighbors.size() < maximumNeighbors || maximumNeighbors < 0)
            ) {
                Optional<T> result = neighborFinder.tryFindNeighbor(maximumNeighbors, neighbor, existingNeighbors, when, function);

                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    default <T> T tryFindNeighbor(
            int maximumNeighbors, BlockPosWithLevel pos,
            BiPredicate<IHotpotNeighborFinder, BlockPosWithLevel> when,
            BiFunction<IHotpotNeighborFinder, BlockPosWithLevel, T> function,
            BiFunction<IHotpotNeighborFinder, BlockPosWithLevel, T> orElse) {
        Optional<T> result;

        return (result = tryFindNeighbor(
                maximumNeighbors, pos,
                new LinkedList<>(),
                when, function)).isPresent() ? result.get() : orElse.apply(this, pos);
    }
}
