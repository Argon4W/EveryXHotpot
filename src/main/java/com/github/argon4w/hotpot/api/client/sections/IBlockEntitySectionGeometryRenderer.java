package com.github.argon4w.hotpot.api.client.sections;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public interface IBlockEntitySectionGeometryRenderer<T extends BlockEntity> {
    void renderSectionGeometry(
            T blockEntity,
            AddSectionGeometryEvent.SectionRenderingContext context,
            PoseStack poseStack,
            BlockPos pos,
            BlockPos regionOrigin,
            ISectionGeometryRenderContext renderAndCacheContext);
}
