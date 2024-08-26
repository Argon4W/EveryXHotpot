package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.stream.Streams;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class BlockEntityFinder<T extends BlockEntity> {
    private final BlockPosIterator iterator;
    private final Class<T> targetClass;
    private final BiPredicate<T, LevelBlockPos> filter;

    public BlockEntityFinder(LevelBlockPos selfPos, Class<T> targetClass, BiPredicate<T, LevelBlockPos> filter) {
        this.targetClass = targetClass;
        this.filter = filter;
        this.iterator = new BlockPosIterator(selfPos, this::checkTargetBlock);
    }

    private boolean checkTargetBlock(LevelBlockPos pos) {
        BlockEntity blockEntity = pos.getBlockEntity();
        return this.targetClass.isInstance(blockEntity) && filter.test(this.targetClass.cast(blockEntity), pos);
    }

    public Map<T, LevelBlockPos> getAll() {
        return Streams.of(iterator).map(pos -> Map.entry(targetClass.cast(pos.getBlockEntity()), pos)).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void getFirst(int maximumBlocks, BiPredicate<T, LevelBlockPos> predicate, BiConsumer<T, LevelBlockPos> consumer) {
        Streams.of(new SizedIterator<>(iterator, maximumBlocks)).map(pos -> Map.entry(targetClass.cast(pos.getBlockEntity()), pos)).filter(entry -> predicate.test(entry.getKey(), entry.getValue())).findFirst().ifPresent(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }
}
