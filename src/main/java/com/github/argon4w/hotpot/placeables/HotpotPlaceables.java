package com.github.argon4w.hotpot.placeables;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HotpotPlaceables {
    public static final HashMap<String, Supplier<IHotpotPlaceable>> HOTPOT_PLATE_TYPES = new HashMap<>(ImmutableMap.of(
            "Empty", HotpotEmptyPlaceable::new,
            "LongPlate", HotpotLongPlate::new,
            "SmallPlate", HotpotSmallPlate::new,
            "PlacedChopstick", HotpotPlacedChopstick::new
    ));
    public static final HashMap<Integer, Direction> POS_TO_DIRECTION = new HashMap<>(ImmutableMap.of(
            -1, Direction.NORTH,
            +1, Direction.SOUTH,
            +2, Direction.EAST,
            -2, Direction.WEST
    ));
    public static final HashMap<Direction, Integer> DIRECTION_TO_POS = new HashMap<>(ImmutableMap.of(
            Direction.NORTH, -1,
            Direction.SOUTH, +1,
            Direction.EAST, +2,
            Direction.WEST, -2
    ));

    public static Supplier<IHotpotPlaceable> getEmptyPlaceable() {
        return HotpotPlaceables.HOTPOT_PLATE_TYPES.get("Empty");
    }

    public static Supplier<IHotpotPlaceable> getPlaceableOrElseEmpty(String key) {
        return HotpotPlaceables.HOTPOT_PLATE_TYPES.getOrDefault(key, getEmptyPlaceable());
    }
}
