package com.github.argon4w.hotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public record BlockPosWithLevel(Level level, BlockPos pos) {
    public BlockEntity getBlockEntity() {
        return level.getBlockEntity(pos);
    }

    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    public LevelChunk getChunkAt() {
        return level.getChunkAt(pos);
    }

    public <T> T mapPos(Function<BlockPos, T> function) {
        return function.apply(pos);
    }

    public BlockPosWithLevel updatePos(Function<BlockPos, BlockPos> function) {
        return new BlockPosWithLevel(level, function.apply(pos));
    }

    public void dropItemStack(ItemStack itemStack) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void markAndNotifyBlock() {
        level.markAndNotifyBlock(pos, getChunkAt(), getBlockState(), getBlockState(), 3, 512);
    }

    public Vec3 toVec3() {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
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

    public record Builder(Level level) {
        public BlockPosWithLevel of(BlockPos pos) {
            return new BlockPosWithLevel(level, pos);
        }
    }
}