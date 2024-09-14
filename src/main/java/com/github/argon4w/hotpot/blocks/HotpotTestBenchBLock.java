package com.github.argon4w.hotpot.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HotpotTestBenchBLock extends BaseEntityBlock {
    public HotpotTestBenchBLock() {
        super(Properties.of().noCollission().noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotTestBenchBLock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HotpotTestBenchBlockEntity(pPos, pState);
    }
}
