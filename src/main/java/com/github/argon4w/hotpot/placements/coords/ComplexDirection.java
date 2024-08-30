package com.github.argon4w.hotpot.placements.coords;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public enum ComplexDirection implements StringRepresentable {
    IDENTITY("identity", 0),
    NORTH("north", 1, Direction.NORTH),
    SOUTH("south", 2, Direction.SOUTH),
    EAST("east", 3, Direction.EAST),
    WEST("west", 4, Direction.WEST),
    NORTH_EAST("north_east", 5, Direction.NORTH, Direction.EAST),
    SOUTH_EAST("south_east", 6, Direction.SOUTH, Direction.EAST),
    NORTH_WEST("north_west", 7, Direction.NORTH, Direction.WEST),
    SOUTH_WEST("south_west", 8, Direction.SOUTH, Direction.WEST);

    public static final Codec<ComplexDirection> CODEC = StringRepresentable.fromEnum(ComplexDirection::values);
    public static final IntFunction<ComplexDirection> BY_INDEX = ByIdMap.continuous(ComplexDirection::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, ComplexDirection> STREAM_CODEC = ByteBufCodecs.idMapper(BY_INDEX, ComplexDirection::getIndex);

    public static final ComplexDirection[] FROM_DIRECTION_DATA2D = {
            SOUTH, WEST, NORTH, EAST
    };

    public static final ComplexDirection[] OPPOSITES = {
            IDENTITY, SOUTH, NORTH, WEST, EAST, SOUTH_WEST, NORTH_WEST, SOUTH_EAST, NORTH_EAST
    };

    public static final ComplexDirection[] CLOCKWISE = {
            IDENTITY, NORTH_EAST, SOUTH_WEST, SOUTH_EAST, NORTH_WEST, EAST, SOUTH, NORTH, WEST
    };

    private final String name;
    private final int index;
    private final List<Direction> directions;

    ComplexDirection(String name, int index, Direction... directions) {
        this.name = name;
        this.index = index;
        this.directions = Arrays.asList(directions);
    }

    public ComplexDirection getOpposite() {
        return OPPOSITES[index];
    }

    public ComplexDirection getClockWise() {
        return CLOCKWISE[index];
    }

    public ComplexDirection getClockWiseQuarter() {
        return getClockWise().getClockWise();
    }

    public double toYRot() {
        return directions.stream().mapToDouble(Direction::toYRot).average().orElse(0);
    }

    public Optional<Integer> relativeTo(int position) {
        return HotpotPlacementPositions.relative(position, this);
    }

    public Optional<Integer> relativeToCoords(int position) {
        return HotpotPlacementPositions.relativeCoords(position, this);
    }

    public <T> T reduce(T value, BiFunction<T, Direction, T> reducer) {
        return directions.stream().reduce(value, reducer, (t1, t2) -> t2);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static ComplexDirection fromDirection(Direction direction) {
        return FROM_DIRECTION_DATA2D[direction.get2DDataValue()];
    }

    public static ComplexDirection between(int position1, int position2) {
        return HotpotPlacementPositions.directionBetween(position1, position2);
    }

    public static Stream<HotpotPlacementCoords.Relative> getNearbyCoords(LevelBlockPos blockPos) {
        return Arrays.stream(values()).map(direction -> new HotpotPlacementCoords.Relative(blockPos, direction));
    }

    public static List<Integer> getNearbyOccupiedPositions(LevelBlockPos blockPos, int layer) {
        return getNearbyCoords(blockPos).map(relative -> relative.getRelativeOccupiedPositions(layer)).flatMap(Collection::stream).toList();
    }
}
