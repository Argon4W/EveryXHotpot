package com.github.argon4w.hotpot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Math;

public class HotpotEmptyContent implements IHotpotContent {
    public HotpotEmptyContent() {}

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset) {}

    @Override
    public void dropContent(Level level, BlockPos pos) {}

    @Override
    public boolean tick(HotpotBlockEntity blockEntity, Level level, BlockPos pos) {
        return false;
    }

    @Override
    public void load(CompoundTag tag) {
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    @Override
    public boolean isValid(CompoundTag tag) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }
}
