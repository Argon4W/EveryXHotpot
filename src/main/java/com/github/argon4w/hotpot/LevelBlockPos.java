package com.github.argon4w.hotpot;

import com.google.common.base.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public record LevelBlockPos(Level level, BlockPos pos) {
    public BlockEntity getBlockEntity() {
        return level.getBlockEntity(pos);
    }

    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    public void setBlockState(BlockState blockState) {
        level.setBlock(pos, blockState, 2);
    }

    public LevelChunk getChunkAt() {
        return level.getChunkAt(pos);
    }

    public <T> T mapPos(Function<BlockPos, T> function) {
        return function.apply(pos);
    }

    public LevelBlockPos updatePos(Function<BlockPos, BlockPos> function) {
        return new LevelBlockPos(level, function.apply(pos));
    }

    public void dropItemStack(ItemStack itemStack) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void dropFloatingItemStack(ItemStack itemStack) {
        dropFloatingItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void markAndNotifyBlock() {
        level.markAndNotifyBlock(pos, getChunkAt(), getBlockState(), getBlockState(), 3, 512);
    }

    public RegistryAccess registryAccess() {
        return level.registryAccess();
    }

    public Vec3 toVec3() {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public SoundType getSoundType(Entity entity) {
        return getBlockState().getSoundType(level, pos, entity);
    }

    public boolean isServerSide() {
        return !level.isClientSide;
    }

    public LevelBlockPos north() {
        return updatePos(BlockPos::north);
    }

    public LevelBlockPos south() {
        return updatePos(BlockPos::south);
    }

    public LevelBlockPos east() {
        return updatePos(BlockPos::east);
    }

    public LevelBlockPos west() {
        return updatePos(BlockPos::west);
    }

    public boolean is(Block block) {
        return getBlockState().is(block);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LevelBlockPos pos1 = (LevelBlockPos) o;
        return Objects.equal(level, pos1.level) && Objects.equal(pos, pos1.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(level, pos);
    }

    public static LevelBlockPos fromVec3(Level level, Vec3 vec) {
        return new LevelBlockPos(level, new BlockPos((int) vec.x, (int) vec.y, (int) vec.z));
    }

    public static LevelBlockPos fromUseOnContext(UseOnContext context) {
        return new LevelBlockPos(context.getLevel(), context.getClickedPos());
    }

    public static LevelBlockPos fromBlockPlaceContext(BlockPlaceContext context) {
        return new LevelBlockPos(context.getLevel(), context.getClickedPos());
    }

    public static void dropFloatingItemStack(Level level, double x, double y, double z, ItemStack itemStack) {
        double halfWidth = EntityType.ITEM.getWidth() / 2.0d;

        x = Math.floor(x) + 0.5d + halfWidth * level.random.nextGaussian();
        y = Math.floor(y) + 0.5d;
        z = Math.floor(z) + 0.5d + halfWidth * level.random.nextGaussian();

        ItemEntity itementity = new ItemEntity(level, x, y, z, itemStack);

        itementity.setNoGravity(true);
        itementity.setPickUpDelay(40);
        itementity.setDeltaMovement(0.0d, 0.025, 0.0d);

        level.addFreshEntity(itementity);
    }
}
