package com.github.argon4w.hotpot.placeables;

import net.minecraft.core.Direction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class HotpotPlaceables {
    public static final ConcurrentHashMap<String, Supplier<IHotpotPlaceable>> HOTPOT_PLATE_TYPES = new ConcurrentHashMap<>(Map.of(
            "Empty", HotpotEmptyPlaceable::new,
            "LongPlate", HotpotLongPlate::new,
            "SmallPlate", HotpotSmallPlate::new,
            "PlacedChopstick", HotpotPlacedChopstick::new
    ));
    public static final Map<Integer, Direction> POS_TO_DIRECTION = Map.of(
            - 1, Direction.NORTH,
            + 1, Direction.SOUTH,
            + 2, Direction.EAST,
            - 2, Direction.WEST
    );
    public static final Map<Direction, Integer> DIRECTION_TO_POS = Map.of(
            Direction.NORTH, - 1,
            Direction.SOUTH, + 1,
            Direction.EAST, + 2,
            Direction.WEST, - 2
    );

    public static Supplier<IHotpotPlaceable> getEmptyPlaceable() {
        return HotpotPlaceables.HOTPOT_PLATE_TYPES.get("Empty");
    }

    public static Supplier<IHotpotPlaceable> getPlaceableOrElseEmpty(String key) {
        return HotpotPlaceables.HOTPOT_PLATE_TYPES.getOrDefault(key, getEmptyPlaceable());
    }
}
