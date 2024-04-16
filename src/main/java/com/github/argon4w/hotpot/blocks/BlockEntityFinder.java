package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

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
        HashMap<T, LevelBlockPos> map = new HashMap<>();

        while (iterator.hasNext()) {
            LevelBlockPos pos = iterator.next();
            T blockEntity = targetClass.cast(pos.getBlockEntity());

            map.put(blockEntity, pos);
        }

        return map;
    }

    public void getFirst(int maximumBlocks, BiPredicate<T, LevelBlockPos> predicate, BiConsumer<T, LevelBlockPos> consumer) {
        int count = 0;

        while (iterator.hasNext() && (count ++) < maximumBlocks) {
            LevelBlockPos pos = iterator.next();

            T blockEntity = targetClass.cast(pos.getBlockEntity());

            if (predicate.test(blockEntity, pos)) {
                consumer.accept(blockEntity, pos);
                break;
            }
        }
    }
}
