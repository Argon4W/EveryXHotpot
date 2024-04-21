package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.client.contents.player.HotpotPlayerModelRenderContext;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Math;

import java.util.HashMap;

public class HotpotEmptyContentRenderer implements IHotpotContentRenderer {
    @Override
    public void render(IHotpotContent content, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float rotation, float waterLevel, float x, float z) {

    }
}
