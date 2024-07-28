package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    public static final MapCodec<IHotpotPlacement> CODEC =LazyMapCodec.of(() -> getPlacementRegistry().byNameCodec().dispatchMap(IHotpotPlacement::getFactory, IHotpotPlacementFactory::buildFromCodec));
    public static final Codec<List<HotpotPlacementWithSlot>> SLOT_CODEC = Codec.lazyInitialized(() -> Codec.INT.dispatch("slot", HotpotPlacementWithSlot::slot, i -> CODEC.xmap(p -> new HotpotPlacementWithSlot(i, p), HotpotPlacementWithSlot::placement)).listOf());

    public static final ResourceLocation EMPTY_PLACEMENT_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_placement");

    public static final ResourceKey<Registry<IHotpotPlacementFactory<?>>> PLACEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placement"));
    public static final DeferredRegister<IHotpotPlacementFactory<?>> PLACEMENTS = DeferredRegister.create(PLACEMENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotPlacementFactory<?>> PLACEMENT_REGISTRY = PLACEMENTS.makeRegistry(builder -> builder.defaultKey(EMPTY_PLACEMENT_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotSmallPlate.Factory> SMALL_PLATE = PLACEMENTS.register("small_plate", HotpotSmallPlate.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotLongPlate.Factory> LONG_PLATE = PLACEMENTS.register("long_plate", HotpotLongPlate.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotLargeRoundPlate.Factory> LARGE_ROUND_PLATE = PLACEMENTS.register("large_round_plate", HotpotLargeRoundPlate.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotPlacedChopstick.Factory> PLACED_CHOPSTICK = PLACEMENTS.register("placed_chopstick", HotpotPlacedChopstick.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotPlacedSpoon.Factory> PLACED_SPOON = PLACEMENTS.register("placed_spoon", HotpotPlacedSpoon.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotPlacedPaperBowl.Factory> PLACED_PAPER_BOWL = PLACEMENTS.register("placed_paper_bowl", HotpotPlacedPaperBowl.Factory::new);
    public static final DeferredHolder<IHotpotPlacementFactory<?>, HotpotEmptyPlacement.Factory> EMPTY_PLACEMENT = PLACEMENTS.register("empty_placement", HotpotEmptyPlacement.Factory::new);

    public static HotpotEmptyPlacement buildEmptyPlacement() {
        return getEmptyPlacementFactory().build();
    }

    public static HotpotEmptyPlacement.Factory getEmptyPlacementFactory() {
        return EMPTY_PLACEMENT.get();
    }

    public static Registry<IHotpotPlacementFactory<?>> getPlacementRegistry() {
        return PLACEMENT_REGISTRY;
    }

    public static IHotpotPlacementFactory<?> getPlacementFactory(ResourceLocation resourceLocation) {
        return getPlacementRegistry().get(resourceLocation);
    }

    public static void loadPlacements(ListTag listTag, HolderLookup.Provider registryAccess, NonNullList<IHotpotPlacement> placements) {
        SLOT_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), listTag).resultOrPartial().orElseGet(List::of).stream().filter(placement -> placement.slot() >= 0 && placement.slot() < placements.size()).forEach(placement -> placements.set(placement.slot(), placement.placement()));
    }

    public static Tag savePlacements(NonNullList<IHotpotPlacement> placements, HolderLookup.Provider registryAccess) {
        return SLOT_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), IntStream.range(0, placements.size()).mapToObj(i -> new HotpotPlacementWithSlot(i, placements.get(i))).toList()).resultOrPartial().orElseGet(ListTag::new);
    }
}
