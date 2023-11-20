package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.placeables.HotpotEmptyPlaceable;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class HotpotPlaceableBlockEntity extends AbstractChopstickInteractiveBlockEntity implements ITickableTileEntity {
    private boolean contentChanged = true;
    private final NonNullList<IHotpotPlaceable> placeables = NonNullList.withSize(4, HotpotPlaceables.getEmptyPlaceable().get());
    private boolean infiniteContent = false;
    private boolean canBeRemoved = true;

    public HotpotPlaceableBlockEntity() {
        super(HotpotModEntry.HOTPOT_PLACEABLE_BLOCK_ENTITY.get());
    }

    @Override
    public ItemStack tryPlaceContentViaChopstick(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        if (isEmpty()) {
            selfPos.level().removeBlock(selfPos.pos(), true);
        }

        IHotpotPlaceable placeable = getPlaceableInPos(hitSection);
        placeable.interact(player, hand, itemStack, hitSection, this, selfPos);
        markDataChanged();
    }

    @Override
    public ItemStack tryTakeOutContentViaChopstick(int hitSection, BlockPosWithLevel pos) {
        if (isEmpty()) {
            pos.level().removeBlock(pos.pos(), true);
        }

        IHotpotPlaceable placeable = getPlaceableInPos(hitSection);
        ItemStack itemStack = placeable.takeOutContent(hitSection, this, pos);
        markDataChanged();

        return itemStack;
    }

    public void tryTakeOutContentViaHand(int hitSection, BlockPosWithLevel pos) {
        pos.dropItemStack(tryTakeOutContentViaChopstick(hitSection, pos));
    }

    public void tryRemove(IHotpotPlaceable placeable, BlockPosWithLevel pos) {
        if (!canBeRemoved()) {
            return;
        }

        tryRemove(placeable.getAnchorPos(), pos);
    }

    public void tryRemove(int hitSection, BlockPosWithLevel pos) {
        IHotpotPlaceable placeable = getPlaceableInPos(hitSection);

        if (!(placeable instanceof HotpotEmptyPlaceable)) {
            placeable.onRemove(this, pos);
            pos.dropItemStack(placeable.getCloneItemStack(this, pos));
            placeables.set(placeable.getAnchorPos(), HotpotPlaceables.getEmptyPlaceable().get());

            markDataChanged();
        }

        if (isEmpty()) {
            pos.level().removeBlock(pos.pos(), true);
        }
    }

    public boolean isEmpty() {
        return placeables.stream().allMatch(placeable -> placeable instanceof HotpotEmptyPlaceable);
    }

    public boolean tryPlace(IHotpotPlaceable placeable) {
        if (canPlaceableFit(placeable)) {
            IHotpotPlaceable toReplace = placeables.get(placeable.getAnchorPos());

            if (toReplace instanceof HotpotEmptyPlaceable) {
                placeables.set(placeable.getAnchorPos(), placeable);
                markDataChanged();

                return true;
            }
        }

        return false;
    }

    public void onRemove(BlockPosWithLevel pos) {
        for (int i = 0; i < placeables.size(); i ++) {
            IHotpotPlaceable placeable = placeables.get(i);

            placeable.onRemove(this, pos);
            pos.dropItemStack(placeable.getCloneItemStack(this, pos));

            placeables.set(i, HotpotPlaceables.getEmptyPlaceable().get());
        }

        markDataChanged();
    }

    public IHotpotPlaceable getPlaceableInPos(int hitPos) {
        return placeables.stream().filter(plate -> plate.getPos().contains(hitPos)).findFirst().orElseGet(HotpotPlaceables.getEmptyPlaceable());
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    @Override //Load
    public void load(BlockState p_230337_1_, CompoundNBT compoundTag) {
        super.load(p_230337_1_, compoundTag);
        handleLoadTag(compoundTag);
    }

    public void handleLoadTag(CompoundNBT compoundTag) {
        canBeRemoved = !compoundTag.contains("CanBeRemoved", Constants.NBT.TAG_ANY_NUMERIC) || compoundTag.getBoolean("CanBeRemoved");
        infiniteContent = compoundTag.contains("InfiniteContent", Constants.NBT.TAG_ANY_NUMERIC) && compoundTag.getBoolean("InfiniteContent");

        if (compoundTag.contains("Placeables", Constants.NBT.TAG_LIST)) {
            placeables.clear();
            IHotpotPlaceable.loadAll(compoundTag.getList("Placeables", Constants.NBT.TAG_COMPOUND), placeables);
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleLoadTag(pkt.getTag());
    }

    @Override //Save
    public CompoundNBT save(CompoundNBT compoundTag) {
        super.save(compoundTag);

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));

        return compoundTag;
    }

    @Override //Game Tick
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compoundTag = new CompoundNBT();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);

        if (contentChanged) {
            compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));

            contentChanged = false;
        }

        return new SUpdateTileEntityPacket(getBlockPos(), 1, compoundTag);
    }

    @Override //Chunk Load
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundTag = super.getUpdateTag();

        compoundTag.putBoolean("CanBeRemoved", canBeRemoved);
        compoundTag.putBoolean("InfiniteContent", infiniteContent);
        compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));

        return compoundTag;
    }

    public List<IHotpotPlaceable> getPlaceables() {
        return placeables;
    }

    @Override
    public void tick() {
        if (level.isClientSide) return;

        if (contentChanged) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public boolean canPlaceableFit(IHotpotPlaceable plate2) {
        return placeables.stream().noneMatch(plate -> plate2.getPos().stream().anyMatch(plate::isConflict));
    }

    public boolean isInfiniteContent() {
        return infiniteContent;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    public static int getHitPos(BlockPos pos, Vector3d location) {
        BlockPos blockPos = pos.relative(Direction.UP);
        Vector3d vec = location.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return (vec.z() < 0.5 ? 0 : 1) | (vec.x() < 0.5 ? 0 : 2);
    }

    public static int getHitPos(BlockRayTraceResult result) {
        return getHitPos(result.getBlockPos(), result.getLocation());
    }

    public static int getHitPos(ItemUseContext context) {
        return getHitPos(context.getClickedPos(), context.getClickLocation());
    }
}
