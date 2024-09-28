package com.github.argon4w.hotpot.client.sections;

import com.github.argon4w.hotpot.api.client.sections.ConditionalBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.api.client.sections.IBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.api.client.sections.cache.ICustomRendererBakedModelsCacheProvider;
import com.github.argon4w.hotpot.client.sections.cache.DefaultRendererBakedModelsCache;
import com.github.argon4w.hotpot.api.client.sections.cache.RendererBakedModelsCache;
import com.github.argon4w.hotpot.client.sections.cache.UncachedRendererBakedModelsCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Argon4W
 */
public record AdditionalSectionGeometryBlockEntityRendererDispatcher(BlockPos regionOrigin) implements AddSectionGeometryEvent.AdditionalSectionRenderer {
    public static final Map<IBlockEntitySectionGeometryRenderer<?>, RendererBakedModelsCache> CACHE = new ConcurrentHashMap<>();

    @Override
    public void render(@NotNull AddSectionGeometryEvent.SectionRenderingContext context) {
        BlockPos.betweenClosed(regionOrigin, regionOrigin.offset(16, 16, 16)).forEach(pos -> renderAt(pos, context));
    }

    public RendererBakedModelsCache getOrCreateCache(IBlockEntitySectionGeometryRenderer<?> renderer) {
        return CACHE.compute(renderer, (renderer1, cache) -> cache == null ? createCache(renderer1) : (cache.getSize() > 128 ? new UncachedRendererBakedModelsCache() : cache));
    }

    public RendererBakedModelsCache createCache(IBlockEntitySectionGeometryRenderer<?> renderer) {
        return renderer instanceof ICustomRendererBakedModelsCacheProvider provider ? provider.createCache() : new DefaultRendererBakedModelsCache();
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> T cast(BlockEntity o) {
        return (T) o;
    }

    public void renderAt(BlockPos pos, AddSectionGeometryEvent.SectionRenderingContext context) {
        BlockEntity blockEntity = context.getRegion().getBlockEntity(pos);

        if (blockEntity == null) {
            return;
        }

        if (!(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof IBlockEntitySectionGeometryRenderer<?> renderer)) {
            return;
        }

        if (renderer instanceof ConditionalBlockEntitySectionGeometryRenderer<?> conditional && !conditional.shouldRender(cast(blockEntity), pos, regionOrigin, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition())) {
            return;
        }

        context.getPoseStack().pushPose();
        context.getPoseStack().translate(pos.getX() - regionOrigin.getX(), pos.getY() - regionOrigin.getY(), pos.getZ() - regionOrigin.getZ());

        try {
            renderer.renderSectionGeometry(cast(blockEntity), context, new PoseStack(), pos, regionOrigin, new LightAwareSectionGeometryRenderContext(context, getOrCreateCache(renderer), pos, regionOrigin));
        } catch (ClassCastException ignored) {

        }

        context.getPoseStack().popPose();
    }
}
