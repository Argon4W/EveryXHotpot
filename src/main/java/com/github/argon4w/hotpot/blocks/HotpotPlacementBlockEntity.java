package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
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

public class HotpotPlacementBlockEntity extends BlockEntity implements Clearable, IHotpotPlacementContainer {
    private final List<IHotpotPlacement> placements = new LinkedList<>();

    private boolean contentChanged = true;
    private boolean infiniteContent = false;
    private boolean canBeRemoved = true;

    public HotpotPlacementBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public void setContentByInteraction(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        if (isEmpty()) {
            selfPos.removeBlock(true);
            return;
        }

        int index = getPlacementIndexInPos(hitPos);

        if (index < 0) {
            ComplexDirection.getNearbyCoords(selfPos).filter(relative -> relative.hasRelativePosition(hitPos, layer)).findFirst().ifPresent(relative -> relative.setContentByInteraction(hitPos, layer, player, hand, itemStack));
            return;
        }

        IHotpotPlacement placement = placements.get(index);
        placement.interact(player, hand, itemStack, hitPos, layer, selfPos, this);

        if (placement.shouldRemove(player, hand, itemStack, hitPos, layer, selfPos, this)) {
            tryRemove(hitPos, selfPos);
        }

        markDataChanged();
    }

    @Override
    public ItemStack getContentByTableware(Player player, InteractionHand hand, int hitPos, int layer, LevelBlockPos pos) {
        if (isEmpty()) {
            pos.removeBlock(true);
            return ItemStack.EMPTY;
        }

        int index = getPlacementIndexInPos(hitPos);

        if (index < 0) {
            return ComplexDirection.getNearbyCoords(pos).filter(relative -> relative.hasRelativePosition(hitPos, layer)).findFirst().map(relative -> relative.getContentByTableware(player, hand, hitPos, layer)).orElse(ItemStack.EMPTY);
        }

        IHotpotPlacement placement = placements.get(index);
        ItemStack itemStack = placement.getContent(player, hand, hitPos, layer, pos, this, true);

        if (placement.shouldRemove(player, hand, itemStack, hitPos, layer, pos, this)) {
            tryRemove(hitPos, pos);
        }

        markDataChanged();

        return itemStack;
    }

    @Override
    public void place(IHotpotPlacement placement, int pos, int layer) {
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
        return position >= 0 && position <= 15 && layer == 0;
    }

    @Override
    public List<Integer> getOccupiedPositions(int layer) {
        return layer == 0 ? placements.stream().map(IHotpotPlacement::getPositions).flatMap(Collection::stream).toList() : List.of();
    }

    @Override
    public int getLayerOffset() {
        return 0;
    }

    public void tryRemove(int hitPos, LevelBlockPos pos) {
        int index = getPlacementIndexInPos(hitPos);

        if (index < 0) {
            return;
        }

        removePlacement(index, pos);

        if (!isEmpty()) {
            return;
        }

        pos.removeBlock(true);
    }

    public void onRemove(LevelBlockPos pos) {
        placements.forEach(placement -> placement.onRemove(this, pos));
        clearContent();
        markDataChanged();
    }

    public void removePlacement(int index, LevelBlockPos pos) {
        placements.remove(index).onRemove(this, pos);
        markDataChanged();
    }

    public int getPlacementIndexInPos(int hitPos) {
        return IntStream.range(0, placements.size()).filter(i -> placements.get(i).getPositions().contains(hitPos)).findFirst().orElse(-1);
    }

    @Override
    public void clearContent() {
        this.placements.clear();
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

        if (compoundTag.contains("Placements", Tag.TAG_LIST)) {
            placements.clear();
            HotpotPlacementSerializers.loadPlacements(compoundTag.getList("Placements", Tag.TAG_COMPOUND), registryAccess, placements);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(compoundTag, registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        compoundTag.put("Placements", HotpotPlacementSerializers.savePlacements(placements, registryAccess));
    }

    public CompoundTag getUpdatePacketTag(BlockEntity blockEntity, HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        if (!contentChanged) {
            return compoundTag;
        }

        compoundTag.put("Placements", HotpotPlacementSerializers.savePlacements(placements, registryAccess));
        contentChanged = false;

        return compoundTag;
    }

    @NotNull
    @Override //Chunk Load
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = super.getUpdateTag(registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);
        compoundTag.put("Placements", HotpotPlacementSerializers.savePlacements(placements, registryAccess));

        return compoundTag;
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public boolean isEmpty() {
        return placements.isEmpty();
    }

    public List<IHotpotPlacement> getPlacements() {
        return placements;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
