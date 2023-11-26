package com.github.argon4w.hotpot.placeables;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.*;

public class HotpotPlaceables {
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

    public static final ResourceLocation EMPTY_PLACEABLE_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_placeable");

    public static final ResourceKey<Registry<HotpotPlaceableType<?>>> PLACEABLE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "placeable"));
    public static final DeferredRegister<HotpotPlaceableType<?>> PLACEABLES = DeferredRegister.create(PLACEABLE_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<HotpotPlaceableType<?>>> PLACEABLE_REGISTRY = PLACEABLES.makeRegistry(() -> new RegistryBuilder<HotpotPlaceableType<?>>().setDefaultKey(EMPTY_PLACEABLE_LOCATION));

    public static final RegistryObject<HotpotPlaceableType<HotpotSmallPlate>> SMALL_PLATE = PLACEABLES.register("small_plate", () -> HotpotSmallPlate::new);
    public static final RegistryObject<HotpotPlaceableType<HotpotLongPlate>> LONG_PLATE = PLACEABLES.register("long_plate", () -> HotpotLongPlate::new);
    public static final RegistryObject<HotpotPlaceableType<HotpotPlacedChopstick>> PLACED_CHOPSTICK = PLACEABLES.register("placed_chopstick", () -> HotpotPlacedChopstick::new);
    public static final RegistryObject<HotpotPlaceableType<HotpotEmptyPlaceable>> EMPTY_PLACEABLE = PLACEABLES.register("empty_placeable", () -> HotpotEmptyPlaceable::new);

    public static HotpotPlaceableType<HotpotEmptyPlaceable> getEmptyPlaceable() {
        return EMPTY_PLACEABLE.get();
    }

    public static IForgeRegistry<HotpotPlaceableType<?>> getPlaceableRegistry() {
        return PLACEABLE_REGISTRY.get();
    }
}
