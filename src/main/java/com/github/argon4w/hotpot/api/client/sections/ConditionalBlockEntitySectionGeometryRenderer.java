package com.github.argon4w.hotpot.api.client.sections;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface ConditionalBlockEntitySectionGeometryRenderer<T extends BlockEntity> extends IBlockEntitySectionGeometryRenderer<T> {
    boolean shouldRender(T blockEntity, BlockPos blockPos, BlockPos regionOrigin, Vec3 cameraPos);
}
