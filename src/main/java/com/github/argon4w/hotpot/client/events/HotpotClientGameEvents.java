package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.sections.AdditionalSectionGeometryBlockEntityRendererDispatcher;
import net.minecraft.client.renderer.Sheets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientGameEvents {
    @SubscribeEvent
    public static void addSectionGeometry(AddSectionGeometryEvent event) {
        event.addRenderer(new AdditionalSectionGeometryBlockEntityRendererDispatcher(event.getSectionOrigin().immutable()));
    }

    @SubscribeEvent
    public static void onRenderSectionRenderType(RenderLevelStageEvent event)
    {
        if (ModList.get().isLoaded("sodium")) {
            return;
        }

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Vector3f position = event.getCamera().getPosition().toVector3f();
        event.getLevelRenderer().renderSectionLayer(Sheets.translucentItemSheet(), position.x, position.y, position.z, event.getModelViewMatrix().translate(position.negate()), event.getProjectionMatrix());
        event.getLevelRenderer().renderBuffers.bufferSource().endBatch(Sheets.translucentItemSheet());
    }
}