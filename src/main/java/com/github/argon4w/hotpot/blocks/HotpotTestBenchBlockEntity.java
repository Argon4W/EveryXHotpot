package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HotpotTestBenchBlockEntity extends BlockEntity {
    public HotpotTestBenchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(HotpotModEntry.HOTPOT_TEST_BENCH_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
