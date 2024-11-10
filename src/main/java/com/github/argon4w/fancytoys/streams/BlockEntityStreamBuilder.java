package com.github.argon4w.fancytoys.streams;

import com.github.argon4w.fancytoys.BlockPosIterator;
import com.github.argon4w.fancytoys.LevelBlockPos;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityStreamBuilder<T extends BlockEntity> {
    private final BlockEntityType<T> type;
    private Predicate<LevelBlockPos> filter;

    private BlockEntityStreamBuilder(BlockEntityType<T> type) {
        this.type = type;
        this.filter = pos -> pos.isBlockEntity(type);
    }

    private T mapper(LevelBlockPos pos) {
        return pos.getBlockEntity(type);
    }

    public BlockEntityStreamBuilder<T> filterPos(Predicate<LevelBlockPos> filter) {
        this.filter = this.filter.and(filter);
        return this;
    }

    public BlockEntityStreamBuilder<T> filter(Predicate<T> filter) {
        this.filter = this.filter.and(pos -> filter.test(pos.getBlockEntity(type)));
        return this;
    }

    public BlockEntityStreamBuilder<T> filterNot(Predicate<T> filter) {
        this.filter = this.filter.and(pos -> !filter.test(pos.getBlockEntity(type)));
        return this;
    }

    public BlockEntityStreamBuilder<T> filter(BiPredicate<T, LevelBlockPos> filter) {
        this.filter = this.filter.and(pos -> filter.test(pos.getBlockEntity(type), pos));
        return this;
    }

    public BlockPosIterator buildIterator(LevelBlockPos pos) {
        return new BlockPosIterator(pos, filter);
    }

    public EntryStream<T, LevelBlockPos> build(LevelBlockPos pos) {
        return EntryStream.fromValues(buildIterator(pos), this::mapper).nonNullKey();
    }

    public static <T extends BlockEntity> BlockEntityStreamBuilder<T> of(BlockEntityType<T> type) {
        return new BlockEntityStreamBuilder<>(type);
    }

    public static <T extends BlockEntity> BlockEntityStreamBuilder<T> of(Supplier<BlockEntityType<T>> holder) {
        return of(holder.get());
    }
}
