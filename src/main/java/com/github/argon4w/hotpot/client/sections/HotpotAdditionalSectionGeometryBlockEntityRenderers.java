package com.github.argon4w.hotpot.client.sections;

import com.github.argon4w.hotpot.client.blocks.IHotpotSectionGeometryBLockEntityRenderer;
import com.github.argon4w.hotpot.client.sections.cache.HotpotModelCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record HotpotAdditionalSectionGeometryBlockEntityRenderers(BlockPos regionOrigin) implements AddSectionGeometryEvent.AdditionalSectionRenderer {
    public static final Map<IHotpotSectionGeometryBLockEntityRenderer<?>, HotpotModelCache> CACHE = new ConcurrentHashMap<>();

    @Override
    public void render(AddSectionGeometryEvent.SectionRenderingContext context) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int index = 0; index < 16 * 16 * 16; index ++) {
            int factor = index / 16;
            renderAt(cursor.set(regionOrigin.getX() + index % 16, regionOrigin.getY() + factor % 16, regionOrigin.getZ() + factor / 16), context);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> T cast(BlockEntity o) {
        return (T) o;
    }

    public HotpotModelCache getModelCache(IHotpotSectionGeometryBLockEntityRenderer<?> renderer) {
        return CACHE.computeIfAbsent(renderer, renderer1 -> new HotpotModelCache());
    }

    public void renderAt(BlockPos pos, AddSectionGeometryEvent.SectionRenderingContext context) {
        BlockEntity blockEntity = context.getRegion().getBlockEntity(pos);

        if (blockEntity == null) {
            return;
        }

        if (!(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof IHotpotSectionGeometryBLockEntityRenderer<?> renderer)) {
            return;
        }

        context.getPoseStack().pushPose();
        context.getPoseStack().translate(pos.getX() - regionOrigin.getX(), pos.getY() - regionOrigin.getY(), pos.getZ() - regionOrigin.getZ());

        try {
            renderer.renderSectionGeometry(cast(blockEntity), context, new PoseStack(), pos, new HotpotSectionGeometryModelRenderer(context, getModelCache(renderer), pos));
        } catch (ClassCastException ignored) {

        }

        context.getPoseStack().popPose();
    }
}
