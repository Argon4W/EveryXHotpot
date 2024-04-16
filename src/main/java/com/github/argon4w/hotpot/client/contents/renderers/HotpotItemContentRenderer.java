package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Math;

import java.util.Optional;

public class HotpotItemContentRenderer implements IHotpotContentRenderer {
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.315f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    @Override
    public void render(IHotpotContent content, BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        if (!(content instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return;
        }

        poseStack.pushPose();

        float f = hotpotBlockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        context.getItemRenderer().renderStatic(null, itemStackContent.getItemStack(), ItemDisplayContext.FIXED, true, poseStack, bufferSource, hotpotBlockEntity.getLevel(), combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());
        poseStack.popPose();
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
