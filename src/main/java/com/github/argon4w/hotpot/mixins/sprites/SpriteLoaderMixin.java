package com.github.argon4w.hotpot.mixins.sprites;

import com.github.argon4w.hotpot.client.items.sprites.SimpleModelBaker;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotEmptySpriteProcessor;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.sprites.processors.IHotpotSpriteProcessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow @Final private ResourceLocation location;

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true, index = 1)
    private List<SpriteContents> stitch(List<SpriteContents> contents) {
        if (!location.equals(InventoryMenu.BLOCK_ATLAS)) {
            return contents;
        }

        ArrayList<SpriteContents> results = new ArrayList<>(contents);
        List<SpriteContents> processedContents = Util.sequence(HotpotSpriteProcessors.getSpriteProcessorRegistry().stream().filter(processor -> !(processor instanceof HotpotEmptySpriteProcessor)).flatMap(processor -> contents.stream().filter(content -> content.name().getPath().startsWith("item/") && content.animatedTexture == null).map(content -> CompletableFuture.supplyAsync(() -> everyxhotpot$getProcessedSpriteContents(processor, content)))).toList()).join();

        results.addAll(processedContents);
        SimpleModelBaker.VALID_PROCESSED_SPRITES.clear();
        SimpleModelBaker.VALID_PROCESSED_SPRITES.addAll(processedContents.stream().map(SpriteContents::name).toList());

        return results;
    }

    @Unique
    private static SpriteContents everyxhotpot$getProcessedSpriteContents(IHotpotSpriteProcessor processor, SpriteContents contents) {
        ResourceLocation name = contents.name();
        ResourceMetadata metadata = contents.metadata();
        NativeImage original = contents.getOriginalImage();
        FrameSize frameSize = metadata.getSection(AnimationMetadataSection.SERIALIZER).map(section -> section.calculateFrameSize(original.getWidth(), original.getHeight())).orElse(new FrameSize(original.getWidth(), original.getHeight()));
        NativeImage image = new NativeImage(contents.getOriginalImage().format(), contents.getOriginalImage().getWidth(), contents.getOriginalImage().getHeight(), true);

        everyxhotpot$processSpriteImage(original, image, frameSize, processor);
        return new SpriteContents(name.withSuffix(processor.getSuffix()), frameSize, image, metadata);
    }

    @Unique
    private static void everyxhotpot$processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, IHotpotSpriteProcessor processor) {
        for (int i = 0; i < original.getHeight() / frameSize.height(); i ++) {
            processor.processSpriteImage(original, image, frameSize, i);
        }
    }
}
