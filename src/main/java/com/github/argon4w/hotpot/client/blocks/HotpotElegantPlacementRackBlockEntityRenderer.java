package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotElegantPlacementRackBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlacementRackBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;

public class HotpotElegantPlacementRackBlockEntityRenderer implements BlockEntityRenderer<HotpotElegantPlacementRackBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotElegantPlacementRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotElegantPlacementRackBlockEntity hotpotElegantPlacementRackBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        LevelBlockPos pos = new LevelBlockPos(hotpotElegantPlacementRackBlockEntity.getLevel(), hotpotElegantPlacementRackBlockEntity.getBlockPos());

        hotpotElegantPlacementRackBlockEntity.getPlacements0().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, pos)));

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.1875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, pos)));

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.75f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, pos)));

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotElegantPlacementRackBlockEntity hotpotElegantPlacementRackBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(HotpotElegantPlacementRackBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
