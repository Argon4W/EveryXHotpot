package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface IHotpotContent {
    void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset);
    boolean tick(HotpotBlockEntity blockEntity, Level level, BlockPos pos);
    void dropContent(Level level, BlockPos pos);
    void load(CompoundTag tag);
    CompoundTag save(CompoundTag tag);

    boolean isValid(CompoundTag tag);
    String getID();
}
