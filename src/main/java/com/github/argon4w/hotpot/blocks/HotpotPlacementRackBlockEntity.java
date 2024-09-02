package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.items.HotpotPlacementBlockItem;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

public class HotpotPlacementRackBlockEntity extends AbstractHotpotCodecBlockEntity<HotpotPlacementRackBlockEntity.Data, HotpotPlacementRackBlockEntity.PartialData> implements Clearable, IHotpotPlacementContainer {

    public static final Codec<Data> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotPlacementSerializers.CODEC.listOf().xmap(LinkedList::new, Function.identity()).fieldOf("placements1").forGetter(Data::placements1),
                    HotpotPlacementSerializers.CODEC.listOf().xmap(LinkedList::new, Function.identity()).fieldOf("placements2").forGetter(Data::placements2),
                    Codec.BOOL.fieldOf("infinite_content").forGetter(Data::infiniteContent),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(Data::canBeRemoved)
            ).apply(data, Data::new))
    );

    public static final Codec<PartialData> PARTIAL_CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotPlacementSerializers.CODEC.listOf().xmap(LinkedList::new, Function.identity()).optionalFieldOf("placements1").forGetter(PartialData::placements1),
                    HotpotPlacementSerializers.CODEC.listOf().xmap(LinkedList::new, Function.identity()).optionalFieldOf("placements2").forGetter(PartialData::placements2),
                    Codec.BOOL.fieldOf("infinite_content").forGetter(PartialData::infiniteContent),
                    Codec.BOOL.fieldOf("can_be_removed").forGetter(PartialData::canBeRemoved)
            ).apply(data, PartialData::new))
    );

    public static final List<Integer> PROVIDED_POSITIONS = List.of(5, 9, 6, 10);

    private Data data = getDefaultData();
    private boolean contentChanged = true;

    public HotpotPlacementRackBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public void setContentByInteraction(int position, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos pos) {
        int index = getPlacementIndexInPosAndLayer(position, layer);

        if (index < 0) {
            return;
        }

        IHotpotPlacement placement = getPlacements(layer).get(index);
        placement.interact(player, hand, itemStack, position, layer, pos, this);

        if (placement.shouldRemove(player, hand, itemStack, position, layer, pos, this)) {
            removePlacement(index, layer, pos);
        }

        markDataChanged();
    }

    @Override
    public ItemStack getContentByTableware(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos) {
        int index = getPlacementIndexInPosAndLayer(position, layer);

        if (index < 0) {
            return ItemStack.EMPTY;
        }

        IHotpotPlacement placement = getPlacements(layer).get(index);
        ItemStack itemStack = placement.getContent(player, hand, position, layer, pos, this, true);

        if (placement.shouldRemove(player, hand, itemStack, position, layer, pos, this)) {
            removePlacement(index, layer, pos);
        }

        markDataChanged();
        return itemStack;
    }

    @Override
    public void place(IHotpotPlacement placement, int pos, int layer) {
        List<IHotpotPlacement> placements = getPlacements(layer);
        placements.add(placement);
        markDataChanged();
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
    public List<Integer> getOccupiedPositions(int layer) {
        return getPlacements(layer).stream().map(IHotpotPlacement::getPositions).flatMap(Collection::stream).toList();
    }

    @Override
    public List<Integer> getProvidedPositions(int layer) {
        return layer == 1 || layer == 2 ? PROVIDED_POSITIONS : List.of();
    }

    @Override
    public int getLayer(Vec3 vec3) {
        return getLayerFromVec3(vec3);
    }

    @Override
    public Data getDefaultData() {
        return new Data(new LinkedList<>(), new LinkedList<>(), false, true);
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
    public PartialData getPartialData(HolderLookup.Provider registryAccess) {
        return new PartialData(contentChanged ? Optional.of(data.placements1) : Optional.empty(), contentChanged ? Optional.of(data.placements2) : Optional.empty(), data.infiniteContent, data.canBeRemoved);
    }

    @Override
    public void onPartialDataUpdated() {
        contentChanged = false;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public void clearContent() {
        data.placements1.clear();
        data.placements2.clear();
    }

    public List<IHotpotPlacement> getPlacements(int layer) {
        return switch (layer) {
            case 1 -> data.placements1;
            case 2 -> data.placements2;
            default -> List.of();
        };
    }

    public void onRemove(LevelBlockPos pos) {
        data.placements1.forEach(placement -> placement.onRemove(this, pos));
        data.placements2.forEach(placement -> placement.onRemove(this, pos));
        markDataChanged();
    }

    public void removePlacement(int index, int layer, LevelBlockPos pos) {
        List<IHotpotPlacement> placements = getPlacements(layer);
        placements.remove(index).onRemove(this, pos);
        markDataChanged();
    }
    
    public int getPlacementIndexInPosAndLayer(int position, int layer) {
        List<IHotpotPlacement> placements = getPlacements(layer);
        return IntStream.range(0, placements.size()).filter(i -> placements.get(i).getPositions().contains(position)).findFirst().orElse(-1);
    }

    public IHotpotPlacement getPlacementInPosAndLayer(int position, int layer) {
        int i = getPlacementIndexInPosAndLayer(position, layer);
        return i < 0 ? HotpotPlacementSerializers.loadEmptyPlacement() : getPlacements(layer).get(i);
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public List<IHotpotPlacement> getPlacements1() {
        return data.placements1;
    }

    public List<IHotpotPlacement> getPlacements2() {
        return data.placements2;
    }

    public static int getLayerFromVec3(Vec3 vec3) {
        return vec3.y() < 0.5 ? 1 : 2;
    }

    public static int getLayerFromBlockHitResult(BlockHitResult blockHitResult) {
        return getLayerFromVec3(getVec3(blockHitResult));
    }

    public static int getLayerFromHitResult(HitResult hitResult, BlockPos pos) {
        return getLayerFromVec3(getVec3(hitResult, pos));
    }

    public static Vec3 getVec3(HitResult hitResult, BlockPos pos) {
        return HotpotPlacementBlockItem.getVec3(pos, hitResult.getLocation());
    }

    public static Vec3 getVec3(BlockHitResult result) {
        return HotpotPlacementBlockItem.getVec3(result.getBlockPos(), result.getLocation());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementRackBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public record Data(LinkedList<IHotpotPlacement> placements1, LinkedList<IHotpotPlacement> placements2, boolean infiniteContent, boolean canBeRemoved) {

    }

    public record PartialData(Optional<LinkedList<IHotpotPlacement>> placements1, Optional<LinkedList<IHotpotPlacement>> placements2, boolean infiniteContent, boolean canBeRemoved) implements AbstractHotpotCodecBlockEntity.PartialData<Data> {
        @Override
        public Data update(Data data) {
            return new Data(placements1.orElse(data.placements1), placements2.orElse(data.placements2), infiniteContent, canBeRemoved);
        }
    }
}
