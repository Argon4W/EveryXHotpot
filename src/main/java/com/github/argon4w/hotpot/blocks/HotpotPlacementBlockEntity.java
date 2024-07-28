package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.HotpotEmptyPlacement;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class HotpotPlacementBlockEntity extends AbstractTablewareInteractiveBlockEntity implements Clearable {
    private final NonNullList<IHotpotPlacement> placements = NonNullList.withSize(4, HotpotPlacements.buildEmptyPlacement());

    private boolean contentChanged = true;
    private boolean infiniteContent = false;
    private boolean canBeRemoved = true;

    public HotpotPlacementBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public ItemStack tryPlaceContentViaTableware(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        tryPlaceContentViaInteraction(hitPos, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, LevelBlockPos selfPos) {
        if (isEmpty()) {
            selfPos.removeBlock(true);
            return;
        }

        IHotpotPlacement placement = getPlacementInPos(hitPos);

        if (placement.interact(player, hand, itemStack, hitPos, this, selfPos) && canBeRemoved()) {
            tryRemove(hitPos, selfPos);
        }

        markDataChanged();
    }

    @Override
    public ItemStack tryTakeOutContentViaTableware(Player player, int hitPos, LevelBlockPos pos) {
        if (isEmpty()) {
            pos.removeBlock(true);
            return ItemStack.EMPTY;
        }

        IHotpotPlacement placement = getPlacementInPos(hitPos);
        ItemStack itemStack = placement.takeOutContent(hitPos, this, pos, true);
        markDataChanged();

        return itemStack;
    }

    public void tryTakeOutContentViaHand(int hitPos, LevelBlockPos pos) {
        if (isEmpty()) {
            pos.removeBlock(true);
            return;
        }

        IHotpotPlacement placement = getPlacementInPos(hitPos);
        ItemStack itemStack = placement.takeOutContent(hitPos, this, pos, false);
        pos.dropItemStack(itemStack);
        markDataChanged();
    }

    public void tryRemove(int hitPos, LevelBlockPos pos) {
        int index = getPlacementIndexInPos(hitPos);

        if (!isEmptyPlacement(index)) {
            removePlacement(index, pos);
        }

        if (!isEmpty()) {
            return;
        }

        pos.removeBlock(true);
    }

    public boolean place(IHotpotPlacement placement, int pos) {
        if (!isNotConflict(placement)) {
            return false;
        }

        IHotpotPlacement toReplace = placements.get(pos);

        if (!(toReplace instanceof HotpotEmptyPlacement)) {
            return false;
        }

        placements.set(pos, placement);
        markDataChanged();

        return true;
    }

    public void onRemove(LevelBlockPos pos) {
        placements.forEach(placement -> removePlacement(placement, pos));
        placements.clear();
        markDataChanged();
    }

    public void removePlacement(int index, LevelBlockPos pos) {
        removePlacement(placements.get(index), pos);
        placements.set(index, HotpotPlacements.getEmptyPlacementFactory().build());
        markDataChanged();
    }

    public void removePlacement(IHotpotPlacement placement, LevelBlockPos pos) {
        placement.onRemove(this, pos);
        pos.dropItemStack(placement.getCloneItemStack(this, pos));
    }
    
    public int getPlacementIndexInPos(int hitPos) {
        return IntStream.range(0, placements.size()).filter(i -> placements.get(i).getPoslist().contains(hitPos)).findFirst().orElse(-1);
    }

    public IHotpotPlacement getPlacementInPos(int hitPos) {
        int i = getPlacementIndexInPos(hitPos);
        return i < 0 ? HotpotPlacements.getEmptyPlacementFactory().build() : placements.get(i);
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    public boolean isEmptyPlacement(int index) {
        return placements.get(index) instanceof HotpotEmptyPlacement;
    }

    public boolean isEmpty() {
        return placements.stream().allMatch(placement -> placement instanceof HotpotEmptyPlacement);
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
            HotpotPlacements.loadPlacements(compoundTag.getList("Placements", Tag.TAG_COMPOUND), registryAccess, placements);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        super.saveAdditional(compoundTag, registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        compoundTag.put("Placements", HotpotPlacements.savePlacements(placements, registryAccess));
    }

    public CompoundTag getUpdatePacketTag(BlockEntity blockEntity, HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        if (!contentChanged) {
            return compoundTag;
        }

        compoundTag.put("Placements", HotpotPlacements.savePlacements(placements, registryAccess));
        contentChanged = false;

        return compoundTag;
    }

    @NotNull
    @Override //Chunk Load
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = super.getUpdateTag(registryAccess);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);
        compoundTag.put("Placements", HotpotPlacements.savePlacements(placements, registryAccess));

        return compoundTag;
    }

    public static int getHitPos(BlockPos pos, Vec3 location) {
        BlockPos blockPos = pos.relative(Direction.UP);
        Vec3 vec = location.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return (vec.z() < 0.5 ? 0 : 1) | (vec.x() < 0.5 ? 0 : 2);
    }

    public List<IHotpotPlacement> getPlacements() {
        return placements;
    }

    public boolean isNotConflict(IHotpotPlacement another) {
        return placements.stream().noneMatch(plate -> another.getPoslist().stream().anyMatch(plate::isConflict));
    }

    public boolean isInfiniteContent() {
        return infiniteContent;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    public static int getHitPos(BlockHitResult result) {
        return getHitPos(result.getBlockPos(), result.getLocation());
    }

    public static int getHitPos(BlockPlaceContext context) {
        return getHitPos(context.getClickedPos(), context.getClickLocation());
    }

    public static int getHitPos(UseOnContext context) {
        return getHitPos(context.getClickedPos(), context.getClickLocation());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlacementBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
