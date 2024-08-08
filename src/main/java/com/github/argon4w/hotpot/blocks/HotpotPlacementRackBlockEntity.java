package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.HotpotEmptyPlacement;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class HotpotPlacementRackBlockEntity extends AbstractTablewareInteractiveBlockEntity implements Clearable, IHotpotPlacementContainerBlockEntity {
    private final NonNullList<IHotpotPlacement> placements1 = NonNullList.withSize(4, HotpotPlacementSerializers.buildEmptyPlacement());
    private final NonNullList<IHotpotPlacement> placements2 = NonNullList.withSize(4, HotpotPlacementSerializers.buildEmptyPlacement());
    
    private boolean contentChanged = true;
    private boolean infiniteContent = false;
    private boolean canBeRemoved = true;

    public HotpotPlacementRackBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public ItemStack tryPlaceContentViaTableware(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        tryPlaceContentViaInteraction(hitPos, layer, player, hand, itemStack, selfPos);
        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        IHotpotPlacement placement = getPlacementInPosAndLayer(hitPos, layer);

        if (placement.interact(player, hand, itemStack, hitPos, layer, selfPos, this) && canBeRemoved()) {
            tryRemove(hitPos, layer, selfPos);
        }

        markDataChanged();
    }

    @Override
    public ItemStack tryTakeOutContentViaTableware(Player player, int hitPos, int layer, LevelBlockPos pos) {
        IHotpotPlacement placement = getPlacementInPosAndLayer(hitPos, layer);
        ItemStack itemStack = placement.takeOutContent(hitPos, layer, pos, this, true);
        markDataChanged();

        return itemStack;
    }

    @Override
    public boolean place(IHotpotPlacement placement, int pos, int layer) {
        if (!isNotConflict(placement, layer)) {
            return false;
        }

        NonNullList<IHotpotPlacement> placements = getPlacements(layer);
        IHotpotPlacement toReplace = placements.get(pos);

        if (!(toReplace instanceof HotpotEmptyPlacement)) {
            return false;
        }

        placements.set(pos, placement);
        markDataChanged();

        return true;
    }

    public void tryRemove(int hitPos, int layer, LevelBlockPos pos) {
        int index = getPlacementIndexInPosAndLayer(hitPos, layer);

        if (isEmptyPlacement(index, layer)) {
            return;
        }

        removePlacement(index, layer, pos);
    }

    public void onRemove(LevelBlockPos pos) {
        placements1.forEach(placement -> removePlacement(placement, pos));
        placements2.forEach(placement -> removePlacement(placement, pos));
        clearContent();
        markDataChanged();
    }

    public void removePlacement(int index, int layer, LevelBlockPos pos) {
        NonNullList<IHotpotPlacement> placements = getPlacements(layer);
        removePlacement(placements.get(index), pos);
        placements.set(index, HotpotPlacementSerializers.buildEmptyPlacement());
        markDataChanged();
    }

    public void removePlacement(IHotpotPlacement placement, LevelBlockPos pos) {
        placement.onRemove(this, pos);
        pos.dropItemStack(placement.getCloneItemStack(this, pos));
    }
    
    public int getPlacementIndexInPosAndLayer(int hitPos, int layer) {
        NonNullList<IHotpotPlacement> placements = getPlacements(layer);
        return IntStream.range(0, placements.size()).filter(i -> placements.get(i).getPoslist().contains(hitPos)).findFirst().orElse(-1);
    }

    public IHotpotPlacement getPlacementInPosAndLayer(int hitPos, int layer) {
        int i = getPlacementIndexInPosAndLayer(hitPos, layer);
        return i < 0 ? HotpotPlacementSerializers.buildEmptyPlacement() : getPlacements(layer).get(i);
    }

    @Override
    public void clearContent() {
        this.placements1.clear();
        this.placements2.clear();
    }

    @Nullable
    @Override //Game Tick
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, this::getUpdatePacketTag);
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.loadAdditional(compoundTag, registryAccess);

        canBeRemoved = !compoundTag.contains("CanBeRemoved", Tag.TAG_ANY_NUMERIC) || compoundTag.getBoolean("CanBeRemoved");
        infiniteContent = compoundTag.contains("InfiniteContent", Tag.TAG_ANY_NUMERIC) && compoundTag.getBoolean("InfiniteContent");

        if (compoundTag.contains("Placements1", Tag.TAG_LIST)) {
            placements1.clear();
            HotpotPlacementSerializers.loadPlacements(compoundTag.getList("Placements1", Tag.TAG_COMPOUND), registryAccess, placements1);
        }

        if (compoundTag.contains("Placements2", Tag.TAG_LIST)) {
            placements2.clear();
            HotpotPlacementSerializers.loadPlacements(compoundTag.getList("Placements2", Tag.TAG_COMPOUND), registryAccess, placements2);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(compoundTag, registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        compoundTag.put("Placements1", HotpotPlacementSerializers.savePlacements(placements1, registryAccess));
        compoundTag.put("Placements2", HotpotPlacementSerializers.savePlacements(placements2, registryAccess));
    }

    public CompoundTag getUpdatePacketTag(BlockEntity blockEntity, HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        if (!contentChanged) {
            return compoundTag;
        }

        compoundTag.put("Placements1", HotpotPlacementSerializers.savePlacements(placements1, registryAccess));
        compoundTag.put("Placements2", HotpotPlacementSerializers.savePlacements(placements2, registryAccess));
        contentChanged = false;

        return compoundTag;
    }

    @NotNull
    @Override //Chunk Load
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = super.getUpdateTag(registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        compoundTag.put("Placements1", HotpotPlacementSerializers.savePlacements(placements1, registryAccess));
        compoundTag.put("Placements2", HotpotPlacementSerializers.savePlacements(placements2, registryAccess));

        return compoundTag;
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    @Override
    public boolean isInfiniteContent() {
        return infiniteContent;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    public boolean isEmptyPlacement(int index, int layer) {
        return getPlacements(layer).get(index) instanceof HotpotEmptyPlacement;
    }

    public boolean isEmpty() {
        return placements1.stream().allMatch(placement -> placement instanceof HotpotEmptyPlacement) && placements2.stream().allMatch(placement -> placement instanceof HotpotEmptyPlacement);
    }

    public List<IHotpotPlacement> getPlacements1() {
        return placements1;
    }

    public NonNullList<IHotpotPlacement> getPlacements2() {
        return placements2;
    }
    
    public NonNullList<IHotpotPlacement> getPlacements(int layer) {
        return layer == 0 ? placements1 : placements2;
    }

    public boolean isNotConflict(IHotpotPlacement another, int layer) {
        return getPlacements(layer).stream().noneMatch(plate -> another.getPoslist().stream().anyMatch(plate::isConflict));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementRackBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
