package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    private @Final ResourceLocation location;

    @SuppressWarnings("deprecation")
    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> stitch(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            return contents;
        }

        ArrayList<SpriteContents> results = new ArrayList<>(contents);
        results.addAll(Util.sequence(HotpotSpriteProcessors.getSpriteProcessorRegistry().stream().flatMap(processor -> contents.stream().map(content -> CompletableFuture.supplyAsync(() -> getProcessedSpriteContents(processor, content)))).toList()).thenApply(l -> l).join());

        return results;
    }

    private static SpriteContents getProcessedSpriteContents(IHotpotSpriteProcessor processor, SpriteContents contents) {
        ResourceLocation name = contents.name();
        ResourceMetadata metadata = contents.metadata();
        NativeImage original = contents.getOriginalImage();
        FrameSize frameSize = new FrameSize(original.getWidth(), original.getHeight());
        NativeImage image = new NativeImage(contents.getOriginalImage().format(), contents.getOriginalImage().getWidth(), contents.getOriginalImage().getHeight(), true);

        processSpriteImage(original, image, frameSize, processor);

        return new SpriteContents(
                name.withSuffix(processor.getProcessedSuffix()),
                frameSize,
                image,
                metadata
        );
    }

    private static void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, IHotpotSpriteProcessor processor) {
        for (int i = 0; i < original.getHeight() / frameSize.height(); i ++) {
            processor.processSpriteImage(original, image, frameSize, i);
        }
    }
}
