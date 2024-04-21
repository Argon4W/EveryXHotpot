package com.github.argon4w.hotpot.placements;

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

public class HotpotPlacements {
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

    public static final ResourceLocation EMPTY_PLACEMENT_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_placement");

    public static final ResourceKey<Registry<IHotpotPlacementFactory<?>>> PLACEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "placement"));
    public static final DeferredRegister<IHotpotPlacementFactory<?>> PLACEMENTS = DeferredRegister.create(PLACEMENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotPlacementFactory<?>>> PLACEMENT_REGISTRY = PLACEMENTS.makeRegistry(() -> new RegistryBuilder<IHotpotPlacementFactory<?>>().setDefaultKey(EMPTY_PLACEMENT_LOCATION));

    public static final RegistryObject<IHotpotPlacementFactory<HotpotSmallPlate>> SMALL_PLATE = PLACEMENTS.register("small_plate", () -> HotpotSmallPlate::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotLongPlate>> LONG_PLATE = PLACEMENTS.register("long_plate", () -> HotpotLongPlate::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotLargeRoundPlate>> LARGE_ROUND_PLATE = PLACEMENTS.register("large_round_plate", () -> HotpotLargeRoundPlate::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotPlacedChopstick>> PLACED_CHOPSTICK = PLACEMENTS.register("placed_chopstick", () -> HotpotPlacedChopstick::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotPlacedSpoon>> PLACED_SPOON = PLACEMENTS.register("placed_spoon", () -> HotpotPlacedSpoon::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotPlacedPaperBowl>> PLACED_PAPER_BOWL = PLACEMENTS.register("placed_paper_bowl", () -> HotpotPlacedPaperBowl::new);
    public static final RegistryObject<IHotpotPlacementFactory<HotpotEmptyPlacement>> EMPTY_PLACEMENT = PLACEMENTS.register("empty_placement", () -> HotpotEmptyPlacement::new);

    public static IHotpotPlacementFactory<HotpotEmptyPlacement> getEmptyPlacement() {
        return EMPTY_PLACEMENT.get();
    }

    public static IForgeRegistry<IHotpotPlacementFactory<?>> getPlacementRegistry() {
        return PLACEMENT_REGISTRY.get();
    }

    public static IHotpotPlacementFactory<?> getPlacementFactory(ResourceLocation resourceLocation) {
        return getPlacementRegistry().getValue(resourceLocation);
    }
}
