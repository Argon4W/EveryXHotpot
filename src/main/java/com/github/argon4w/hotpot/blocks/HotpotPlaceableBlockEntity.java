package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.github.argon4w.hotpot.plates.IHotpotPlaceable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

import java.util.ArrayList;
import java.util.List;

public class HotpotPlateBlockEntity extends BlockEntity {
    private boolean contentChanged = true;
    private final List<IHotpotPlaceable> plates = new ArrayList<>(4);

    public HotpotPlateBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(HotpotModEntry.HOTPOT_PLATE_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    public void interact(int hitSlot, Player player, InteractionHand hand, ItemStack itemStack, BlockPosWithLevel selfPos) {
        IHotpotPlaceable plate = getPlateInSlot(hitSlot);

        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                ItemStack plateItemStack = plate.getCloneItemStack(this, selfPos);

                removePlate(hitSlot, selfPos);
                selfPos.dropItemStack(plateItemStack);

                if (plates.size() == 0) {
                    selfPos.level().removeBlock(selfPos.pos(), true);
                }

                markDataChanged();
            } else {
                ItemStack contentItemStack = plate.takeContent(hitSlot, this, selfPos);
                selfPos.dropItemStack(contentItemStack);
                markDataChanged();
            }
        } else if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            ItemStack chopstickFoodItemStack;

            if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack)).isEmpty()) {
                plate.placeContent(chopstickFoodItemStack, hitSlot, this, selfPos);
                itemStack.getTag().put("Item", chopstickFoodItemStack.save(new CompoundTag()));
                markDataChanged();
            } else {
                ItemStack foodItemStack = plate.takeContent(hitSlot, this, selfPos);

                if (!foodItemStack.isEmpty()) {
                    itemStack.getOrCreateTag().put("Item", foodItemStack.save(new CompoundTag()));
                }
                markDataChanged();
            }
        } else {
            plate.placeContent(itemStack, hitSlot, this, selfPos);
            markDataChanged();
        }
    }

    public boolean tryPlace(IHotpotPlaceable plate) {
        if (IHotpotPlaceable.canPlaceableFit(plates, plate)) {
            plates.add(plate);
            markDataChanged();

            return true;
        }

        return false;
    }

    public void removePlate(int hitSlot, BlockPosWithLevel pos) {
        IHotpotPlaceable plate = getPlateInSlot(hitSlot);
        plate.dropAllContent(this, pos);
        plates.remove(plate);
        markDataChanged();
    }

    public IHotpotPlaceable getPlateInSlot(int hitSlot) {
        return IHotpotPlaceable.getPlateWithSlot(plates, hitSlot).orElseGet(HotpotDefinitions.getEmptyPlaceable());
    }

    public void markDataChanged() {
        contentChanged = true;
        setChanged();
    }

    @Override //Load
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        IHotpotPlaceable.loadAll(compoundTag, plates);
    }

    @Override //Save
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        IHotpotPlaceable.saveAll(compoundTag, plates);
    }

    @Nullable
    @Override //Game Tick
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (entity) -> {
            CompoundTag compoundTag = new CompoundTag();

            if (contentChanged) {
                IHotpotPlaceable.saveAll(compoundTag, plates);

                contentChanged = false;
            }

            return compoundTag;
        });
    }

    @NotNull
    @Override //Chunk Load
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();
        IHotpotPlaceable.saveAll(compoundTag, plates);

        return compoundTag;
    }

    public List<IHotpotPlaceable> getPlates() {
        return plates;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HotpotPlateBlockEntity blockEntity) {
        if (blockEntity.contentChanged) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    public static int getHitSlot(BlockPos pos, Vec3 location) {
        BlockPos blockPos = pos.relative(Direction.UP);
        Vec3 vec = location.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return (vec.z() < 0.5 ? 0 : 1) | (vec.x() < 0.5 ? 0 : 2);
    }

    public static int getHitSlot(BlockHitResult result) {
        return getHitSlot(result.getBlockPos(), result.getLocation());
    }

    public static int getHitSlot(BlockPlaceContext context) {
        return getHitSlot(context.getClickedPos(), context.getClickLocation());
    }

    public static int getHitSlot(UseOnContext context) {
        return getHitSlot(context.getClickedPos(), context.getClickLocation());
    }
}
