package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.placeables.HotpotEmptyPlaceable;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotpotPlaceableBlockEntity extends AbstractChopstickInteractiveBlockEntity {
    private boolean contentChanged = true;
    private final NonNullList<IHotpotPlaceable> placeables = NonNullList.withSize(4, HotpotPlaceables.getEmptyPlaceable().get());

    public HotpotPlaceableBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLACEABLE_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public ItemStack tryPlaceContentViaChopstick(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        tryPlaceContentViaInteraction(hitSection, player, hand, itemStack, selfPos);

        return itemStack;
    }

    @Override
    public void tryPlaceContentViaInteraction(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
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
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (compoundTag.contains("Placeables", Tag.TAG_LIST)) {
            placeables.clear();
            IHotpotPlaceable.loadAll(compoundTag.getList("Placeables", Tag.TAG_COMPOUND), placeables);
        }
    }

    @Override //Save
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));
    }

    @Nullable
    @Override //Game Tick
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (entity) -> {
            CompoundTag compoundTag = new CompoundTag();

            if (contentChanged) {
                compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));

                contentChanged = false;
            }

            return compoundTag;
        });
    }

    @NotNull
    @Override //Chunk Load
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();
        compoundTag.put("Placeables", IHotpotPlaceable.saveAll(placeables));

        return compoundTag;
    }

    public List<IHotpotPlaceable> getPlaceables() {
        return placeables;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlaceableBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public boolean canPlaceableFit(IHotpotPlaceable plate2) {
        return placeables.stream().noneMatch(plate -> plate2.getPos().stream().anyMatch(plate::isConflict));
    }

    public static int getHitPos(BlockPos pos, Vec3 location) {
        BlockPos blockPos = pos.relative(Direction.UP);
        Vec3 vec = location.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return (vec.z() < 0.5 ? 0 : 1) | (vec.x() < 0.5 ? 0 : 2);
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
}
