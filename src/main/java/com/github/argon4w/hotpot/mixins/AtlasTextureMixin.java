package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.DummyCheesedResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(AtlasTexture.class)
public abstract class AtlasTextureMixin {
    @Shadow @Final @Deprecated public static ResourceLocation LOCATION_BLOCKS;

    @Shadow protected abstract ResourceLocation getResourceLocation(ResourceLocation pLocation);

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "getBasicSpriteInfos", at = @At("RETURN"), cancellable = true)
    public void getBasicSpriteInfos(IResourceManager pResourceManager, Set<ResourceLocation> pSpriteLocations, CallbackInfoReturnable<Collection<TextureAtlasSprite.Info>> cir) {
        ArrayList<TextureAtlasSprite.Info> extendedInfos = new ArrayList<>(cir.getReturnValue());

        cir.getReturnValue().stream()
                .filter(info -> info.name().getPath().contains("item/"))
                .forEach(info -> extendedInfos.add(new TextureAtlasSprite.Info(new DummyCheesedResourceLocation(info.name(), info.name().getNamespace(), info.name().getPath().concat("_cheesed")), info.width(), info.height(), info.metadata)));

        cir.setReturnValue(extendedInfos);
    }

    @Inject(method = "load(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", at = @At("HEAD"), cancellable = true)
    public void load(IResourceManager pResourceManager, TextureAtlasSprite.Info pSpriteInfo, int pWidth, int pHeight, int pMipmapLevel, int pOriginX, int pOriginY, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (pSpriteInfo.name() instanceof DummyCheesedResourceLocation) {
            ResourceLocation resourcelocation = this.getResourceLocation(((DummyCheesedResourceLocation) pSpriteInfo.name()).getOriginalTextureLocation());
            try (IResource iresource = pResourceManager.getResource(resourcelocation)) {
                NativeImage nativeImage = NativeImage.read(iresource.getInputStream());
                remapCheesedImage(nativeImage);

                cir.setReturnValue(new TextureAtlasSprite((AtlasTexture) (Object) this, pSpriteInfo, pMipmapLevel, pWidth, pHeight, pOriginX, pOriginY, nativeImage));
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

                int blue = ColorHelper.PackedColor.blue(originalColor);
                int green = ColorHelper.PackedColor.green(originalColor);
                int red = ColorHelper.PackedColor.red(originalColor);

                if (ColorHelper.PackedColor.alpha(originalColor) != 0) {
                    totalGray += (red * 0.299f + green * 0.587f + blue * 0.144f) / 255f;
                    validCount ++;
                }
            }
        }

        return totalGray / validCount;
    }

    private void remapCheesedImage(NativeImage image) {
        float amplifier = 0.65f / getAverageGrayScale(image);

        Random source = new Random();
        source.setSeed(42L);

        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y ++) {
                int originalColor = image.getPixelRGBA(x, y);

                int alpha = ColorHelper.PackedColor.alpha(originalColor);
                int blue = ColorHelper.PackedColor.blue(originalColor);
                int green = ColorHelper.PackedColor.green(originalColor);
                int red = ColorHelper.PackedColor.red(originalColor);

                float gray =  Math.min(1f, (red * 0.299f + green * 0.587f + blue * 0.144f) / 255f * amplifier + (float) source.nextGaussian() * 0.12f);
                int finalAlpha = (int) (alpha *  sigmoid(((image.getHeight() - 2f * y) / image.getHeight()) * 10f));

                blendPixel(image, x, y, ColorHelper.PackedColor.color(
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

        float toBlendA = (float)ColorHelper.PackedColor.alpha(colorToBlend) / 255.0F;
        float toBlendB = (float)ColorHelper.PackedColor.blue(colorToBlend) / 255.0F;
        float toBlendG = (float)ColorHelper.PackedColor.green(colorToBlend) / 255.0F;
        float toBlendR = (float)ColorHelper.PackedColor.red(colorToBlend) / 255.0F;

        float pixelB = (float)ColorHelper.PackedColor.blue(pixelColor) / 255.0F;
        float pixelG = (float)ColorHelper.PackedColor.green(pixelColor) / 255.0F;
        float pixelR = (float)ColorHelper.PackedColor.red(pixelColor) / 255.0F;

        float transparency = 1.0F - toBlendA;

        int b = (int) (Math.min(1f, toBlendB * toBlendA + pixelB * transparency) * 255.0F);
        int g = (int) (Math.min(1f, toBlendG * toBlendA + pixelG * transparency) * 255.0F);
        int r = (int) (Math.min(1f, toBlendR * toBlendA + pixelR * transparency) * 255.0F);

        image.setPixelRGBA(x, y, ColorHelper.PackedColor.color(ColorHelper.PackedColor.alpha(pixelColor), r, g, b));
    }

    private float sigmoid(float x) {
        return 1f / (1f + (float) Math.exp(-x));
    }
}
