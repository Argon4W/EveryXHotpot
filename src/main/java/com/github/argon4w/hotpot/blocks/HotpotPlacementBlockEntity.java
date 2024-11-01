package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.AbstractCodecBlockEntity;
import com.github.argon4w.hotpot.api.blocks.AbstractHotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HotpotPlacementBlockEntity
        extends AbstractHotpotPlacementBlockEntity<
                HotpotPlacementBlockEntity.Data, HotpotPlacementBlockEntity.PartialData>
        implements Clearable, IHotpotPlacementContainer {
    public static final Codec<Data> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(data -> data.group(
                    HotpotPlacementSerializers.CODEC
                            .listOf()
                            .xmap(LinkedList::new, Function.identity())
                            .fieldOf("placements")
                            .forGetter(Data::placements),
                    Codec.BOOL.fieldOf("infinite_content").forGetter(Data::infiniteContent),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(Data::canBeRemoved))
            .apply(data, Data::new)));

    public static final Codec<PartialData> PARTIAL_CODEC =
            Codec.lazyInitialized(() -> RecordCodecBuilder.create(data -> data.group(
                            HotpotPlacementSerializers.CODEC
                                    .listOf()
                                    .xmap(LinkedList::new, Function.identity())
                                    .optionalFieldOf("placements")
                                    .forGetter(PartialData::placements),
                            Codec.BOOL.fieldOf("infinite_content").forGetter(PartialData::infiniteContent),
                            Codec.BOOL.fieldOf("can_be_removed").forGetter(PartialData::canBeRemoved))
                    .apply(data, PartialData::new)));

    public static final List<Integer> PROVIDED_POSITIONS =
            IntStream.range(0, 16).boxed().toList();

    private boolean contentChanged = true;

    public HotpotPlacementBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public void interact(
            int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        if (!isEmpty()) {
            super.interact(position, layer, player, hand, itemStack, pos);
        }

        if (isEmpty()) {
            pos.removeBlock(true);
        }
    }

    @Override
    public List<Integer> getOccupiedPositions(int layer, LevelBlockPos pos) {
        return layer == 0
                ? data.placements.stream()
                        .map(IHotpotPlacement::getPositions)
                        .flatMap(Collection::stream)
                        .toList()
                : List.of();
    }

    @Override
    public void place(IHotpotPlacement placement, int position, int layer, LevelBlockPos pos) {
        data.placements.add(placement);
        markDataChanged();
    }

    @Override
    public List<Integer> getProvidedPositions(int layer, LevelBlockPos pos) {
        return layer == 0 ? PROVIDED_POSITIONS : List.of();
    }

    @Override
    public boolean canConsumeContents() {
        return !data.infiniteContent;
    }

    @Override
    public boolean canBeRemoved() {
        return data.canBeRemoved;
    }

    @Override
    public int getLayer(Vec3 vec3) {
        return 0;
    }

    @Override
    public PartialData getPartialData(HolderLookup.Provider registryAccess) {
        return new PartialData(
                contentChanged ? Optional.of(data.placements) : Optional.empty(),
                data.infiniteContent,
                data.canBeRemoved);
    }

    @Override
    public Data getDefaultData(HolderLookup.Provider registryAccess) {
        return new Data(new LinkedList<>(), false, true);
    }

    @Override
    public Codec<Data> getFullCodec() {
        return CODEC;
    }

    @Override
    public Codec<PartialData> getPartialCodec() {
        return PARTIAL_CODEC;
    }

    @Override
    public Data onFullDataUpdate(LevelBlockPos pos, Data data) {
        pos.markAndNotifyClient();
        return data;
    }

    @Override
    public Data onFullDataUpdate(Data data) {
        return data;
    }

    @Override
    public void onPartialDataUpdated() {
        contentChanged = false;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public void clearContent() {
        data.placements.clear();
    }

    @Override
    public int getPlacementIndexInPosAndLayer(int position, int layer) {
        return IntStream.range(0, data.placements.size())
                .filter(i -> data.placements.get(i).getPositions().contains(position))
                .findFirst()
                .orElse(-1);
    }

    @Override
    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    @Override
    public void removePlacement(int index, int layer, LevelBlockPos pos) {
        data.placements.remove(index).onRemove(this, pos);
        markDataChanged();
    }

    @Override
    public List<IHotpotPlacement> getPlacements(int layer) {
        return data.placements;
    }

    public void onRemove(LevelBlockPos pos) {
        data.placements.forEach(placement -> placement.onRemove(this, pos));
        markDataChanged();
    }

    public boolean isEmpty() {
        return data.placements.isEmpty();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public record Data(LinkedList<IHotpotPlacement> placements, boolean infiniteContent, boolean canBeRemoved) {}

    public record PartialData(
            Optional<LinkedList<IHotpotPlacement>> placements, boolean infiniteContent, boolean canBeRemoved)
            implements AbstractCodecBlockEntity.PartialData<Data> {
        @Override
        public Data update(Data data) {
            return new Data(placements.orElse(data.placements), infiniteContent, canBeRemoved);
        }
    }
}
