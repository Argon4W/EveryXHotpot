package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    private @Final ResourceLocation location;

    private static final HashMap<ResourceLocation, SpriteContents> processedContents = new HashMap<>();

    @Inject(method = "loadSprite", at = @At("RETURN"))
    private static void loadSprite(ResourceLocation p_251630_, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
        if (cir.getReturnValue() == null) {
            return;
        }

        if (!cir.getReturnValue().name().getPath().startsWith("item/") && !cir.getReturnValue().name().getPath().startsWith("items/")) {
            return;
        }

        AnimationMetadataSection section;
        try {
            section = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
        } catch (Throwable throwable) {
            return;
        }

        SpriteContents content = cir.getReturnValue();
        NativeImage original = content.getOriginalImage();
        FrameSize frameSize = section.calculateFrameSize(original.getWidth(), original.getHeight());

        for (IHotpotSpriteProcessor processor : HotpotSpriteProcessors.getSpriteProcessorRegistry().getValues()) {
            if (processor instanceof HotpotEmptySpriteProcessor) {
                continue;
            }

            NativeImage image = new NativeImage(content.getOriginalImage().format(), content.getOriginalImage().getWidth(), content.getOriginalImage().getHeight(), true);

            processSpriteImage(original, image, frameSize, processor);

            processedContents.put(content.name().withSuffix(processor.getProcessedSuffix()), new SpriteContents(
                    content.name().withSuffix(processor.getProcessedSuffix()),
                    frameSize,
                    image,
                    section,
                    content.forgeMeta
            ));
        }
    }

    @SuppressWarnings("deprecation")
    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> stitch(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            ArrayList<SpriteContents> replacedContents = new ArrayList<>(contents);

            //pls, idea, don't be sad.
            for (SpriteContents newContents : processedContents.values()) {
                replacedContents.add(newContents);
            }

            return replacedContents;
        }

        return contents;
    }

    private static void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, IHotpotSpriteProcessor processor) {
        for (int i = 0; i < original.getHeight() / frameSize.height(); i ++) {
            processor.processSpriteImage(original, image, frameSize, i);
        }
    }
}
