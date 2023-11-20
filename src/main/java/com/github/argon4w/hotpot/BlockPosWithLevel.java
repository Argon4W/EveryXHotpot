package com.github.argon4w.hotpot;

import com.google.common.base.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.function.Function;

public class BlockPosWithLevel {
    private final World level;
    private final BlockPos pos;

    public BlockPosWithLevel(World level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public World level() {
        return level;
    }

    public BlockPos pos() {
        return pos;
    }

    public TileEntity getBlockEntity() {
        return level.getBlockEntity(pos);
    }

    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    public Chunk getChunkAt() {
        return level.getChunkAt(pos);
    }

    public <T> T mapPos(Function<BlockPos, T> function) {
        return function.apply(pos);
    }

    public BlockPosWithLevel updatePos(Function<BlockPos, BlockPos> function) {
        return new BlockPosWithLevel(level, function.apply(pos));
    }

    public void dropItemStack(ItemStack itemStack) {
        InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void markAndNotifyBlock() {
        level.markAndNotifyBlock(pos, getChunkAt(), getBlockState(), getBlockState(), 3, 512);
    }

    public Vector3d toVec3() {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public SoundType getSoundType(Entity entity) {
        return getBlockState().getSoundType(level, pos, entity);
    }

    public boolean isServerSide() {
        return !level.isClientSide;
    }

    public BlockPosWithLevel north() {
        return updatePos(BlockPos::north);
    }

    public BlockPosWithLevel south() {
        return updatePos(BlockPos::south);
    }

    public BlockPosWithLevel east() {
        return updatePos(BlockPos::east);
    }

    public BlockPosWithLevel west() {
        return updatePos(BlockPos::west);
    }

    public boolean is(Block block) {
        return getBlockState().is(block);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosWithLevel pos1 = (BlockPosWithLevel) o;
        return Objects.equal(level, pos1.level) && Objects.equal(pos, pos1.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(level, pos);
    }

    public class Builder {
        private final World level;

        public Builder(World level) {
            this.level = level;
        }

        public BlockPosWithLevel of(BlockPos pos) {
            return new BlockPosWithLevel(level, pos);
        }
    }

    public static BlockPosWithLevel fromVec3(World level, Vector3d vec) {
        return new BlockPosWithLevel(level, new BlockPos((int) vec.x, (int) vec.y, (int) vec.z));
    }

    public static BlockPosWithLevel fromUseOnContext(ItemUseContext context) {
        return new BlockPosWithLevel(context.getLevel(), context.getClickedPos());
    }

    public static BlockPosWithLevel fromBlockPlaceContext(BlockItemUseContext context) {
        return new BlockPosWithLevel(context.getLevel(), context.getClickedPos());
    }
}
