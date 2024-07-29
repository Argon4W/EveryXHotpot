package com.github.argon4w.hotpot.client.items.process.processors;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.items.components.HotpotSoupDataComponent;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

public abstract class AbstractHotpotHalfSaucedSpriteProcessor implements IHotpotSpriteProcessor {
    @Override
    public void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame) {
        float amplifier = 0.65f / getAverageGrayScale(original, frameSize, frame);

        RandomSource source = RandomSource.create();
        source.setSeed(42L);

        for (int x = 0; x < frameSize.width(); x ++) {
            for (int y = 0; y < frameSize.height(); y ++) {
                int originalColor = original.getPixelRGBA(x, y + frame * frameSize.height());

                int alpha = FastColor.ABGR32.alpha(originalColor);
                int blue = FastColor.ABGR32.blue(originalColor);
                int green = FastColor.ABGR32.green(originalColor);
                int red = FastColor.ABGR32.red(originalColor);

                float gray =  Math.min(1f, (red * 0.299f + green * 0.587f + blue * 0.144f) / 255f * amplifier + (float) source.nextGaussian() * 0.12f);
                int finalAlpha = (int) (alpha * sigmoid(((frameSize.height() - 2f * y) / frameSize.height()) * 10f) * getAlphaModifier());

                image.setPixelRGBA(x, y + frame * frameSize.height(), FastColor.ABGR32.color(
                        finalAlpha,
                        (int) (200 + gray * 55),
                        (int) (200 + gray * 55),
                        (int) (200 + gray * 55)
                ));
            }
        }
    }

    @Override
    public int getColor(ItemStack itemStack) {
        if (!HotpotSoupDataComponent.hasDataComponent(itemStack)) {
            return -1;
        }

        HotpotSoupRendererConfig rendererConfig = HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.getSoupRendererConfig(HotpotSoupDataComponent.getDataComponent(itemStack).soupTypeFactory().key());

        if (rendererConfig.color().isEmpty()) {
            return -1;
        }

        return rendererConfig.color().get().toInt();
    }

    private float getAverageGrayScale(NativeImage image, FrameSize frameSize, int frame) {
        float totalGray = 0f;
        int validCount = 0;

        for (int x = 0; x < frameSize.width(); x ++) {
            for (int y = 0; y < frameSize.height(); y++) {
                int originalColor = image.getPixelRGBA(x, y + frame * frameSize.height());

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

    private float sigmoid(float x) {
        return 1f / (1f + (float) Math.exp(-x));
    }

    public abstract float getAlphaModifier();
}
