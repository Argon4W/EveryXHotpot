package com.github.argon4w.hotpot.placements.coords;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HotpotPlacementPositions {
    /*
    0, 4, 8,  12
    1, 5, 9,  13
    2, 6, 10, 14
    3, 7 ,11, 15
    */
    
    public static final Map<ComplexDirection, Function<Integer, Optional<Integer>>> DIRECTION_TO_POS_MAP = new HashMap<>(Map.of(
            ComplexDirection.IDENTITY, Util.memoize(Optional::of),
            ComplexDirection.NORTH, Util.memoize(i -> isNorthLimit(i) ? Optional.empty() : Optional.of(i - 1)),
            ComplexDirection.SOUTH, Util.memoize(i -> isSouthLimit(i) ? Optional.empty() : Optional.of(i + 1)),
            ComplexDirection.EAST, Util.memoize(i -> isEastLimit(i) ? Optional.empty() : Optional.of(i + 4)),
            ComplexDirection.WEST, Util.memoize(i -> isWestLimit(i) ? Optional.empty() : Optional.of(i - 4)),
            ComplexDirection.NORTH_EAST, Util.memoize(i -> isNorthLimit(i) || isEastLimit(i) ? Optional.empty() : Optional.of(i - 1 + 4)),
            ComplexDirection.SOUTH_EAST, Util.memoize(i -> isSouthLimit(i) || isEastLimit(i) ? Optional.empty() : Optional.of(i + 1 + 4)),
            ComplexDirection.NORTH_WEST, Util.memoize(i -> isNorthLimit(i) || isWestLimit(i) ? Optional.empty() : Optional.of(i - 1 - 4)),
            ComplexDirection.SOUTH_WEST, Util.memoize(i -> isSouthLimit(i) || isWestLimit(i) ? Optional.empty() : Optional.of(i + 1 - 4))
    ));

    public static Map<ComplexDirection, Function<Integer, Optional<Integer>>> DIRECTION_TO_COORDS_MAP = new HashMap<>(Map.of(
            ComplexDirection.IDENTITY, Util.memoize(Optional::of),
            ComplexDirection.NORTH, Util.memoize(i -> isNorthCoordsLimit(i) ? Optional.empty() : Optional.of(i + 2)),
            ComplexDirection.SOUTH, Util.memoize(i -> isSouthCoordsLimit(i) ? Optional.empty() : Optional.of(i - 2)),
            ComplexDirection.EAST, Util.memoize(i -> isEastCoordsLimit(i) ? Optional.empty() : Optional.of( i - 8)),
            ComplexDirection.WEST, Util.memoize(i -> isWestCoordsLimit(i) ? Optional.empty() : Optional.of( i + 8)),
            ComplexDirection.NORTH_EAST, Util.memoize(i -> isNorthCoordsLimit(i) || isEastCoordsLimit(i) ? Optional.empty() : Optional.of(i + 2 - 8)),
            ComplexDirection.SOUTH_EAST, Util.memoize(i -> isSouthCoordsLimit(i) || isEastCoordsLimit(i) ? Optional.empty() : Optional.of(i - 2 - 8)),
            ComplexDirection.NORTH_WEST, Util.memoize(i -> isNorthCoordsLimit(i) || isWestCoordsLimit(i) ? Optional.empty() : Optional.of(i + 2 + 8)),
            ComplexDirection.SOUTH_WEST, Util.memoize(i -> isSouthCoordsLimit(i) || isWestCoordsLimit(i) ? Optional.empty() : Optional.of(i - 2 + 8))
    ));
    
    public static final Map<Integer, ComplexDirection> POS_TO_DIRECTION_MAP = new HashMap<>(Map.of(
            0, ComplexDirection.IDENTITY,
            1, ComplexDirection.NORTH,
            -1, ComplexDirection.SOUTH,
            -4, ComplexDirection.EAST,
            4, ComplexDirection.WEST
    ));

    public static Optional<Integer> relativeCoords(int position, ComplexDirection direction) {
        return DIRECTION_TO_COORDS_MAP.get(direction).apply(position);
    }

    public static Optional<Integer> relative(int position, ComplexDirection direction) {
        return DIRECTION_TO_POS_MAP.get(direction).apply(position);
    }

    public static ComplexDirection directionBetween(int position1, int position2) {
        return POS_TO_DIRECTION_MAP.getOrDefault(position1 - position2, ComplexDirection.NORTH);
    }

    public static boolean isNorthCoordsLimit(int position) {
        return (position & 0b0011) >= 0b0010;
    }

    public static boolean isSouthCoordsLimit(int position) {
        return (position & 0b0011) < 0b0010;
    }

    public static boolean isEastCoordsLimit(int position) {
        return (position & 0b1100) < 0b1000;
    }

    public static boolean isWestCoordsLimit(int position) {
        return (position & 0b1100) >= 0b1000;
    }
    
    public static boolean isNorthLimit(int position) {
        return (position & 0b0011) == 0b00;
    }
    
    public static boolean isSouthLimit(int position) {
        return (position & 0b0011) == 0b11;
    }
    
    public static boolean isEastLimit(int position) {
        return (position & 0b1100) == 0b1100;
    }
    
    public static boolean isWestLimit(int position) {
        return (position & 0b1100) == 0b0000;
    }

    public static int getXComponent(int position) {
        return (position & 0b1100) >>> 2;
    }

    public static int getYComponent(int position) {
        return (position & 0b0011);
    }

    public static double getRenderX(int position) {
        return getXComponent(position) * 0.5 -0.5;
    }

    public static double getRenderZ(int position) {
        return getYComponent(position) * 0.5 -0.5;
    }

    public static double getRenderCenterX(int position) {
        return getRenderX(position) + 0.25;
    }

    public static double getRenderCenterZ(int position) {
        return getRenderZ(position) + 0.25;
    }

    public static int getPosition(BlockPos blockPos, Vec3 location) {
        return getPosition(location.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public static int getPosition(Vec3 vec) {
        return (vec.z() < 0.5 ? 0b01 : 0b10) | (vec.x() < 0.5 ? 0b0100 : 0b1000);
    }
}
