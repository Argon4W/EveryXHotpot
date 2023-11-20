package com.github.argon4w.hotpot.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import org.joml.Math;
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
import java.util.Map;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow
    private @Final ResourceLocation location;

    @SuppressWarnings("deprecation")
    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> stitch(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            ArrayList<SpriteContents> replacedContents = new ArrayList<>(contents);

            for (SpriteContents content : contents) {
                if (!content.name().getPath().contains("item/")) continue;

                NativeImage image = new NativeImage(content.getOriginalImage().format(), content.getOriginalImage().getWidth(), content.getOriginalImage().getHeight(), true);
                image.copyFrom(content.getOriginalImage());
                remapCheesedImage(image);

                SpriteContents cheesedContent = new SpriteContents(
                        content.name().withSuffix("_cheesed"),
                        new FrameSize(content.width(), content.height()),
                        image,
                        AnimationMetadataSection.EMPTY,
                        content.forgeMeta
                );
                cheesedContent.animatedTexture = content.animatedTexture;

                replacedContents.add(cheesedContent);
            }

            return replacedContents;
        }

        return contents;
    }

    private float getAverageGrayScale(NativeImage image) {
        float totalGray = 0f;
        int validCount = 0;

        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int originalColor = image.getPixelRGBA(x, y);

                int blue = FastColor.ARGB32.blue(originalColor);
                int green = FastColor.ARGB32.green(originalColor);
                int red = FastColor.ARGB32.red(originalColor);

                if (FastColor.ARGB32.alpha(originalColor) != 0) {
                    totalGray += (red * 0.299f + green * 0.587f + blue * 0.144f) / 255f;
                    validCount ++;
                }
            }
        }

        return totalGray / validCount;
    }

    private void remapCheesedImage(NativeImage image) {
        float amplifier = 0.65f / getAverageGrayScale(image);

        RandomSource source = RandomSource.create();
        source.setSeed(42L);

        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y ++) {
                int originalColor = image.getPixelRGBA(x, y);

                int alpha = FastColor.ARGB32.alpha(originalColor);
                int blue = FastColor.ARGB32.blue(originalColor);
                int green = FastColor.ARGB32.green(originalColor);
                int red = FastColor.ARGB32.red(originalColor);

                float gray =  Math.min(1f, (red * 0.299f + green * 0.587f + blue * 0.144f) / 255f * amplifier + (float) source.nextGaussian() * 0.12f);
                int finalAlpha = (int) (alpha *  sigmoid(((image.getHeight() - 2f * y) / image.getHeight()) * 10f));

                blendPixel(image, x, y, FastColor.ARGB32.color(
                        finalAlpha,
                        0,
                        (int) (170 + gray * 55),
                        (int) (220 + gray * 35)
                ));

                //芝士 0, 170+55, 220+35
                //咖喱 98, 203, 250
            }
        }
    }

    public void blendPixel(NativeImage image, int x, int y, int colorToBlend) {
        int pixelColor = image.getPixelRGBA(x, y);

        float toBlendA = (float)FastColor.ABGR32.alpha(colorToBlend) / 255.0F;
        float toBlendB = (float)FastColor.ABGR32.blue(colorToBlend) / 255.0F;
        float toBlendG = (float)FastColor.ABGR32.green(colorToBlend) / 255.0F;
        float toBlendR = (float)FastColor.ABGR32.red(colorToBlend) / 255.0F;

        float pixelB = (float)FastColor.ABGR32.blue(pixelColor) / 255.0F;
        float pixelG = (float)FastColor.ABGR32.green(pixelColor) / 255.0F;
        float pixelR = (float)FastColor.ABGR32.red(pixelColor) / 255.0F;

        float transparency = 1.0F - toBlendA;

        int b = (int) (Math.min(1f, toBlendB * toBlendA + pixelB * transparency) * 255.0F);
        int g = (int) (Math.min(1f, toBlendG * toBlendA + pixelG * transparency) * 255.0F);
        int r = (int) (Math.min(1f, toBlendR * toBlendA + pixelR * transparency) * 255.0F);

        image.setPixelRGBA(x, y, FastColor.ABGR32.color(FastColor.ABGR32.alpha(pixelColor), b, g, r));
    }

    private float sigmoid(float x) {
        return 1f / (1f + (float) Math.exp(-x));
    }
}
