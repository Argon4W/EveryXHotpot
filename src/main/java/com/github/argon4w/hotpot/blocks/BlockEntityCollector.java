package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.LevelBlockPos;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.stream.Streams;

public class BlockEntityCollector<T extends BlockEntity> {
    private final BlockEntityType<T> type;
    ;
    private final BiPredicate<T, LevelBlockPos> filter;
    private final BlockPosIterator iterator;

    public BlockEntityCollector(LevelBlockPos pos, BlockEntityType<T> type, BiPredicate<T, LevelBlockPos> filter) {
        this.type = type;
        this.filter = filter;
        this.iterator = new BlockPosIterator(pos, this::filter);
    }

    public BlockEntityCollector(
            LevelBlockPos pos, Supplier<BlockEntityType<T>> holder, BiPredicate<T, LevelBlockPos> filter) {
        this(pos, holder.get(), filter);
    }

    private boolean filter(LevelBlockPos pos) {
        T blockEntity = pos.getBlockEntity(type);
        return blockEntity != null && filter.test(blockEntity, pos);
    }

    public void getFirst(int maximumBlocks, Predicate<T> filter, BiConsumer<T, LevelBlockPos> consumer) {
        getFirst(maximumBlocks, (t, pos) -> filter.test(t), consumer);
    }

    public void getFirst(
            int maximumBlocks, BiPredicate<T, LevelBlockPos> filter, BiConsumer<T, LevelBlockPos> consumer) {
        Streams.of(new SizedIterator<>(iterator, maximumBlocks))
                .map(EntryStreams.create(pos -> pos.getBlockEntity(type)))
                .filter(EntryStreams.filterEntryValue(Objects::nonNull))
                .map(EntryStreams.swap())
                .filter(EntryStreams.filterEntry(filter))
                .findFirst()
                .ifPresent(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public Stream<Map.Entry<T, LevelBlockPos>> stream() {
        return Streams.of(iterator)
                .map(EntryStreams.create(pos -> pos.getBlockEntity(type)))
                .filter(EntryStreams.filterEntryValue(Objects::nonNull))
                .map(EntryStreams.swap());
    }
}
