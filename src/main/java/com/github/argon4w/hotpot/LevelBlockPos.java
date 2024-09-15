package com.github.argon4w.hotpot;

import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.google.common.base.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public record LevelBlockPos(Level level, BlockPos pos) {
    public BlockEntity getBlockEntity() {
        return level.getBlockEntity(pos);
    }

    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    public <T extends Comparable<T>, V extends T> void setBlockStateProperty(Property<T> property, V value) {
        setBlockState(getBlockState().setValue(property, value));
    }

    public void setBlockState(BlockState blockState) {
        level.setBlock(pos, blockState, 2);
    }

    public LevelChunk getChunkAt() {
        return level.getChunkAt(pos);
    }

    public LevelBlockPos updatePos(Function<BlockPos, BlockPos> function) {
        return new LevelBlockPos(level, function.apply(pos));
    }

    public void dropCopiedItemStacks(List<ItemStack> itemStacks) {
        itemStacks.stream().map(ItemStack::copy).forEach(this::dropItemStack);
    }

    public void dropItemStacks(List<ItemStack> itemStacks) {
        itemStacks.forEach(this::dropItemStack);
    }

    public void dropItemStack(ItemStack itemStack) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void dropCopiedFloatingItemStacks(List<ItemStack> itemStacks) {
        itemStacks.stream().map(ItemStack::copy).forEach(this::dropFloatingItemStack);
    }

    public void dropFloatingItemStacks(List<ItemStack> itemStacks) {
        itemStacks.forEach(this::dropFloatingItemStack);
    }

    public void dropFloatingItemStack(ItemStack itemStack) {
        dropFloatingItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public void markAndNotifyBlock() {
        level.markAndNotifyBlock(pos, getChunkAt(), getBlockState(), getBlockState(), 3, 512);
    }

    public void markAndNotifyClient() {
        level.markAndNotifyBlock(pos, getChunkAt(), getBlockState(), getBlockState(), 2, 512);
    }

    public void playSound(Holder<SoundEvent> soundEventHolder) {
        playSound(soundEventHolder, 1.0f, 1.0f);
    }

    public void playSound(Holder<SoundEvent> soundEventHolder, float volume, float pitch) {
        playSound(soundEventHolder.value(), volume, pitch);
    }

    public void playSound(SoundEvent soundEvent) {
        playSound(soundEvent, 1.0f, 1.0f);
    }

    public void playSound(SoundEvent soundEvent, float volume, float pitch) {
        playSound(soundEvent, SoundSource.BLOCKS, volume, pitch);
    }

    public void playSound(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        level.playSound(null, pos, soundEvent, soundSource, volume, pitch);
    }

    public void addParticle(ParticleType<?> type, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        level.addParticle((ParticleOptions) type, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    public void removeBlock(boolean isMoving) {
        level.removeBlock(pos, isMoving);
    }

    public RegistryAccess registryAccess() {
        return level.registryAccess();
    }

    public RecipeManager getRecipeManager() {
        return level.getRecipeManager();
    }

    public LootTable getLootTable(ResourceKey<LootTable> lootTableKey) {
        return level.getServer().reloadableRegistries().getLootTable(lootTableKey);
    }

    public Vec3 toVec3() {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public SoundType getSoundType(Entity entity) {
        return getBlockState().getSoundType(level, pos, entity);
    }

    public RandomSource getRandomSource() {
        return level.random;
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

    public LevelBlockPos relative(ComplexDirection direction) {
        return updatePos(pos -> direction.reduce(pos, BlockPos::relative));
    }

    public boolean is(Block block) {
        return getBlockState().is(block);
    }

    public boolean isAir() {
        return getBlockState().isAir();
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
