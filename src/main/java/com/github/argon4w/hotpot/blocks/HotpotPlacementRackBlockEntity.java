package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class HotpotPlacementRackBlockEntity extends BlockEntity implements Clearable, IHotpotPlacementContainer {
    private final LinkedList<IHotpotPlacement> placements1 = new LinkedList<>();
    private final LinkedList<IHotpotPlacement> placements2 = new LinkedList<>();
    
    private boolean contentChanged = true;
    private boolean infiniteContent = false;
    private boolean canBeRemoved = true;

    public HotpotPlacementRackBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_RACK_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        IHotpotPlacement placement = getPlacementInPosAndLayer(hitPos, layer);
        placement.interact(player, hand, itemStack, hitPos, layer, selfPos, this);

        if (placement.shouldRemove(player, hand, itemStack, hitPos, layer, selfPos, this)) {
            tryRemove(hitPos, layer, selfPos);
        }

        markDataChanged();
    }

    @Override
    public ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer, LevelBlockPos pos) {
        IHotpotPlacement placement = getPlacementInPosAndLayer(hitPos, layer);
        ItemStack itemStack = placement.getContent(player, hand, hitPos, layer, pos, this, true);

        if (placement.shouldRemove(player, hand, itemStack, hitPos, layer, pos, this)) {
            tryRemove(hitPos, layer, pos);
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
    public boolean isInfiniteContent() {
        return infiniteContent;
    }

    @Override
    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    @Override
    public boolean isPositionValid(int position, int layer) {
        return position == 5 || position == 9 || position == 6 || position == 10;
    }

    @Override
    public List<Integer> getOccupiedPositions(int layer) {
        return getPlacements(layer).stream().map(IHotpotPlacement::getPositions).flatMap(Collection::stream).toList();
    }

    @Override
    public int getLayerOffset() {
        return 1;
    }

    public void tryRemove(int hitPos, int layer, LevelBlockPos pos) {
        int index = getPlacementIndexInPosAndLayer(hitPos, layer);

        if (index < 0) {
            return;
        }

        removePlacement(index, layer, pos);
    }

    public void onRemove(LevelBlockPos pos) {
        placements1.forEach(placement -> placement.onRemove(this, pos));
        placements2.forEach(placement -> placement.onRemove(this, pos));
        clearContent();
        markDataChanged();
    }

    public void removePlacement(int index, int layer, LevelBlockPos pos) {
        List<IHotpotPlacement> placements = getPlacements(layer);
        placements.remove(index).onRemove(this, pos);
        markDataChanged();
    }
    
    public int getPlacementIndexInPosAndLayer(int hitPos, int layer) {
        List<IHotpotPlacement> placements = getPlacements(layer);
        return IntStream.range(0, placements.size()).filter(i -> placements.get(i).getPositions().contains(hitPos)).findFirst().orElse(-1);
    }

    public IHotpotPlacement getPlacementInPosAndLayer(int hitPos, int layer) {
        int i = getPlacementIndexInPosAndLayer(hitPos, layer);
        return i < 0 ? HotpotPlacementSerializers.loadEmptyPlacement() : getPlacements(layer).get(i);
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

    public List<IHotpotPlacement> getPlacements1() {
        return placements1;
    }

    public List<IHotpotPlacement> getPlacements2() {
        return placements2;
    }
    
    public List<IHotpotPlacement> getPlacements(int layer) {
        return switch (layer) {
            case 1 -> placements1;
            case 2 -> placements2;
            default -> List.of();
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementRackBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
