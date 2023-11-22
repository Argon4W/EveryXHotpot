package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.DummyCheesedResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Shadow @Final @Deprecated public static ResourceLocation LOCATION_BLOCKS;

    @Shadow protected abstract ResourceLocation getResourceLocation(ResourceLocation pLocation);

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "getBasicSpriteInfos", at = @At("RETURN"), cancellable = true)
    public void getBasicSpriteInfos(ResourceManager pResourceManager, Set<ResourceLocation> pSpriteLocations, CallbackInfoReturnable<Collection<TextureAtlasSprite.Info>> cir) {
        ArrayList<TextureAtlasSprite.Info> extendedInfos = new ArrayList<>(cir.getReturnValue());

        cir.getReturnValue().stream()
                .filter(info -> info.name().getPath().contains("item/"))
                .forEach(info -> extendedInfos.add(new TextureAtlasSprite.Info(new DummyCheesedResourceLocation(info.name(), info.name().getNamespace(), info.name().getPath().concat("_cheesed")), info.width(), info.height(), info.metadata)));

        cir.setReturnValue(extendedInfos);
    }

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
    public void load(ResourceManager pResourceManager, TextureAtlasSprite.Info pSpriteInfo, int pWidth, int pHeight, int pMipmapLevel, int pOriginX, int pOriginY, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (pSpriteInfo.name() instanceof DummyCheesedResourceLocation) {
            ResourceLocation resourcelocation = this.getResourceLocation(((DummyCheesedResourceLocation) pSpriteInfo.name()).getOriginalTextureLocation());
            Optional<Resource> optional = pResourceManager.getResource(resourcelocation);

            try {
                if (optional.isEmpty()) {
                    return;
                }

                Resource resource = optional.get();

                NativeImage nativeImage = NativeImage.read(resource.open());
                remapCheesedImage(nativeImage);

                cir.setReturnValue(new TextureAtlasSprite((TextureAtlas) (Object) this, pSpriteInfo, pMipmapLevel, pWidth, pHeight, pOriginX, pOriginY, nativeImage));
            } catch (RuntimeException runtimeexception) {
                LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                cir.setReturnValue(null);
            } catch (IOException ioexception) {
                LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
                cir.setReturnValue(null);
            }
        }
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
                        (int) (220 + gray * 35),
                        (int) (170 + gray * 55),
                        0
                ));

                //芝士 0, 170+55, 220+35
                //咖喱 98, 203, 250
            }
        }
    }

    public void blendPixel(NativeImage image, int x, int y, int colorToBlend) {
        int pixelColor = image.getPixelRGBA(x, y);

        float toBlendA = (float)FastColor.ARGB32.alpha(colorToBlend) / 255.0F;
        float toBlendB = (float)FastColor.ARGB32.blue(colorToBlend) / 255.0F;
        float toBlendG = (float)FastColor.ARGB32.green(colorToBlend) / 255.0F;
        float toBlendR = (float)FastColor.ARGB32.red(colorToBlend) / 255.0F;

        float pixelR = (float)FastColor.ARGB32.blue(pixelColor) / 255.0F;
        float pixelG = (float)FastColor.ARGB32.green(pixelColor) / 255.0F;
        float pixelB = (float)FastColor.ARGB32.red(pixelColor) / 255.0F;

        float transparency = 1.0F - toBlendA;

        int b = (int) (Math.min(1f, toBlendB * toBlendA + pixelB * transparency) * 255.0F);
        int g = (int) (Math.min(1f, toBlendG * toBlendA + pixelG * transparency) * 255.0F);
        int r = (int) (Math.min(1f, toBlendR * toBlendA + pixelR * transparency) * 255.0F);

        image.setPixelRGBA(x, y, FastColor.ARGB32.color(FastColor.ARGB32.alpha(pixelColor), b, g, r));
    }

    private float sigmoid(float x) {
        return 1f / (1f + (float) Math.exp(-x));
    }
}
