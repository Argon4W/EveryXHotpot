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

public class HotpotPlacementSerializers {
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

    public static final MapCodec<IHotpotPlacement> CODEC = LazyMapCodec.of(() -> getPlacementSerializerRegistry().holderByNameCodec().dispatchMap(IHotpotPlacement::getPlacementSerializerHolder, holder -> holder.value().getCodec()));
    public static final Codec<List<HotpotPlacementWithSlot>> SLOT_CODEC = Codec.lazyInitialized(() -> Codec.INT.dispatch("slot", HotpotPlacementWithSlot::slot, i -> CODEC.xmap(p -> new HotpotPlacementWithSlot(i, p), HotpotPlacementWithSlot::placement)).listOf());

    public static final ResourceLocation EMPTY_PLACEMENT_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_placement");

    public static final ResourceKey<Registry<IHotpotPlacementSerializer<?>>> PLACEMENT_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placement_serializer"));
    public static final DeferredRegister<IHotpotPlacementSerializer<?>> PLACEMENT_SERIALIZERS = DeferredRegister.create(PLACEMENT_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotPlacementSerializer<?>> PLACEMENT_SERIALIZER_REGISTRY = PLACEMENT_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_PLACEMENT_SERIALIZER_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotSmallPlate.Serializer> SMALL_PLATE_SERIALIZER = PLACEMENT_SERIALIZERS.register("small_plate", HotpotSmallPlate.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotLongPlate.Serializer> LONG_PLATE_SERIALIZER = PLACEMENT_SERIALIZERS.register("long_plate", HotpotLongPlate.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotLargeRoundPlate.Serializer> LARGE_ROUND_PLATE_SERIALIZER = PLACEMENT_SERIALIZERS.register("large_round_plate", HotpotLargeRoundPlate.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotPlacedNapkinHolder.Serializer> NAPKIN_HOLDER_SERIALIZER = PLACEMENT_SERIALIZERS.register("napkin_holder", HotpotPlacedNapkinHolder.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotPlacedChopstick.Serializer> PLACED_CHOPSTICK_SERIALIZER = PLACEMENT_SERIALIZERS.register("placed_chopstick", HotpotPlacedChopstick.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotPlacedSpoon.Serializer> PLACED_SPOON_SERIALIZER = PLACEMENT_SERIALIZERS.register("placed_spoon", HotpotPlacedSpoon.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotPlacedPaperBowl.Serializer> PLACED_PAPER_BOWL_SERIALIZER = PLACEMENT_SERIALIZERS.register("placed_paper_bowl", HotpotPlacedPaperBowl.Serializer::new);
    public static final DeferredHolder<IHotpotPlacementSerializer<?>, HotpotEmptyPlacement.Serializer> EMPTY_PLACEMENT_SERIALIZER = PLACEMENT_SERIALIZERS.register("empty_placement", HotpotEmptyPlacement.Serializer::new);

    public static HotpotEmptyPlacement buildEmptyPlacement() {
        return getEmptyPlacementSerializer().build();
    }

    public static HotpotEmptyPlacement.Serializer getEmptyPlacementSerializer() {
        return EMPTY_PLACEMENT_SERIALIZER.get();
    }

    public static Registry<IHotpotPlacementSerializer<?>> getPlacementSerializerRegistry() {
        return PLACEMENT_SERIALIZER_REGISTRY;
    }

    public static void loadPlacements(ListTag listTag, HolderLookup.Provider registryAccess, NonNullList<IHotpotPlacement> placements) {
        SLOT_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), listTag).resultOrPartial().orElseGet(List::of).stream().filter(placement -> placement.slot() >= 0 && placement.slot() < placements.size()).forEach(placement -> placements.set(placement.slot(), placement.placement()));
    }

    public static Tag savePlacements(NonNullList<IHotpotPlacement> placements, HolderLookup.Provider registryAccess) {
        return SLOT_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), IntStream.range(0, placements.size()).mapToObj(i -> new HotpotPlacementWithSlot(i, placements.get(i))).toList()).resultOrPartial().orElseGet(ListTag::new);
    }

    public static float getSlotX(int slot) {
        return ((2 & slot) > 0 ? 0.5f : 0f);
    }

    public static float getSlotZ(int slot) {
        return ((1 & slot) > 0 ? 0.5f : 0f);
    }
}
