package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class BlockEntityFinder<T extends BlockEntity> {
    private final BlockPosIterator iterator;
    private final Class<T> targetClass;

    public BlockEntityFinder(BlockPosWithLevel selfPos, Class<T> targetClass, BiPredicate<T, BlockPosWithLevel> filter) {
        this.targetClass = targetClass;

        this.iterator = new BlockPosIterator(selfPos, pos -> {
            BlockEntity blockEntity = pos.getBlockEntity();
            return this.targetClass.isInstance(blockEntity) && filter.test(this.targetClass.cast(blockEntity), pos);
        });
    }

    public Map<T, BlockPosWithLevel> getAll(BiPredicate<T, BlockPosWithLevel> predicate) {
        HashMap<T, BlockPosWithLevel> map = new HashMap<>();

        iterator.forEachRemaining(pos -> {
            T blockEntity = targetClass.cast(pos.getBlockEntity());

            if (predicate.test(blockEntity, pos)) {
                map.put(blockEntity, pos);
            }
        });

        return map;
    }

    public Map<T, BlockPosWithLevel> getAll() {
        return getAll((blockEntity, pos) -> true);
    }

    public void getFirst(int maximumBlocks, BiPredicate<T, BlockPosWithLevel> predicate, BiConsumer<T, BlockPosWithLevel> consumer) {
        int count = 0;

        while (iterator.hasNext() && (count ++) < maximumBlocks) {
            BlockPosWithLevel pos = iterator.next();

            T blockEntity = targetClass.cast(pos.getBlockEntity());

            if (predicate.test(blockEntity, pos)) {
                consumer.accept(blockEntity, pos);
                break;
            }
        }
    }
}
