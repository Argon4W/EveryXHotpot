package com.github.argon4w.hotpot.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HotpotTestBenchBlock extends BaseEntityBlock {
    public HotpotTestBenchBlock() {
        super(Properties.of().noCollission().noOcclusion());
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(HotpotTestBenchBlock::new);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new HotpotTestBenchBlockEntity(pPos, pState);
    }
}
